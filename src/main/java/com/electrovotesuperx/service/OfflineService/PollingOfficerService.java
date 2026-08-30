package com.electrovotesuperx.service.OfflineService;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.config.firebaseConfig.FirebaseConfig;
import com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class PollingOfficerService {

    public static final String CHIEF_APPROVER_EMAIL = "ravi.parkhe2006@gmail.com";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final SecureRandom RANDOM = new SecureRandom();

    public enum OfficerApprovalStatus {
        APPROVED,
        PENDING,
        REJECTED,
        NOT_FOUND
    }

    public static class OfficerRegistrationResult {
        private final boolean success;
        private final String message;
        private final String uid;
        private final String approvalPin;

        public OfficerRegistrationResult(boolean success, String message, String uid, String approvalPin) {
            this.success = success;
            this.message = message;
            this.uid = uid;
            this.approvalPin = approvalPin;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getUid() {
            return uid;
        }

        public String getApprovalPin() {
            return approvalPin;
        }
    }

    // =========================================================
    // 1. REGISTER POLLING OFFICER
    // =========================================================

    public static OfficerRegistrationResult registerOfficer(
            String name,
            String email,
            String password,
            String stationName,
            String phone) {

        System.out.println("[PollingOfficerService] Starting registration for: " + email);

        try {
            // Step 1: Create Firebase Auth Account
            FirebaseAuthService.AuthResult auth = FirebaseAuthService.createUser(email, password);
            System.out.println("[PollingOfficerService] CreateUser result success: " + auth.isSuccess() + ", message: " + auth.getMessage());

            String uid;
            String idToken;

            if (auth.isSuccess()) {
                uid = auth.getLocalId();
                idToken = auth.getIdToken();
            } else if (auth.getMessage() != null && auth.getMessage().contains("EMAIL_EXISTS")) {
                // If account exists, attempt sign in to get tokens
                System.out.println("[PollingOfficerService] Email exists, attempting signIn with provided password...");
                FirebaseAuthService.AuthResult signIn = FirebaseAuthService.signIn(email, password);
                if (!signIn.isSuccess()) {
                    return new OfficerRegistrationResult(false, "An account with this email already exists with a different password. Please check your credentials.", null, null);
                }
                uid = signIn.getLocalId();
                idToken = signIn.getIdToken();
            } else {
                return new OfficerRegistrationResult(false, auth.getMessage(), null, null);
            }

            // Generate 6-digit Secret Approval PIN
            int pinNumber = 100_000 + RANDOM.nextInt(900_000);
            String approvalPin = String.valueOf(pinNumber);

            System.out.println("[PollingOfficerService] Got UID: " + uid + ", Approval PIN: " + approvalPin);

            // Step 2: Save Polling Officer Profile in Firestore (status: PENDING)
            FirestoreDAO.savePollingOfficer(
                    uid,
                    name,
                    email,
                    stationName,
                    phone,
                    "PENDING",
                    CHIEF_APPROVER_EMAIL,
                    approvalPin,
                    idToken);

            // Step 3: Also record in Realtime Database for real-time syncing
            saveToRealtimeDb(uid, name, email, stationName, phone, "PENDING", approvalPin, idToken);

            // Step 4: Dispatch Email with Secret Approval PIN to ravi.parkhe2006@gmail.com
            EmailService.sendApprovalPinEmail(
                    uid,
                    CHIEF_APPROVER_EMAIL,
                    name,
                    email,
                    stationName,
                    phone,
                    approvalPin,
                    idToken);

            System.out.println("[PollingOfficerService] Registration complete and PIN emailed to " + CHIEF_APPROVER_EMAIL);

            return new OfficerRegistrationResult(true,
                    "Registration submitted! Secret Approval PIN has been sent directly to " + CHIEF_APPROVER_EMAIL + ".",
                    uid,
                    approvalPin);

        } catch (Exception e) {
            System.err.println("[PollingOfficerService] Registration error: " + e.getMessage());
            e.printStackTrace();
            return new OfficerRegistrationResult(false, "Registration failed: " + e.getMessage(), null, null);
        }
    }

    // =========================================================
    // 2. CHECK POLLING OFFICER APPROVAL STATUS
    // =========================================================

    public static OfficerApprovalStatus checkOfficerApprovalStatus(
            String uid,
            String email,
            String idToken) {

        try {
            // Check Firestore first
            JsonObject fields = FirestoreDAO.getPollingOfficer(uid, idToken);
            if (fields != null) {
                String status = FirestoreDAO.getString(fields, "status");
                if (status != null) {
                    if ("APPROVED".equalsIgnoreCase(status) || "ACCEPTED".equalsIgnoreCase(status)) {
                        populateOfficerSession(fields, uid, email, idToken);
                        return OfficerApprovalStatus.APPROVED;
                    } else if ("REJECTED".equalsIgnoreCase(status)) {
                        return OfficerApprovalStatus.REJECTED;
                    } else {
                        return OfficerApprovalStatus.PENDING;
                    }
                }
            }

            // Fallback: Check Realtime Database
            String rtdbStatus = checkRealtimeDbStatus(uid, idToken);
            if (rtdbStatus != null) {
                if ("APPROVED".equalsIgnoreCase(rtdbStatus) || "ACCEPTED".equalsIgnoreCase(rtdbStatus)) {
                    SessionManager.idToken = idToken;
                    SessionManager.currentRole = "polling_officer";
                    SessionManager.officerUid = uid;
                    SessionManager.officerEmail = email;
                    return OfficerApprovalStatus.APPROVED;
                } else if ("REJECTED".equalsIgnoreCase(rtdbStatus)) {
                    return OfficerApprovalStatus.REJECTED;
                } else {
                    return OfficerApprovalStatus.PENDING;
                }
            }

            return OfficerApprovalStatus.NOT_FOUND;

        } catch (Exception e) {
            System.err.println("[PollingOfficerService] Status check error: " + e.getMessage());
            return OfficerApprovalStatus.PENDING;
        }
    }

    // =========================================================
    // 3. VERIFY & APPROVE WITH 6-DIGIT PIN
    // =========================================================

    public static boolean verifyAndApproveWithPin(
            String uid,
            String email,
            String inputPin,
            String idToken) {

        if (inputPin == null || inputPin.isBlank()) {
            return false;
        }

        try {
            String trimmedInput = inputPin.trim();

            // 1. Check Firestore
            JsonObject fields = FirestoreDAO.getPollingOfficer(uid, idToken);
            if (fields != null) {
                String storedPin = FirestoreDAO.getString(fields, "approvalPin");
                if (storedPin != null && storedPin.equals(trimmedInput)) {
                    // PIN MATCHES! Set to APPROVED
                    FirestoreDAO.updateOfficerStatus(uid, "APPROVED", idToken);
                    updateRealtimeDbStatus(uid, "APPROVED", idToken);
                    populateOfficerSession(fields, uid, email, idToken);
                    return true;
                }
            }

            // 2. Fallback check Realtime Database
            String storedRtdbPin = getRealtimeDbPin(uid, idToken);
            if (storedRtdbPin != null && storedRtdbPin.equals(trimmedInput)) {
                FirestoreDAO.updateOfficerStatus(uid, "APPROVED", idToken);
                updateRealtimeDbStatus(uid, "APPROVED", idToken);
                SessionManager.idToken = idToken;
                SessionManager.currentRole = "polling_officer";
                SessionManager.officerUid = uid;
                SessionManager.officerEmail = email;
                return true;
            }

        } catch (Exception e) {
            System.err.println("[PollingOfficerService] PIN verification error: " + e.getMessage());
        }

        return false;
    }

    // =========================================================
    // HELPER: POPULATE SESSION
    // =========================================================

    private static void populateOfficerSession(JsonObject fields, String uid, String email, String idToken) {
        String name = FirestoreDAO.getString(fields, "name");
        String station = FirestoreDAO.getString(fields, "stationName");

        SessionManager.idToken = idToken;
        SessionManager.currentRole = "polling_officer";
        SessionManager.officerUid = uid;
        SessionManager.officerEmail = email;
        SessionManager.officerName = name != null ? name : "Polling Officer";
        SessionManager.officerStation = station != null ? station : "Main Station";
    }

    // =========================================================
    // HELPER: RTDB WRITE & READ
    // =========================================================

    private static void saveToRealtimeDb(
            String uid, String name, String email, String station, String phone, String status, String approvalPin, String idToken) {
        try {
            String url = FirebaseConfig.DATABASE_URL + "/polling_officer_requests/" + uid + ".json?auth=" + idToken;
            JsonObject obj = new JsonObject();
            obj.addProperty("uid", uid);
            obj.addProperty("name", name);
            obj.addProperty("email", email);
            obj.addProperty("stationName", station);
            obj.addProperty("phone", phone);
            obj.addProperty("status", status);
            obj.addProperty("approverEmail", CHIEF_APPROVER_EMAIL);
            obj.addProperty("approvalPin", approvalPin);
            obj.addProperty("createdAt", java.time.Instant.now().toString());

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(obj.toString(), StandardCharsets.UTF_8))
                    .build();

            CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
        }
    }

    private static void updateRealtimeDbStatus(String uid, String status, String idToken) {
        try {
            String url = FirebaseConfig.DATABASE_URL + "/polling_officer_requests/" + uid + "/status.json?auth=" + idToken;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString("\"" + status + "\"", StandardCharsets.UTF_8))
                    .build();

            CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ignored) {
        }
    }

    private static String checkRealtimeDbStatus(String uid, String idToken) {
        try {
            String url = FirebaseConfig.DATABASE_URL + "/polling_officer_requests/" + uid + ".json?auth=" + idToken;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200 && res.body() != null && !res.body().equals("null")) {
                JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
                if (json.has("status")) {
                    return json.get("status").getAsString();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static String getRealtimeDbPin(String uid, String idToken) {
        try {
            String url = FirebaseConfig.DATABASE_URL + "/polling_officer_requests/" + uid + ".json?auth=" + idToken;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() == 200 && res.body() != null && !res.body().equals("null")) {
                JsonObject json = JsonParser.parseString(res.body()).getAsJsonObject();
                if (json.has("approvalPin")) {
                    return json.get("approvalPin").getAsString();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
