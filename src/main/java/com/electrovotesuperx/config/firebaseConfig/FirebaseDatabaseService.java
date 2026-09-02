package com.electrovotesuperx.config.firebaseConfig;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class FirebaseDatabaseService {

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    private static final Gson GSON =
            new Gson();

    private static final SecureRandom RANDOM =
            new SecureRandom();

    // =========================================================
    // GENERATE UNIQUE JOIN CODE
    // =========================================================

    public static String generateJoinCode() {

        String characters =
                "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

        StringBuilder code =
                new StringBuilder();

        code.append("EV-");

        for (int i = 0; i < 4; i++) {

            code.append(
                    characters.charAt(
                            RANDOM.nextInt(
                                    characters.length()
                            )
                    )
            );
        }

        code.append("-");

        for (int i = 0; i < 4; i++) {

            code.append(
                    characters.charAt(
                            RANDOM.nextInt(
                                    characters.length()
                            )
                    )
            );
        }

        return code.toString();
    }

    // =========================================================
    // SAVE ORGANIZATION
    // =========================================================

    public static boolean saveOrganization(
            String joinCode,
            String organizationName,
            String adminName,
            String adminEmail,
            String adminUid,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + ".json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + "?auth="
                        + encode(idToken);

        JsonObject organization =
                new JsonObject();

        organization.addProperty(
                "organizationName",
                organizationName
        );

        organization.addProperty(
                "joinCode",
                joinCode
        );

        organization.addProperty(
                "adminName",
                adminName
        );

        organization.addProperty(
                "adminEmail",
                adminEmail
        );

        organization.addProperty(
                "adminUid",
                adminUid
        );

        organization.addProperty(
                "createdAt",
                System.currentTimeMillis()
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers.ofString(
                                        GSON.toJson(organization)
                                )
                        )
                        .build();

        HttpResponse<String> response =
        CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

System.out.println("Status Code : " + response.statusCode());
System.out.println("Response    : " + response.body());

return response.statusCode() >= 200 &&
       response.statusCode() < 300;
    }

    // =========================================================
    // SAVE ADMIN MEMBERSHIP
    // =========================================================

    public static boolean saveAdminMembership(
            String joinCode,
            String uid,
            String name,
            String email,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members/"
                        + encode(uid)
                        + ".json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + "?auth="
                        + encode(idToken);

        JsonObject member =
                new JsonObject();

        member.addProperty(
                "uid",
                uid
        );

        member.addProperty(
                "name",
                name
        );

        member.addProperty(
                "email",
                email
        );

        member.addProperty(
                "role",
                "ADMIN"
        );

        member.addProperty(
                "status",
                "ACCEPTED"
        );

        member.addProperty(
                "joinedAt",
                System.currentTimeMillis()
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers.ofString(
                                        GSON.toJson(member)
                                )
                        )
                        .build();

        HttpResponse<String> response =
        CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

System.out.println("Status Code : " + response.statusCode());
System.out.println("Response    : " + response.body());

return response.statusCode() >= 200 &&
       response.statusCode() < 300;
    }

    // =========================================================
    // GET ORGANIZATION
    // =========================================================

    public static JsonObject getOrganization(
            String joinCode,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + ".json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + "?auth="
                        + encode(idToken);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            return null;
        }

        if (response.body() == null
                || response.body().equals("null")) {

            return null;
        }

        return JsonParser
                .parseString(response.body())
                .getAsJsonObject();
    }

    // =========================================================
    // GET MEMBER
    // =========================================================

    public static JsonObject getMember(
            String joinCode,
            String uid,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members/"
                        + encode(uid)
                        + ".json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + "?auth="
                        + encode(idToken);

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            return null;
        }

        if (response.body() == null
                || response.body().equals("null")) {

            return null;
        }

        return JsonParser
                .parseString(response.body())
                .getAsJsonObject();
    }

    // =========================================================
    // ADD VOTER
    // =========================================================

    public static boolean saveVoter(
            String joinCode,
            String uid,
            String name,
            String email,
            String idToken)
            throws IOException, InterruptedException {

        return saveVoter(
                joinCode,
                uid,
                "VOT" + String.format("%03d", (int)(Math.random() * 900) + 100),
                name,
                email,
                "",
                "Student",
                "General",
                "Member",
                idToken
        );
    }

    public static boolean saveVoter(
            String joinCode,
            String uid,
            String voterId,
            String name,
            String email,
            String phone,
            String category,
            String department,
            String yearOrRole,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members/"
                        + encode(uid)
                        + ".json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

        JsonObject voter =
                new JsonObject();

        voter.addProperty(
                "uid",
                uid
        );

        voter.addProperty(
                "voterId",
                voterId != null && !voterId.isBlank() ? voterId : ("VOT" + String.format("%03d", (int)(Math.random() * 900) + 100))
        );

        voter.addProperty(
                "name",
                name
        );

        voter.addProperty(
                "fullName",
                name
        );

        voter.addProperty(
                "email",
                email
        );

        voter.addProperty(
                "phone",
                phone != null ? phone : ""
        );

        voter.addProperty(
                "category",
                category != null && !category.isBlank() ? category : "Student"
        );

        voter.addProperty(
                "department",
                department != null && !department.isBlank() ? department : "General"
        );

        voter.addProperty(
                "yearOrRole",
                yearOrRole != null && !yearOrRole.isBlank() ? yearOrRole : "Member"
        );

        voter.addProperty(
                "role",
                "VOTER"
        );

        voter.addProperty(
                "status",
                "PENDING"
        );

        voter.addProperty(
                "joinedAt",
                System.currentTimeMillis()
        );

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy");
        voter.addProperty(
                "date",
                java.time.LocalDateTime.now().format(dtf)
        );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers.ofString(
                                        GSON.toJson(voter)
                                )
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        return response.statusCode() >= 200
                && response.statusCode() < 300;
    }

    // =========================================================
    // GET ALL MEMBERS
    // =========================================================

    public static JsonObject getAllMembers(
            String joinCode,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members.json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            return null;
        }

        if (response.body() == null
                || response.body().equals("null")) {

            return null;
        }

        return JsonParser
                .parseString(response.body())
                .getAsJsonObject();
    }

    // =========================================================
    // UPDATE MEMBER STATUS (PENDING, ACCEPTED, REJECTED)
    // =========================================================

    public static boolean updateMemberStatus(
            String joinCode,
            String uid,
            String status,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members/"
                        + encode(uid)
                        + "/status.json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers.ofString(
                                        "\"" + status + "\""
                                )
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            // Also mirror status to Firestore Users/{uid}
            try {
                if (idToken != null && !idToken.isBlank()) {
                    String usersUrl = "https://firestore.googleapis.com/v1/projects/"
                            + FirebaseConfig.PROJECT_ID
                            + "/databases/(default)/documents/Users/"
                            + uid
                            + "?updateMask.fieldPaths=status";
                    JsonObject uFields = new JsonObject();
                    JsonObject strVal = new JsonObject();
                    strVal.addProperty("stringValue", status);
                    uFields.add("status", strVal);
                    JsonObject uBody = new JsonObject();
                    uBody.add("fields", uFields);

                    HttpRequest userReq = HttpRequest.newBuilder()
                            .uri(URI.create(usersUrl))
                            .header("Authorization", "Bearer " + idToken)
                            .header("Content-Type", "application/json")
                            .method("PATCH", HttpRequest.BodyPublishers.ofString(uBody.toString(), StandardCharsets.UTF_8))
                            .build();
                    CLIENT.send(userReq, HttpResponse.BodyHandlers.ofString());
                }
            } catch (Exception ignored) {}
            return true;
        }

        return false;
    }

    // =========================================================
    // GET ALL MEMBERS
    // =========================================================

    public static JsonObject getMembers(
            String joinCode,
            String idToken)
            throws IOException, InterruptedException {

        String path =
                "/organizations/"
                        + encode(joinCode)
                        + "/members.json";

        String url =
                FirebaseConfig.DATABASE_URL
                        + path
                        + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return null;
        }

        if (response.body() == null || response.body().equals("null")) {
            return null;
        }

        return JsonParser
                .parseString(response.body())
                .getAsJsonObject();
    }

    // =========================================================
    // QUICK POLL CLOUD SERVICES (REALTIME DATABASE)
    // =========================================================

    public static boolean saveQuickPoll(String pollCode, JsonObject pollData, String idToken) {
        try {
            String path = "/quick_polls/" + encode(pollCode) + ".json";
            String url = FirebaseConfig.DATABASE_URL + path + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(pollData.toString()))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static JsonObject getQuickPolls(String idToken) {
        try {
            String path = "/quick_polls.json";
            String url = FirebaseConfig.DATABASE_URL + path + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300 || response.body() == null || response.body().equals("null")) {
                return null;
            }

            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonObject getQuickPollByCode(String pollCode, String idToken) {
        try {
            String path = "/quick_polls/" + encode(pollCode) + ".json";
            String url = FirebaseConfig.DATABASE_URL + path + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300 || response.body() == null || response.body().equals("null")) {
                return null;
            }

            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean recordQuickPollVote(String pollCode, String voterId, String voterName, String option, String idToken) {
        try {
            String authParam = (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

            // 1. Record voter in voter list (prevents double voting in cloud)
            String voterPath = "/quick_polls/" + encode(pollCode) + "/voters/" + encode(voterId) + ".json";
            JsonObject voterRecord = new JsonObject();
            voterRecord.addProperty("voterName", voterName);
            voterRecord.addProperty("option", option);
            voterRecord.addProperty("timestamp", System.currentTimeMillis());

            HttpRequest voterReq = HttpRequest.newBuilder()
                    .uri(URI.create(FirebaseConfig.DATABASE_URL + voterPath + authParam))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(voterRecord.toString()))
                    .build();

            CLIENT.send(voterReq, HttpResponse.BodyHandlers.ofString());

            // 2. Fetch and increment option vote count
            String optPath = "/quick_polls/" + encode(pollCode) + "/options/" + encode(option) + ".json";
            HttpRequest getOptReq = HttpRequest.newBuilder()
                    .uri(URI.create(FirebaseConfig.DATABASE_URL + optPath + authParam))
                    .GET()
                    .build();

            HttpResponse<String> getOptResp = CLIENT.send(getOptReq, HttpResponse.BodyHandlers.ofString());
            int currentCount = 0;
            if (getOptResp.body() != null && !getOptResp.body().equals("null")) {
                try {
                    currentCount = Integer.parseInt(getOptResp.body().trim());
                } catch (Exception ignored) {}
            }

            HttpRequest putOptReq = HttpRequest.newBuilder()
                    .uri(URI.create(FirebaseConfig.DATABASE_URL + optPath + authParam))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(String.valueOf(currentCount + 1)))
                    .build();

            HttpResponse<String> putResp = CLIENT.send(putOptReq, HttpResponse.BodyHandlers.ofString());
            return putResp.statusCode() >= 200 && putResp.statusCode() < 300;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================
    // GET ALL ORGANIZATIONS (REALTIME DATABASE)
    // =========================================================

    public static JsonObject getAllOrganizations(String idToken) {
        try {
            String path = "/organizations.json";
            String url = FirebaseConfig.DATABASE_URL + path + (idToken != null && !idToken.isBlank() ? "?auth=" + encode(idToken) : "");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300 && response.body() != null && !response.body().equals("null")) {
                return JsonParser.parseString(response.body()).getAsJsonObject();
            }
        } catch (Exception e) {
            System.err.println("[FirebaseDatabaseService] getAllOrganizations note: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // GET ALL ORGANIZATIONS FOR A GIVEN USER (BY UID OR EMAIL)
    // =========================================================

    public static java.util.List<com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership> getUserOrganizations(
            String uid,
            String email,
            String idToken) {

        java.util.List<com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership> memberships = new java.util.ArrayList<>();
        java.util.Set<String> processedCodes = new java.util.HashSet<>();

        try {
            JsonObject allOrgs = getAllOrganizations(idToken);
            if (allOrgs == null && idToken != null) {
                // Try without token as fallback
                allOrgs = getAllOrganizations(null);
            }

            if (allOrgs != null) {
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : allOrgs.entrySet()) {
                    String joinCode = entry.getKey();
                    if (entry.getValue() == null || !entry.getValue().isJsonObject()) continue;
                    JsonObject org = entry.getValue().getAsJsonObject();

                    String orgName = org.has("organizationName") ? org.get("organizationName").getAsString() : joinCode;
                    String adminUid = org.has("adminUid") ? org.get("adminUid").getAsString() : "";
                    String adminEmail = org.has("adminEmail") ? org.get("adminEmail").getAsString() : "";

                    boolean isMember = false;
                    String role = "VOTER";
                    String status = "ACCEPTED";
                    String memberName = org.has("adminName") ? org.get("adminName").getAsString() : "Voter";

                    // 1. Check admin
                    if ((uid != null && !uid.isBlank() && uid.equals(adminUid)) ||
                        (email != null && !email.isBlank() && email.equalsIgnoreCase(adminEmail))) {
                        isMember = true;
                        role = "ADMIN";
                        status = "ACCEPTED";
                        memberName = org.has("adminName") ? org.get("adminName").getAsString() : "Administrator";
                    }

                    // 2. Check members list
                    if (org.has("members") && org.get("members").isJsonObject()) {
                        JsonObject members = org.getAsJsonObject("members");
                        
                        // Check direct UID
                        if (uid != null && members.has(uid) && members.get(uid).isJsonObject()) {
                            JsonObject m = members.getAsJsonObject(uid);
                            isMember = true;
                            String mRole = m.has("role") ? m.get("role").getAsString() : "VOTER";
                            if (!"ADMIN".equalsIgnoreCase(role)) {
                                role = mRole;
                                status = m.has("status") ? m.get("status").getAsString() : "PENDING";
                            }
                            if (m.has("name")) memberName = m.get("name").getAsString();
                            else if (m.has("fullName")) memberName = m.get("fullName").getAsString();
                        } else {
                            // Check matching email
                            for (java.util.Map.Entry<String, com.google.gson.JsonElement> memEntry : members.entrySet()) {
                                if (memEntry.getValue().isJsonObject()) {
                                    JsonObject m = memEntry.getValue().getAsJsonObject();
                                    String mEmail = m.has("email") ? m.get("email").getAsString() : "";
                                    String mUid = m.has("uid") ? m.get("uid").getAsString() : memEntry.getKey();

                                    if ((email != null && !email.isBlank() && email.equalsIgnoreCase(mEmail)) ||
                                        (uid != null && !uid.isBlank() && uid.equals(mUid))) {
                                        isMember = true;
                                        String mRole = m.has("role") ? m.get("role").getAsString() : "VOTER";
                                        if (!"ADMIN".equalsIgnoreCase(role)) {
                                            role = mRole;
                                            status = m.has("status") ? m.get("status").getAsString() : "PENDING";
                                        }
                                        if (m.has("name")) memberName = m.get("name").getAsString();
                                        else if (m.has("fullName")) memberName = m.get("fullName").getAsString();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (isMember && !processedCodes.contains(joinCode)) {
                        processedCodes.add(joinCode);
                        boolean isActive = joinCode.equalsIgnoreCase(com.electrovotesuperx.config.SessionManager.joinCode);
                        memberships.add(new com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership(
                                joinCode, orgName, role, status, memberName, email, 0, isActive
                        ));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[FirebaseDatabaseService] getUserOrganizations note: " + e.getMessage());
        }

        // Always ensure current session's active organization is in the list
        String activeJoinCode = com.electrovotesuperx.config.SessionManager.joinCode;
        if (activeJoinCode != null && !activeJoinCode.isBlank() && !processedCodes.contains(activeJoinCode)) {
            String activeOrgName = com.electrovotesuperx.config.SessionManager.organizationName != null && !com.electrovotesuperx.config.SessionManager.organizationName.isBlank()
                    ? com.electrovotesuperx.config.SessionManager.organizationName : activeJoinCode;
            String activeStatus = com.electrovotesuperx.config.SessionManager.voterStatus != null ? com.electrovotesuperx.config.SessionManager.voterStatus : "ACCEPTED";
            String activeName = com.electrovotesuperx.config.SessionManager.voterName != null ? com.electrovotesuperx.config.SessionManager.voterName : "Voter";
            String activeRole = com.electrovotesuperx.config.SessionManager.currentRole != null ? com.electrovotesuperx.config.SessionManager.currentRole.toUpperCase() : "VOTER";

            memberships.add(0, new com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership(
                    activeJoinCode, activeOrgName, activeRole, activeStatus, activeName, email, 0, true
            ));
        }

        return memberships;
    }

    // =========================================================
    // URL ENCODE
    // =========================================================

    private static String encode(
            String value) {

        return URLEncoder
                .encode(
                        value,
                        StandardCharsets.UTF_8
                );
    }
}