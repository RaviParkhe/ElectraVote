package com.electrovotesuperx.service;

import com.electrovotesuperx.config.SessionManager;
import com.google.gson.*;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

/**
 * Service to connect ElectraVote's AI Governance Advisor to Google Gemini AI
 * API.
 */
public class AIGovernanceService {

    // =========================================================================
    // 🔑 PASTE YOUR GOOGLE GEMINI API KEY HERE
    // (Get a free key from https://aistudio.google.com/)
    // =========================================================================
    public static String GEMINI_API_KEY = "AQ.Ab8RN6IcA-JrGb6ZMYdfOyApaLwsztut1g43EcONTlq2DSuq8w";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * Sends the governance query with live election context to Gemini 1.5 Flash.
     * Returns null if API key is not configured or if the request fails (falling
     * back to built-in intelligence).
     */
    public static String callGeminiAPI(String userPrompt, int activeElections, int votesCast) {
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isBlank() || GEMINI_API_KEY.contains("PASTE_YOUR_API_KEY")) {
            return null; // Fallback to built-in governance rules engine
        }

        try {
            String orgName = SessionManager.organizationName != null ? SessionManager.organizationName
                    : "Current Organization";
            String orgCode = SessionManager.joinCode != null ? SessionManager.joinCode : "EV-SYSTEM";

            String systemPrompt = "You are the ElectraVote AI Governance and Compliance Advisor for organization: "
                    + orgName
                    + " (Organization Code: " + orgCode + "). "
                    + "Live Election Stats: Active Elections = " + activeElections + ", Total Cast Ballots = "
                    + votesCast + ". "
                    + "Provide clear, professional, structured advice with bullet points on election governance, voter turnout strategies, dispute resolution, or announcement drafting.";

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                    + GEMINI_API_KEY.trim();

            JsonObject contentObj = new JsonObject();
            JsonArray partsArr = new JsonArray();
            JsonObject partObj = new JsonObject();
            partObj.addProperty("text", systemPrompt + "\n\nUser Question / Instruction: " + userPrompt);
            partsArr.add(partObj);
            contentObj.add("parts", partsArr);

            JsonArray contentsArr = new JsonArray();
            contentsArr.add(contentObj);

            JsonObject reqBody = new JsonObject();
            reqBody.add("contents", contentsArr);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonObject resJson = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray candidates = resJson.getAsJsonArray("candidates");
                if (candidates != null && candidates.size() > 0) {
                    JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                    JsonObject content = firstCandidate.getAsJsonObject("content");
                    JsonArray parts = content.getAsJsonArray("parts");
                    if (parts != null && parts.size() > 0) {
                        return parts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            } else {
                System.err.println("[AIGovernanceService] Gemini API Response Status: " + response.statusCode() + " -> "
                        + response.body());
            }
        } catch (Exception ex) {
            System.err.println("[AIGovernanceService] API call error: " + ex.getMessage());
        }
        return null;
    }
}
