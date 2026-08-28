package com.elctrovotesuperx.dao.OrganizationDAO;

import com.elctrovotesuperx.exception.FirestoreException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

public class FirestoreDAO {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final String PROJECT_ID = "electravote-ca872";

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + idToken)
                .GET()
                .build();

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
    // GET USER
    // =====================================================

    public static JsonObject getUser(
            String uid,
            String idToken) throws FirestoreException {

        String url = "https://firestore.googleapis.com/v1/projects/"
                + PROJECT_ID
                + "/databases/(default)/documents/Users/"
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