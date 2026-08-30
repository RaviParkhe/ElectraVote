package com.electrovotesuperx;

import com.electrovotesuperx.service.OfflineService.ClerkOtpService;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class Fast2SmsTest {

    @Test
    public void testQuickSmsRoute() {
        String apiKey = ClerkOtpService.FAST2SMS_API_KEY;
        String phone = "8459955616";
        String otp = "926010";

        try {
            HttpClient client = HttpClient.newHttpClient();
            
            // Fast2SMS Quick Route (route="q") bypasses website verification requirement
            JsonObject json = new JsonObject();
            json.addProperty("route", "q");
            json.addProperty("message", "ElectraVote: Your Offline Ballot Verification OTP is " + otp + ". Valid for 60 seconds.");
            json.addProperty("language", "english");
            json.addProperty("flash", 0);
            json.addProperty("numbers", phone);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://www.fast2sms.com/dev/bulkV2"))
                    .header("authorization", apiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Fast2SMS Quick Route Response Code: " + response.statusCode());
            System.out.println("Fast2SMS Quick Route Response Body: " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
