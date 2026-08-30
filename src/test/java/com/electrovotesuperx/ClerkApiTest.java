package com.electrovotesuperx;

import com.electrovotesuperx.service.OfflineService.ClerkOtpService;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClerkApiTest {

    @Test
    public void testClerkApiKeyAuthentication() {
        String secretKey = ClerkOtpService.CLERK_SECRET_KEY;
        System.out.println("Testing Clerk Secret Key authentication...");

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.clerk.com/v1/users?limit=1"))
                    .header("Authorization", "Bearer " + secretKey.trim())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Clerk API Response Code: " + response.statusCode());
            System.out.println("Clerk API Response Body: " + response.body());

            assertTrue(response.statusCode() == 200, "Clerk Secret Key should authenticate successfully (HTTP 200)");
            System.out.println("✅ Clerk API Key is 100% VALID and CONNECTED!");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
