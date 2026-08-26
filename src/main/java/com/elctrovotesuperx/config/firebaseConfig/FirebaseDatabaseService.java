package com.elctrovotesuperx.config.firebaseConfig;


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

        JsonObject voter =
                new JsonObject();

        voter.addProperty(
                "uid",
                uid
        );

        voter.addProperty(
                "name",
                name
        );

        voter.addProperty(
                "email",
                email
        );

        voter.addProperty(
                "role",
                "VOTER"
        );

        voter.addProperty(
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