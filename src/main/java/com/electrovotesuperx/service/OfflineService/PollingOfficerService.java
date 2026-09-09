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
            } else if (auth.getMessage() != null && (auth.getMessage().contains("EMAIL_EXISTS") || auth.getMessage().toLowerCase().contains("already registered"))) {
                // If account exists, attempt sign in with provided password to authenticate
                System.out.println("[PollingOfficerService] Email exists, attempting signIn with provided password...");
                FirebaseAuthService.AuthResult signIn = FirebaseAuthService.signIn(email, password);
                if (!signIn.isSuccess()) {
                    if (SessionManager.idToken != null && !SessionManager.idToken.isBlank()
                            && SessionManager.loggedInEmail != null && SessionManager.loggedInEmail.equalsIgnoreCase(email)) {
                        uid = SessionManager.voterUid != null ? SessionManager.voterUid : (SessionManager.officerUid != null ? SessionManager.officerUid : (SessionManager.adminUid != null ? SessionManager.adminUid : signIn.getLocalId()));
                        idToken = SessionManager.idToken;
                    } else {
                        return new OfficerRegistrationResult(false, "An account with this email already exists. Please enter your existing password to submit your Polling Officer application.", null, null);
                    }
                } else {
                    uid = signIn.getLocalId();
                    idToken = signIn.getIdToken();
                }
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

            // Step 5: Also store in local SQLite database for offline admin approval
            try {
                com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO sqliteOfficerDAO = new com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO();
                sqliteOfficerDAO.saveRequest(uid, name, email, stationName, phone, "PENDING", approvalPin);
                System.out.println("[PollingOfficerService] Synced officer request into local SQLite database.");
            } catch (Exception localDbEx) {
                System.err.println("[PollingOfficerService] Local SQLite sync warning: " + localDbEx.getMessage());
            }

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
            // Check local SQLite first (primary for Offline Admin approval)
            try {
                com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO sqliteOfficerDAO = new com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO();
                String queryKey = (email != null && !email.isBlank()) ? email : uid;
                com.electrovotesuperx.model.OfflineModel.PollingOfficerRequest localReq = sqliteOfficerDAO.findByUidOrEmail(queryKey);
                if (localReq != null && localReq.getStatus() != null) {
                    String localStatus = localReq.getStatus();
                    if ("APPROVED".equalsIgnoreCase(localStatus) || "ACCEPTED".equalsIgnoreCase(localStatus)) {
                        SessionManager.idToken = idToken;
                        SessionManager.currentRole = "polling_officer";
                        SessionManager.officerUid = uid != null ? uid : localReq.getUid();
                        SessionManager.officerEmail = email != null ? email : localReq.getEmail();
                        SessionManager.officerName = localReq.getName() != null ? localReq.getName() : "Polling Officer";
                        SessionManager.officerStation = localReq.getStationName() != null ? localReq.getStationName() : "Main Station";
                        return OfficerApprovalStatus.APPROVED;
                    } else if ("REJECTED".equalsIgnoreCase(localStatus)) {
                        return OfficerApprovalStatus.REJECTED;
                    }
                }
            } catch (Exception localEx) {
                System.err.println("[PollingOfficerService] Local SQLite check notice: " + localEx.getMessage());
            }

            // Check Firestore
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

            // 1. Check local SQLite database first (with 8-hour validity check)
            com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO sqliteDao = new com.electrovotesuperx.dao.OfflineDAO.PollingOfficerDAO();
            String queryKey = (email != null && !email.isBlank()) ? email : uid;
            com.electrovotesuperx.model.OfflineModel.PollingOfficerRequest localReq = sqliteDao.findByUidOrEmail(queryKey);
            if (localReq != null && localReq.getApprovalPin() != null) {
                if (localReq.getApprovalPin().trim().equals(trimmedInput)) {
                    if (localReq.isPinExpired()) {
                        System.out.println("[PollingOfficerService] Local PIN expired (>8 hours) for " + queryKey);
                        return false;
                    }
                    sqliteDao.updateStatus(queryKey, "APPROVED");
                    SessionManager.idToken = idToken;
                    SessionManager.currentRole = "polling_officer";
                    SessionManager.officerUid = localReq.getUid() != null ? localReq.getUid() : uid;
                    SessionManager.officerEmail = localReq.getEmail() != null ? localReq.getEmail() : email;
                    SessionManager.officerName = localReq.getName() != null ? localReq.getName() : "Polling Officer";
                    SessionManager.officerStation = localReq.getStationName() != null ? localReq.getStationName() : "Main Station";
                    return true;
                }
            }

            // 2. Check Firestore
            JsonObject fields = FirestoreDAO.getPollingOfficer(uid, idToken);
            if (fields != null) {
                String storedPin = FirestoreDAO.getString(fields, "approvalPin");
                if (storedPin != null && storedPin.equals(trimmedInput)) {
                    FirestoreDAO.updateOfficerStatus(uid, "APPROVED", idToken);
                    updateRealtimeDbStatus(uid, "APPROVED", idToken);
                    sqliteDao.saveRequest(uid, FirestoreDAO.getString(fields, "name"), email,
                            FirestoreDAO.getString(fields, "stationName"),
                            FirestoreDAO.getString(fields, "phone"),
                            "APPROVED", trimmedInput);
                    populateOfficerSession(fields, uid, email, idToken);
                    return true;
                }
            }

            // 3. Fallback check Realtime Database
            String storedRtdbPin = getRealtimeDbPin(uid, idToken);
            if (storedRtdbPin != null && storedRtdbPin.equals(trimmedInput)) {
                FirestoreDAO.updateOfficerStatus(uid, "APPROVED", idToken);
                updateRealtimeDbStatus(uid, "APPROVED", idToken);
                sqliteDao.updateStatus(email != null ? email : uid, "APPROVED");
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

    private static String buildRtdbUrl(String path, String idToken) {
        String auth = (idToken != null && !idToken.isBlank()) 
                ? "?auth=" + java.net.URLEncoder.encode(idToken, StandardCharsets.UTF_8) 
                : "";
        return FirebaseConfig.DATABASE_URL + path + auth;
    }

    private static void saveToRealtimeDb(
            String uid, String name, String email, String station, String phone, String status, String approvalPin, String idToken) {
        try {
            String url = buildRtdbUrl("/polling_officer_requests/" + uid + ".json", idToken);
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

            HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("[PollingOfficerService] RealtimeDB save status: " + res.statusCode());
        } catch (Exception e) {
            System.err.println("[PollingOfficerService] RealtimeDB save error: " + e.getMessage());
        }
    }

    private static void updateRealtimeDbStatus(String uid, String status, String idToken) {
        try {
            String url = buildRtdbUrl("/polling_officer_requests/" + uid + "/status.json", idToken);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString("\"" + status + "\"", StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println("[PollingOfficerService] RealtimeDB update status: " + res.statusCode());
        } catch (Exception e) {
            System.err.println("[PollingOfficerService] RealtimeDB update error: " + e.getMessage());
        }
    }

    private static String checkRealtimeDbStatus(String uid, String idToken) {
        try {
            String url = buildRtdbUrl("/polling_officer_requests/" + uid + ".json", idToken);
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
        } catch (Exception e) {
            System.err.println("[PollingOfficerService] RealtimeDB check error: " + e.getMessage());
        }
        return null;
    }

    private static String getRealtimeDbPin(String uid, String idToken) {
        try {
            String url = buildRtdbUrl("/polling_officer_requests/" + uid + ".json", idToken);
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
        } catch (Exception e) {
            System.err.println("[PollingOfficerService] RealtimeDB PIN error: " + e.getMessage());
        }
        return null;
    }
}
