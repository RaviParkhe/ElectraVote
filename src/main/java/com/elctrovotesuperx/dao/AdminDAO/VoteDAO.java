package com.elctrovotesuperx.dao.AdminDAO;

import com.elctrovotesuperx.model.AdminModel.VoteRecord;
import com.google.gson.*;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * VoteDAO handles all Firestore REST API calls for cast votes.
 * Firestore path: Votes/{id}
 */
public class VoteDAO {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL =
            "https://firestore.googleapis.com/v1/projects/electravote-ca872/databases/(default)/documents/Votes";

    // =========================================================
    // CAST VOTE (save vote record)
    // =========================================================

    public static boolean castVote(VoteRecord vote, String idToken)
            throws IOException, InterruptedException {

        String url = BASE_URL + "/" + encode(vote.getId())
                + "?updateMask.fieldPaths=electionId"
                + "&updateMask.fieldPaths=voterUid"
                + "&updateMask.fieldPaths=position"
                + "&updateMask.fieldPaths=candidateId"
                + "&updateMask.fieldPaths=candidateName"
                + "&updateMask.fieldPaths=joinCode"
                + "&updateMask.fieldPaths=votedAt";

        JsonObject fields = new JsonObject();
        fields.add("electionId", str(vote.getElectionId()));
        fields.add("voterUid", str(vote.getVoterUid()));
        fields.add("position", str(vote.getPosition()));
        fields.add("candidateId", str(vote.getCandidateId()));
        fields.add("candidateName", str(vote.getCandidateName()));
        fields.add("joinCode", str(vote.getJoinCode()));
        fields.add("votedAt", intVal(vote.getVotedAt()));

        JsonObject body = new JsonObject();
        body.add("fields", fields);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("[VoteDAO.castVote] " + res.statusCode());
        return res.statusCode() >= 200 && res.statusCode() < 300;
    }

    // =========================================================
    // CHECK IF VOTER ALREADY VOTED FOR A POSITION IN AN ELECTION
    // =========================================================

    public static boolean hasVoted(String electionId, String voterUid,
                                   String position, String idToken)
            throws IOException, InterruptedException {

        String queryUrl = "https://firestore.googleapis.com/v1/projects/electravote-ca872/databases/(default)/documents:runQuery";

        String queryBody = "{"
                + "\"structuredQuery\": {"
                + "  \"from\": [{\"collectionId\": \"Votes\"}],"
                + "  \"where\": {"
                + "    \"compositeFilter\": {"
                + "      \"op\": \"AND\","
                + "      \"filters\": ["
                + "        {\"fieldFilter\": {\"field\": {\"fieldPath\": \"electionId\"}, \"op\": \"EQUAL\", \"value\": {\"stringValue\": \"" + electionId + "\"}}},"
                + "        {\"fieldFilter\": {\"field\": {\"fieldPath\": \"voterUid\"}, \"op\": \"EQUAL\", \"value\": {\"stringValue\": \"" + voterUid + "\"}}},"
                + "        {\"fieldFilter\": {\"field\": {\"fieldPath\": \"position\"}, \"op\": \"EQUAL\", \"value\": {\"stringValue\": \"" + position + "\"}}}"
                + "      ]"
                + "    }"
                + "  },"
                + "  \"limit\": 1"
                + "}"
                + "}";

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(queryUrl))
                .header("Authorization", "Bearer " + idToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(queryBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() < 200 || res.statusCode() >= 300) return false;

        JsonArray docs = JsonParser.parseString(res.body()).getAsJsonArray();
        return docs.size() > 0 && docs.get(0).getAsJsonObject().has("document");
    }

    // =========================================================
    // GET RESULTS — aggregate vote counts per candidate per position
    // Returns: Map<position, Map<candidateName, voteCount>>
    // =========================================================

    public static Map<String, Map<String, Integer>> getResults(
            String electionId, String idToken)
            throws IOException, InterruptedException {

        String queryUrl = "https://firestore.googleapis.com/v1/projects/electravote-ca872/databases/(default)/documents:runQuery";

        String queryBody = "{"
                + "\"structuredQuery\": {"
                + "  \"from\": [{\"collectionId\": \"Votes\"}],"
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

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

        Map<String, Map<String, Integer>> results = new LinkedHashMap<>();
        if (res.statusCode() < 200 || res.statusCode() >= 300) return results;

        JsonArray docs = JsonParser.parseString(res.body()).getAsJsonArray();
        for (JsonElement el : docs) {
            JsonObject obj = el.getAsJsonObject();
            if (!obj.has("document")) continue;
            JsonObject doc = obj.getAsJsonObject("document");
            if (!doc.has("fields")) continue;
            JsonObject fields = doc.getAsJsonObject("fields");

            String position = strField(fields, "position");
            String candidateName = strField(fields, "candidateName");

            results.computeIfAbsent(position, k -> new LinkedHashMap<>())
                    .merge(candidateName, 1, Integer::sum);
        }
        return results;
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private static JsonObject str(String v) {
        JsonObject o = new JsonObject();
        o.addProperty("stringValue", v != null ? v : "");
        return o;
    }

    private static JsonObject intVal(long v) {
        JsonObject o = new JsonObject();
        o.addProperty("integerValue", v);
        return o;
    }

    private static String strField(JsonObject fields, String key) {
        if (!fields.has(key)) return "";
        JsonObject f = fields.getAsJsonObject(key);
        return f.has("stringValue") ? f.get("stringValue").getAsString() : "";
    }

    private static String encode(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
