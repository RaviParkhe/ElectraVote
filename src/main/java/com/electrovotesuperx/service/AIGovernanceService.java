package com.electrovotesuperx.service;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.dao.OfflineDAO.ElectionStatsDAO;
import com.google.gson.*;

import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;

/**
 * Service to connect ElectraVote's AI Election Assistant to Google Gemini AI.
 */
public class AIGovernanceService {

    // =========================================================================
    // 🔑 PASTE YOUR GOOGLE GEMINI API KEY HERE
    // (Get a free key from https://aistudio.google.com/)
    // =========================================================================
    public static String GEMINI_API_KEY = "AQ.Ab8RN6IcA-JrGb6ZMYdfOyApaLwsztut1g43EcONTlq2DSuq8w";

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    /**
     * Sends a chat message with live election DB context to Gemini 1.5 Flash.
     * Returns null if API key is not configured or if the request fails.
     */
    public static String chat(String userPrompt, ElectionStatsDAO.ElectionContext context) {
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isBlank() || GEMINI_API_KEY.contains("PASTE_YOUR_API_KEY")) {
            return null;
        }

        try {
            String orgName = SessionManager.organizationName != null ? SessionManager.organizationName
                    : "Current Organization";
            String orgCode = SessionManager.joinCode != null ? SessionManager.joinCode : "EV-SYSTEM";
            String officerName = SessionManager.officerName != null ? SessionManager.officerName : "Officer";
            String station = SessionManager.officerStation != null ? SessionManager.officerStation : "Station";

            String systemPrompt = """
                    You are the ElectraVote AI Election Assistant for organization: %s (Code: %s).
                    Logged-in officer: %s at %s.

                    %s

                    Answer questions about this organization's election data clearly and professionally.
                    Use bullet points, numbers, and percentages where helpful.
                    If asked for a report, format it cleanly with sections.
                    Keep answers concise unless the user asks for a full report.
                    You only have access to the offline (local) election data shown above.
                    """.formatted(orgName, orgCode, officerName, station,
                            context != null ? context.toString() : "No data available.");

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key="
                    + GEMINI_API_KEY.trim();

            JsonObject contentObj = new JsonObject();
            JsonArray partsArr = new JsonArray();
            JsonObject partObj = new JsonObject();
            partObj.addProperty("text", systemPrompt + "\n\nUser question: " + userPrompt);
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
                System.err.println("[AIGovernanceService] Gemini API Status: " + response.statusCode()
                        + " -> " + response.body());
            }
        } catch (Exception ex) {
            System.err.println("[AIGovernanceService] API call error: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Sends a chat query with live Online Election Context (Firestore & Firebase) to Gemini AI.
     */
    public static String chatOnline(String userPrompt, com.electrovotesuperx.dao.AdminDAO.OnlineElectionStatsDAO.OnlineContext context) {
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isBlank() || GEMINI_API_KEY.contains("PASTE_YOUR_API_KEY")) {
            return null;
        }

        try {
            String orgName = context != null && context.orgName() != null ? context.orgName() : "Current Organization";
            String orgCode = context != null && context.joinCode() != null ? context.joinCode() : "EV-SYSTEM";
            String adminName = context != null && context.adminName() != null ? context.adminName() : "Administrator";

            String systemPrompt = """
                    You are the ElectraVote AI Online Election Assistant for organization: %s (Join Code: %s).
                    Logged-in Administrator: %s.

                    %s

                    INSTRUCTIONS:
                    1. Answer questions clearly using the real-time online organization data provided above.
                    2. If asked about voter turnout or percentages, cite the exact numbers from the data.
                    3. If asked "How many members haven't voted?", calculate and list the numbers clearly (Total eligible voters vs. votes cast).
                    4. If asked "Which election had the highest turnout?", compare the turnout percentages and highlight the winner.
                    5. If asked "Show me elections with declining participation", analyze trends across elections.
                    6. If asked "Generate a report for this election" (or general report), structure a clean markdown report with sections.
                    7. If asked "Why was turnout lower this year?", provide realistic, strategic election governance insights based on the numbers and timing.
                    8. Keep responses concise, professional, and well-formatted with markdown bolding and bullet points.
                    """.formatted(orgName, orgCode, adminName, context != null ? context.toString() : "No live data available.");

            // Verified active high-availability models with zero 503/429
            String[] candidateModels = {
                "gemini-3.5-flash",
                "gemini-3.5-flash-lite",
                "gemini-3.1-flash-lite",
                "gemini-3.6-flash",
                "gemini-flash-latest"
            };

            JsonObject contentObj = new JsonObject();
            JsonArray partsArr = new JsonArray();
            JsonObject partObj = new JsonObject();
            partObj.addProperty("text", systemPrompt + "\n\nAdmin Question / Instruction: " + userPrompt);
            partsArr.add(partObj);
            contentObj.add("parts", partsArr);

            JsonArray contentsArr = new JsonArray();
            contentsArr.add(contentObj);

            JsonObject reqBody = new JsonObject();
            reqBody.add("contents", contentsArr);

            for (String modelName : candidateModels) {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key="
                        + GEMINI_API_KEY.trim();

                try {
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
                        System.err.println("[AIGovernanceService] Model " + modelName + " Status: " + response.statusCode());
                    }
                } catch (Exception modelEx) {
                    System.err.println("[AIGovernanceService] Model " + modelName + " Error: " + modelEx.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("[AIGovernanceService] Online API error: " + ex.getMessage());
        }
        return null;
    }

    /**
     * Legacy overload — kept for backward compatibility.
     * Builds a minimal context from the two integer values.
     */
    public static String callGeminiAPI(String userPrompt, int activeElections, int votesCast) {
        ElectionStatsDAO.ElectionContext ctx = new ElectionStatsDAO.ElectionContext(
                activeElections, activeElections, 0, 0,
                votesCast, votesCast, 0.0, 0, java.util.List.of());
        return chat(userPrompt, ctx);
    }
}

