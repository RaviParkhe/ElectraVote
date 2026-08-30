package com.electrovotesuperx.dao.AdminDAO;

import com.electrovotesuperx.config.firebaseConfig.FirebaseConfig;
import com.electrovotesuperx.exception.FirestoreException;
import com.electrovotesuperx.model.AdminModel.Candidate;
import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * CandidateDAO handles all Firestore REST API calls for candidates.
 * Firestore path: Candidates/{id}
 */
public class CandidateDAO {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL =
            "https://firestore.googleapis.com/v1/projects/" + FirebaseConfig.PROJECT_ID + "/databases/(default)/documents/Candidates";

    // =========================================================
    // SAVE CANDIDATE (PATCH = upsert)
    // =========================================================

    public static boolean saveCandidate(Candidate candidate, String idToken)
            throws FirestoreException {

        String url = BASE_URL + "/" + encode(candidate.getId())
                + "?updateMask.fieldPaths=electionId"
                + "&updateMask.fieldPaths=electionTitle"
                + "&updateMask.fieldPaths=position"
                + "&updateMask.fieldPaths=name"
                + "&updateMask.fieldPaths=email"
                + "&updateMask.fieldPaths=phone"
                + "&updateMask.fieldPaths=bio"
                + "&updateMask.fieldPaths=status"
                + "&updateMask.fieldPaths=joinCode"
                + "&updateMask.fieldPaths=appliedAt";

        JsonObject fields = new JsonObject();
        fields.add("electionId", str(candidate.getElectionId()));
        fields.add("electionTitle", str(candidate.getElectionTitle()));
        fields.add("position", str(candidate.getPosition()));
        fields.add("name", str(candidate.getName()));
        fields.add("email", str(candidate.getEmail()));
        fields.add("phone", str(candidate.getPhone()));
        fields.add("bio", str(candidate.getBio()));
        fields.add("status", str(candidate.getStatus()));
        fields.add("joinCode", str(candidate.getJoinCode()));
        fields.add("appliedAt", intVal(candidate.getAppliedAt()));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res;
        try {
            res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to save candidate.", e);
        }
        System.out.println("[CandidateDAO.save] " + res.statusCode());
        return res.statusCode() >= 200 && res.statusCode() < 300;
    }

    // =========================================================
    // UPDATE CANDIDATE STATUS
    // =========================================================

    public static boolean updateStatus(String candidateId, String status, String idToken)
            throws FirestoreException {

        String url = BASE_URL + "/" + encode(candidateId)
                + "?updateMask.fieldPaths=status";

        JsonObject fields = new JsonObject();
        fields.add("status", str(status));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res;
        try {
            res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to update candidate status.", e);
        }
        return res.statusCode() >= 200 && res.statusCode() < 300;
    }

    // =========================================================
    // GET CANDIDATES BY ELECTION
    // =========================================================

    public static List<Candidate> getCandidatesByElection(String electionId, String idToken)
            throws FirestoreException {

        String queryUrl = "https://firestore.googleapis.com/v1/projects/"
                + FirebaseConfig.PROJECT_ID
                + "/databases/(default)/documents:runQuery";

        String queryBody = "{"
                + "\"structuredQuery\": {"
                + "  \"from\": [{\"collectionId\": \"Candidates\"}],"
                + "  \"where\": {"
                + "    \"fieldFilter\": {"
                + "      \"field\": {\"fieldPath\": \"electionId\"},"
                + "      \"op\": \"EQUAL\","
                + "      \"value\": {\"stringValue\": \"" + electionId + "\"}"
                + "    }"
                + "  }"
                + "}"
                + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(queryUrl))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(queryBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res;
        try {
            res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve candidates.", e);
        }

        List<Candidate> result = new ArrayList<>();
        if (res.statusCode() < 200 || res.statusCode() >= 300) return result;

        JsonArray docs = JsonParser.parseString(res.body()).getAsJsonArray();
        for (JsonElement el : docs) {
            JsonObject obj = el.getAsJsonObject();
            if (!obj.has("document")) continue;
            result.add(parseCandidate(obj.getAsJsonObject("document")));
        }
        return result;
    }

    // =========================================================
    // GET CANDIDATES BY ORGANIZATION (JOIN CODE)
    // =========================================================

    public static List<Candidate> getCandidatesByOrg(String joinCode, String idToken)
            throws FirestoreException {

        String queryUrl = "https://firestore.googleapis.com/v1/projects/"
                + FirebaseConfig.PROJECT_ID
                + "/databases/(default)/documents:runQuery";

        String queryBody = "{"
                + "\"structuredQuery\": {"
                + "  \"from\": [{\"collectionId\": \"Candidates\"}],"
                + "  \"where\": {"
                + "    \"fieldFilter\": {"
                + "      \"field\": {\"fieldPath\": \"joinCode\"},"
                + "      \"op\": \"EQUAL\","
                + "      \"value\": {\"stringValue\": \"" + joinCode + "\"}"
                + "    }"
                + "  }"
                + "}"
                + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(queryUrl))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(queryBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res;
        try {
            res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to retrieve candidates.", e);
        }

        List<Candidate> result = new ArrayList<>();
        if (res.statusCode() < 200 || res.statusCode() >= 300) return result;

        JsonArray docs = JsonParser.parseString(res.body()).getAsJsonArray();
        for (JsonElement el : docs) {
            JsonObject obj = el.getAsJsonObject();
            if (!obj.has("document")) continue;
            result.add(parseCandidate(obj.getAsJsonObject("document")));
        }
        return result;
    }

    // =========================================================
    // DELETE CANDIDATE
    // =========================================================

    public static boolean deleteCandidate(String candidateId, String idToken)
            throws FirestoreException {

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + encode(candidateId)))
                .header("Authorization", "Bearer " + idToken)
                .DELETE()
                .build();

        HttpResponse<String> res;
        try {
            res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new FirestoreException("Unable to delete candidate.", e);
        }
        return res.statusCode() >= 200 && res.statusCode() < 300;
    }

    // =========================================================
    // PARSE
    // =========================================================

    private static Candidate parseCandidate(JsonObject doc) {
        Candidate c = new Candidate();
        if (doc.has("name")) {
            String name = doc.get("name").getAsString();
            c.setId(name.substring(name.lastIndexOf('/') + 1));
        }
        if (!doc.has("fields")) return c;
        JsonObject f = doc.getAsJsonObject("fields");
        c.setElectionId(str(f, "electionId"));
        c.setElectionTitle(str(f, "electionTitle"));
        c.setPosition(str(f, "position"));
        c.setName(str(f, "name"));
        c.setEmail(str(f, "email"));
        c.setPhone(str(f, "phone"));
        c.setBio(str(f, "bio"));
        c.setStatus(str(f, "status"));
        c.setJoinCode(str(f, "joinCode"));
        if (f.has("appliedAt")) {
            JsonObject at = f.getAsJsonObject("appliedAt");
            if (at.has("integerValue")) c.setAppliedAt(at.get("integerValue").getAsLong());
        }
        return c;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private static JsonObject str(String v) {
        JsonObject o = new JsonObject();
        o.addProperty("stringValue", v != null ? v : "");
        return o;
    }

    private static String str(JsonObject fields, String key) {
        if (!fields.has(key)) return "";
        JsonObject f = fields.getAsJsonObject(key);
        return f.has("stringValue") ? f.get("stringValue").getAsString() : "";
    }

    private static JsonObject intVal(long v) {
        JsonObject o = new JsonObject();
        o.addProperty("integerValue", v);
        return o;
    }

    private static String encode(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
