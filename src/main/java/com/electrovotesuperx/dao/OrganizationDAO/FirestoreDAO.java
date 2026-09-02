package com.electrovotesuperx.dao.OrganizationDAO;

import com.electrovotesuperx.config.firebaseConfig.FirebaseConfig;
import com.electrovotesuperx.exception.FirestoreException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class FirestoreDAO {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String PROJECT_ID = FirebaseConfig.PROJECT_ID;

    // =====================================================
    // SAVE ORGANIZATION
    // =====================================================

    public static boolean saveOrganization(
            String joinCode,
            String organizationName,
            String adminName,
            String adminEmail,
            String adminUid,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Organizations/"
                + joinCode;

        JsonObject fields = new JsonObject();
        fields.add("organizationName", stringValue(organizationName));
        fields.add("joinCode", stringValue(joinCode));
        fields.add("adminName", stringValue(adminName));
        fields.add("adminEmail", stringValue(adminEmail));
        fields.add("adminUid", stringValue(adminUid));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH",
                        HttpRequest.BodyPublishers.ofString(body.toString(),
                                StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to save organization.", e);
        }

        return response.statusCode() == 200;
    }

    // =====================================================
    // SAVE USER (ADMIN / VOTER)
    // =====================================================

    public static boolean saveUser(
            String uid,
            String name,
            String email,
            String role,
            String joinCode,
            String idToken) throws FirestoreException {
        String status = "ADMIN".equalsIgnoreCase(role) ? "ACTIVE" : "PENDING";
        return saveUser(uid, name, email, role, joinCode, status, idToken);
    }

    public static boolean saveUser(
            String uid,
            String name,
            String email,
            String role,
            String joinCode,
            String status,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Users/"
                + uid;

        JsonObject fields = new JsonObject();
        fields.add("uid", stringValue(uid));
        fields.add("name", stringValue(name));
        fields.add("email", stringValue(email));
        fields.add("role", stringValue(role));
        fields.add("organization", stringValue(joinCode));
        fields.add("status", stringValue(status != null ? status : "PENDING"));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH",
                        HttpRequest.BodyPublishers.ofString(body.toString(),
                                StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to save user.", e);
        }

        return response.statusCode() == 200;
    }

    // =====================================================
    // GET ORGANIZATION
    // =====================================================

    public static JsonObject getOrganization(
            String joinCode,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Organizations/"
                + joinCode;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        if (idToken != null && !idToken.isBlank()) {
            builder.header("Authorization", "Bearer " + idToken);
        }

        HttpRequest request = builder.build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve organization.", e);
        }

        if (response.statusCode() != 200) {
            return null;
        }

        return JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("fields");
    }

    // =====================================================
    // GET ALL ORGANIZATIONS (FIRESTORE)
    // =====================================================

    public static JsonObject getOrganizations(
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Organizations?pageSize=300";

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        if (idToken != null && !idToken.isBlank()) {
            builder.header("Authorization", "Bearer " + idToken);
        }

        HttpRequest request = builder.build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve organizations.", e);
        }

        if (response.statusCode() != 200) {
            return null;
        }

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    // =====================================================
    // FIND ORGANIZATION BY ADMIN (UID OR EMAIL)
    // =====================================================

    public static JsonObject findOrganizationByAdmin(
            String uid,
            String email,
            String idToken) {

        try {
            JsonObject orgsResp = getOrganizations(idToken);
            if (orgsResp == null && idToken != null) {
                // Retry without token if auth token had scope issues
                orgsResp = getOrganizations(null);
            }

            if (orgsResp != null && orgsResp.has("documents") && orgsResp.get("documents").isJsonArray()) {
                com.google.gson.JsonArray docs = orgsResp.getAsJsonArray("documents");
                for (com.google.gson.JsonElement elem : docs) {
                    if (!elem.isJsonObject()) continue;
                    JsonObject doc = elem.getAsJsonObject();
                    if (!doc.has("fields")) continue;
                    JsonObject fields = doc.getAsJsonObject("fields");

                    String adminUid = getString(fields, "adminUid");
                    String adminEmail = getString(fields, "adminEmail");

                    if ((uid != null && !uid.isBlank() && uid.equals(adminUid))
                            || (email != null && !email.isBlank() && email.equalsIgnoreCase(adminEmail))) {
                        
                        // Ensure joinCode is present in fields
                        if (!fields.has("joinCode") || getString(fields, "joinCode") == null) {
                            String docName = doc.has("name") ? doc.get("name").getAsString() : "";
                            if (docName.contains("/")) {
                                String extractedCode = docName.substring(docName.lastIndexOf('/') + 1);
                                fields.add("joinCode", stringValue(extractedCode));
                            }
                        }
                        return fields;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[FirestoreDAO] findOrganizationByAdmin note: " + e.getMessage());
        }
        return null;
    }

    // =====================================================
    // GET USER
    // =====================================================

    public static JsonObject getUser(
            String uid,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Users/"
                + uid;

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        if (idToken != null && !idToken.isBlank()) {
            builder.header("Authorization", "Bearer " + idToken);
        }

        HttpRequest request = builder.build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve user.", e);
        }

        if (response.statusCode() != 200) {
            return null;
        }

        return JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("fields");
    }

    // =====================================================
    // SAVE POLLING OFFICER
    // =====================================================

    public static boolean savePollingOfficer(
            String uid,
            String name,
            String email,
            String stationName,
            String phone,
            String status,
            String approverEmail,
            String approvalPin,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/PollingOfficers/"
                + uid;

        JsonObject fields = new JsonObject();
        fields.add("uid", stringValue(uid));
        fields.add("name", stringValue(name));
        fields.add("email", stringValue(email));
        fields.add("stationName", stringValue(stationName));
        fields.add("phone", stringValue(phone));
        fields.add("role", stringValue("POLLING_OFFICER"));
        fields.add("status", stringValue(status));
        fields.add("approverEmail", stringValue(approverEmail));
        fields.add("approvalPin", stringValue(approvalPin));
        fields.add("createdAt", stringValue(java.time.Instant.now().toString()));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH",
                        HttpRequest.BodyPublishers.ofString(body.toString(),
                                StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[FirestoreDAO] PollingOfficers write status: " + response.statusCode() + " response: " + response.body());

            // Also mirror to Users/{uid}
            String usersUrl = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID
                    + "/databases/(default)/documents/Users/"
                    + uid;
            HttpRequest userReq = HttpRequest.newBuilder()
                    .uri(URI.create(usersUrl))
                    .header("Authorization", "Bearer " + idToken)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();
            CLIENT.send(userReq, HttpResponse.BodyHandlers.ofString());

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to save polling officer.", e);
        }
    }

    // =====================================================
    // UPDATE POLLING OFFICER STATUS
    // =====================================================

    public static boolean updateOfficerStatus(
            String uid,
            String status,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/PollingOfficers/"
                + uid
                + "?updateMask.fieldPaths=status";

        JsonObject fields = new JsonObject();
        fields.add("status", stringValue(status));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("[FirestoreDAO] UpdateOfficerStatus response: " + response.statusCode());

            // Also mirror status and role to Users/{uid}
            try {
                String usersUrl = "https://firestore.googleapis.com/v1/projects/"
                        + PROJECT_ID
                        + "/databases/(default)/documents/Users/"
                        + uid
                        + "?updateMask.fieldPaths=status&updateMask.fieldPaths=role";
                JsonObject uFields = new JsonObject();
                uFields.add("status", stringValue(status));
                uFields.add("role", stringValue("POLLING_OFFICER"));
                JsonObject uBody = new JsonObject();
                uBody.add("fields", uFields);

                HttpRequest userReq = HttpRequest.newBuilder()
                        .uri(URI.create(usersUrl))
                        .header("Authorization", "Bearer " + idToken)
                        .header("Content-Type", "application/json")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(uBody.toString(), StandardCharsets.UTF_8))
                        .build();
                CLIENT.send(userReq, HttpResponse.BodyHandlers.ofString());
            } catch (Exception ignored) {}

            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            throw new FirestoreException("Unable to update officer status.", e);
        }
    }

    // =====================================================
    // GET POLLING OFFICER
    // =====================================================

    public static JsonObject getPollingOfficer(
            String uid,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/PollingOfficers/"
                + uid;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve polling officer.", e);
        }

        if (response.statusCode() != 200) {
            return null;
        }

        return JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("fields");
    }

    // =====================================================
    // QUEUE APPROVAL EMAIL (FIRESTORE MAIL TRIGGER)
    // =====================================================

    public static boolean queueApprovalEmail(
            String toEmail,
            String officerName,
            String officerEmail,
            String stationName,
            String officerPhone,
            String idToken) {

        try {
            String url = "https://firestore.googleapis.com/v1/projects/"
                    + PROJECT_ID
                    + "/databases/(default)/documents/mail";

            JsonObject fields = new JsonObject();

            // to: ["ravi.parkhe2006@gmail.com"]
            JsonObject arrayVal = new JsonObject();
            com.google.gson.JsonArray values = new com.google.gson.JsonArray();
            values.add(stringValue(toEmail));
            arrayVal.add("values", values);
            JsonObject toField = new JsonObject();
            toField.add("arrayValue", arrayVal);
            fields.add("to", toField);

            // message: { subject: "...", text: "...", html: "..." }
            JsonObject msgMap = new JsonObject();
            JsonObject msgFields = new JsonObject();
            msgFields.add("subject", stringValue("ElectraVote: New Polling Officer Approval Request"));
            msgFields.add("text", stringValue(
                    "A new Polling Officer has registered for the Offline Voting System and is awaiting your approval.\n\n"
                            + "Officer Name   : " + officerName + "\n"
                            + "Officer Email  : " + officerEmail + "\n"
                            + "Station Name   : " + stationName + "\n"
                            + "Phone Number   : " + officerPhone + "\n\n"
                            + "Please approve this officer in your ElectraVote Admin / Firebase Console to grant Offline Voting access."));
            msgFields.add("html", stringValue(
                    "<div style='font-family: Arial, sans-serif; color: #1e293b;'>"
                            + "<h2 style='color: #059669;'>ElectraVote Polling Officer Approval Request</h2>"
                            + "<p>A new Polling Officer has registered for the Offline Voting System and is awaiting your approval:</p>"
                            + "<table style='border-collapse: collapse; margin: 15px 0;'>"
                            + "<tr><td style='padding: 6px 12px; font-weight: bold;'>Officer Name:</td><td style='padding: 6px 12px;'>" + officerName + "</td></tr>"
                            + "<tr><td style='padding: 6px 12px; font-weight: bold;'>Officer Email:</td><td style='padding: 6px 12px;'>" + officerEmail + "</td></tr>"
                            + "<tr><td style='padding: 6px 12px; font-weight: bold;'>Station Name:</td><td style='padding: 6px 12px;'>" + stationName + "</td></tr>"
                            + "<tr><td style='padding: 6px 12px; font-weight: bold;'>Phone:</td><td style='padding: 6px 12px;'>" + officerPhone + "</td></tr>"
                            + "</table>"
                            + "<p>Please review and set status to <strong>APPROVED</strong> to grant offline terminal access.</p>"
                            + "</div>"));
            msgMap.add("fields", msgFields);
            JsonObject msgField = new JsonObject();
            msgField.add("mapValue", msgMap);
            fields.add("message", msgField);

            JsonObject body = new JsonObject();
            body.add("fields", fields);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 201;
        } catch (Exception e) {
            System.err.println("[FirestoreDAO] Error queueing email: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // READ STRING VALUE
    // =====================================================

    public static String getString(JsonObject fields, String key) {

        if (fields == null || !fields.has(key)) {
            return null;
        }

        return fields.getAsJsonObject(key)
                .get("stringValue")
                .getAsString();
    }

    // =====================================================
    // FIRESTORE STRING TYPE
    // =====================================================

    private static JsonObject stringValue(String value) {

        JsonObject obj = new JsonObject();
        obj.addProperty("stringValue", value);
        return obj;
    }
}