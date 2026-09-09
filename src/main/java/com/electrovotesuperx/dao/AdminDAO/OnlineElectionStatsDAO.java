package com.electrovotesuperx.dao.AdminDAO;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.model.AdminModel.ElectionData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Gathers live statistics from Firestore and Firebase Realtime Database
 * for the Admin Online AI Election Assistant.
 *
 * Uses the logged-in organization's authorized data only (tenant isolation via joinCode & idToken).
 */
public class OnlineElectionStatsDAO {

    public record OnlineContext(
            String orgName,
            String joinCode,
            String adminName,
            int totalElections,
            int activeElections,
            int draftElections,
            int closedElections,
            int totalVoters,
            int acceptedVoters,
            int pendingVoters,
            int totalBallotsCast,
            double overallTurnoutPct,
            List<OnlineElectionSummary> electionSummaries,
            List<String> registeredVoterNames) {

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Authorized Organization Online Election Data ===\n");
            sb.append("Organization: ").append(orgName).append(" (Join Code: ").append(joinCode).append(")\n");
            sb.append("Logged-in Administrator: ").append(adminName).append("\n\n");

            sb.append("=== Summary Metrics ===\n");
            sb.append("• Total Online Elections: ").append(totalElections)
              .append(" (Active/Live: ").append(activeElections)
              .append(", Draft: ").append(draftElections)
              .append(", Closed/Ended: ").append(closedElections).append(")\n");
            sb.append("• Total Registered Voters: ").append(totalVoters)
              .append(" (Approved/Active: ").append(acceptedVoters)
              .append(", Pending Approval: ").append(pendingVoters).append(")\n");
            sb.append("• Total Ballots Cast Across All Elections: ").append(totalBallotsCast).append("\n");
            sb.append(String.format("• Overall Turnout Rate: %.2f%%\n\n", overallTurnoutPct));

            if (!electionSummaries.isEmpty()) {
                sb.append("=== Per-Election Breakdown & Live Tallies ===\n");
                for (OnlineElectionSummary e : electionSummaries) {
                    sb.append("• Election: \"").append(e.title()).append("\" [ID: ").append(e.electionId()).append("]\n");
                    sb.append("  - Status: ").append(e.status()).append("\n");
                    if (e.startDateTime() != null && !e.startDateTime().isBlank()) {
                        sb.append("  - Schedule: ").append(e.startDateTime()).append(" to ").append(e.endDateTime()).append("\n");
                    }
                    sb.append(String.format("  - Ballots Cast: %d / %d eligible voters (Turnout: %.2f%%)\n",
                            e.ballotsCast(), acceptedVoters > 0 ? acceptedVoters : totalVoters, e.turnoutPct()));
                    sb.append("  - Positions: ").append(String.join(", ", e.positions())).append("\n");
                    if (e.candidateVotes() != null && !e.candidateVotes().isEmpty()) {
                        sb.append("  - Candidate Standings:\n");
                        for (Map.Entry<String, Map<String, Integer>> posEntry : e.candidateVotes().entrySet()) {
                            sb.append("    * Position [").append(posEntry.getKey()).append("]: ");
                            List<String> cands = new ArrayList<>();
                            for (Map.Entry<String, Integer> c : posEntry.getValue().entrySet()) {
                                cands.add(c.getKey() + " (" + c.getValue() + " votes)");
                            }
                            sb.append(String.join(", ", cands)).append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }

            return sb.toString();
        }
    }

    public record OnlineElectionSummary(
            String electionId,
            String title,
            String status,
            String startDateTime,
            String endDateTime,
            List<String> positions,
            int ballotsCast,
            double turnoutPct,
            Map<String, Map<String, Integer>> candidateVotes) {}

    // =========================================================
    // FETCH LIVE ONLINE CONTEXT FOR CURRENT ORGANIZATION
    // =========================================================

    public static OnlineContext getLiveOnlineContext() {
        String orgName = SessionManager.organizationName != null && !SessionManager.organizationName.isBlank()
                ? SessionManager.organizationName : "Current Organization";
        String joinCode = SessionManager.joinCode != null ? SessionManager.joinCode : "EV-SYSTEM";
        String adminName = SessionManager.adminName != null ? SessionManager.adminName : "Administrator";
        String idToken = SessionManager.idToken;

        int totalElections = 0;
        int activeElections = 0;
        int draftElections = 0;
        int closedElections = 0;
        int totalVoters = 0;
        int acceptedVoters = 0;
        int pendingVoters = 0;
        int totalBallotsCast = 0;

        List<OnlineElectionSummary> summaries = new ArrayList<>();
        List<String> voterNames = new ArrayList<>();

        // 1. Fetch live elections from Firestore
        List<ElectionData> elections = new ArrayList<>();
        try {
            if (joinCode != null && !joinCode.isBlank() && idToken != null) {
                elections = ElectionDAO.getElectionsByOrg(joinCode, idToken);
            }
        } catch (Exception ex) {
            System.err.println("[OnlineElectionStatsDAO] Error loading elections: " + ex.getMessage());
        }

        totalElections = elections.size();

        // 2. Fetch live registered voters from Firebase Realtime DB
        try {
            if (joinCode != null && !joinCode.isBlank() && idToken != null) {
                JsonObject membersJson = FirebaseDatabaseService.getAllMembers(joinCode, idToken);
                if (membersJson != null) {
                    for (Map.Entry<String, JsonElement> entry : membersJson.entrySet()) {
                        if (entry.getValue().isJsonObject()) {
                            JsonObject m = entry.getValue().getAsJsonObject();
                            String role = m.has("role") && !m.get("role").isJsonNull() ? m.get("role").getAsString() : "VOTER";
                            if ("VOTER".equalsIgnoreCase(role)) {
                                totalVoters++;
                                String status = m.has("status") && !m.get("status").isJsonNull() ? m.get("status").getAsString() : "PENDING";
                                if ("ACCEPTED".equalsIgnoreCase(status)) {
                                    acceptedVoters++;
                                } else if ("PENDING".equalsIgnoreCase(status)) {
                                    pendingVoters++;
                                }
                                String name = m.has("name") && !m.get("name").isJsonNull() ? m.get("name").getAsString() : "Member " + totalVoters;
                                voterNames.add(name + " (" + status + ")");
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println("[OnlineElectionStatsDAO] Error loading voters: " + ex.getMessage());
        }

        int eligibleVoters = acceptedVoters > 0 ? acceptedVoters : totalVoters;

        // 3. Fetch live votes for each election from Firestore
        for (ElectionData e : elections) {
            String status = e.getStatus() != null ? e.getStatus() : "Draft";
            if ("Active".equalsIgnoreCase(status) || "Live".equalsIgnoreCase(status)) {
                activeElections++;
            } else if ("Draft".equalsIgnoreCase(status)) {
                draftElections++;
            } else if ("Closed".equalsIgnoreCase(status) || "Ended".equalsIgnoreCase(status)) {
                closedElections++;
            }

            int electionBallots = 0;
            Map<String, Map<String, Integer>> results = new LinkedHashMap<>();
            try {
                if (idToken != null) {
                    results = VoteDAO.getResults(e.getId(), idToken);
                    if (results != null && !results.isEmpty()) {
                        for (Map<String, Integer> candMap : results.values()) {
                            int posVotes = 0;
                            for (int cnt : candMap.values()) {
                                posVotes += cnt;
                            }
                            if (posVotes > electionBallots) {
                                electionBallots = posVotes;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println("[OnlineElectionStatsDAO] Error loading votes for " + e.getId() + ": " + ex.getMessage());
            }

            totalBallotsCast += electionBallots;
            double turnoutPct = eligibleVoters > 0 ? (electionBallots * 100.0 / eligibleVoters) : 0.0;

            summaries.add(new OnlineElectionSummary(
                    e.getId(),
                    e.getTitle(),
                    status,
                    e.getStartDateTime(),
                    e.getEndDateTime(),
                    e.getPositions() != null ? e.getPositions() : Collections.emptyList(),
                    electionBallots,
                    turnoutPct,
                    results
            ));
        }

        int totalOpportunities = eligibleVoters * Math.max(1, totalElections);
        double overallTurnout = (totalOpportunities > 0 && totalBallotsCast > 0)
                ? Math.min(100.0, (totalBallotsCast * 100.0) / totalOpportunities)
                : 0.0;

        return new OnlineContext(
                orgName,
                joinCode,
                adminName,
                totalElections,
                activeElections,
                draftElections,
                closedElections,
                totalVoters,
                acceptedVoters,
                pendingVoters,
                totalBallotsCast,
                overallTurnout,
                summaries,
                voterNames
        );
    }
}
