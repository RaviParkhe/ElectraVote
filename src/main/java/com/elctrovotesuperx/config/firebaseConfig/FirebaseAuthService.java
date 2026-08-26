package com.elctrovotesuperx.config.firebaseConfig;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FirebaseAuthService {

    private static final HttpClient CLIENT =
            HttpClient.newHttpClient();

    // =========================================================
    // CREATE USER
    // =========================================================

    public static AuthResult createUser(
            String email,
            String password) throws IOException, InterruptedException {

        String url =
                "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                        + FirebaseConfig.API_KEY;

        String json =
                "{"
                        + "\"email\":\"" + escape(email) + "\","
                        + "\"password\":\"" + escape(password) + "\","
                        + "\"returnSecureToken\":true"
                        + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        json,
                                        StandardCharsets.UTF_8
                                )
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        return parseResponse(response);
    }

    // =========================================================
    // SIGN IN
    // =========================================================

    public static AuthResult signIn(
            String email,
            String password) throws IOException, InterruptedException {

        String url =
                "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                        + FirebaseConfig.API_KEY;

        String json =
                "{"
                        + "\"email\":\"" + escape(email) + "\","
                        + "\"password\":\"" + escape(password) + "\","
                        + "\"returnSecureToken\":true"
                        + "}";

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        json,
                                        StandardCharsets.UTF_8
                                )
                        )
                        .build();

        HttpResponse<String> response =
                CLIENT.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        return parseResponse(response);
    }

    // =========================================================
    // PARSE RESPONSE
    // =========================================================

    private static AuthResult parseResponse(
            HttpResponse<String> response) {

        JsonObject json =
                JsonParser.parseString(
                        response.body()
                ).getAsJsonObject();

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            String idToken =
                    json.has("idToken")
                            ? json.get("idToken").getAsString()
                            : null;

            String localId =
                    json.has("localId")
                            ? json.get("localId").getAsString()
                            : null;

            String refreshToken =
                    json.has("refreshToken")
                            ? json.get("refreshToken").getAsString()
                            : null;

            String email =
                    json.has("email")
                            ? json.get("email").getAsString()
                            : null;

            return AuthResult.success(
                    idToken,
                    localId,
                    refreshToken,
                    email
            );
        }

        String message = "Firebase authentication failed.";

        if (json.has("error")) {

            JsonObject error =
                    json.getAsJsonObject("error");

            if (error.has("message")) {

                message =
                        firebaseErrorMessage(
                                error.get("message").getAsString()
                        );
            }
        }

        return AuthResult.failure(message);
    }

    // =========================================================
    // FIREBASE ERROR TRANSLATION
    // =========================================================

    private static String firebaseErrorMessage(
            String error) {

        return switch (error) {

            case "EMAIL_EXISTS" ->
                    "This email is already registered.";

            case "EMAIL_NOT_FOUND" ->
                    "No account exists with this email.";

            case "INVALID_PASSWORD" ->
                    "Incorrect password.";

            case "INVALID_LOGIN_CREDENTIALS" ->
                    "Invalid email or password.";

            case "USER_DISABLED" ->
                    "This account has been disabled.";

            case "WEAK_PASSWORD : Password should be at least 6 characters" ->
                    "Password must contain at least 6 characters.";

            case "OPERATION_NOT_ALLOWED" ->
                    "Email/password authentication is disabled in Firebase.";

            case "TOO_MANY_ATTEMPTS_TRY_LATER" ->
                    "Too many attempts. Please try again later.";

            default ->
                    error;
        };
    }

    // =========================================================
    // ESCAPE JSON
    // =========================================================

    private static String escape(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    // =========================================================
    // AUTH RESULT
    // =========================================================

    public static class AuthResult {

        private final boolean success;
        private final String message;

        private final String idToken;
        private final String localId;
        private final String refreshToken;
        private final String email;

        private AuthResult(
                boolean success,
                String message,
                String idToken,
                String localId,
                String refreshToken,
                String email) {

            this.success = success;
            this.message = message;

            this.idToken = idToken;
            this.localId = localId;
            this.refreshToken = refreshToken;
            this.email = email;
        }

        public static AuthResult success(
                String idToken,
                String localId,
                String refreshToken,
                String email) {

            return new AuthResult(
                    true,
                    null,
                    idToken,
                    localId,
                    refreshToken,
                    email
            );
        }

        public static AuthResult failure(
                String message) {

            return new AuthResult(
                    false,
                    message,
                    null,
                    null,
                    null,
                    null
            );
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getIdToken() {
            return idToken;
        }

        public String getLocalId() {
            return localId;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getEmail() {
            return email;
        }
    }
}