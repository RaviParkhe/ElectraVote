package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Gathers live statistics from the local SQLite database for the
 * AI Election Assistant context prompt.
 *
 * The context is re-fetched on every AI query so the assistant always
 * sees up-to-date data.
 */
public class ElectionStatsDAO {

    // =========================================================
    // ELECTION CONTEXT RECORD
    // =========================================================

    public record ElectionContext(
            int totalElections,
            int openElections,
            int draftElections,
            int closedElections,
            int totalMembers,
            int totalVoted,
            double overallTurnoutPct,
            int todayRiskFlags,
            List<ElectionStat> perElection) {

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== Live Election Stats ===\n");
            sb.append("Total Elections: ").append(totalElections)
              .append(" (OPEN: ").append(openElections)
              .append(", DRAFT: ").append(draftElections)
              .append(", CLOSED: ").append(closedElections).append(")\n");
            sb.append("Total Enrolled Members: ").append(totalMembers).append("\n");
            sb.append("Total Votes Cast: ").append(totalVoted).append("\n");
            sb.append(String.format("Overall Turnout: %.1f%%\n", overallTurnoutPct));
            sb.append("Today's Risk Flags: ").append(todayRiskFlags).append("\n\n");

            if (!perElection.isEmpty()) {
                sb.append("=== Per-Election Breakdown ===\n");
                for (ElectionStat stat : perElection) {
                    sb.append(stat).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public record ElectionStat(
            String electionId,
            String name,
            String status,
            int enrolled,
            int voted) {

        public double turnoutPct() {
            return enrolled == 0 ? 0.0 : (voted * 100.0 / enrolled);
        }

        @Override
        public String toString() {
            return String.format(
                    "[%s] %s (%s) — Enrolled: %d, Voted: %d, Turnout: %.1f%%",
                    electionId, name, status, enrolled, voted, turnoutPct());
        }
    }

    // =========================================================
    // GET CONTEXT — single method called by AIGovernanceService
    // =========================================================

    public ElectionContext getContext() {
        try (Connection c = DatabaseConfig.getConnection()) {

            int totalElections = 0, openElections = 0, draftElections = 0, closedElections = 0;
            int totalMembers = 0, totalVoted = 0;
            int todayRiskFlags = 0;
            List<ElectionStat> perElection = new ArrayList<>();

            // === Election counts by status ===
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                         "SELECT status, COUNT(*) AS cnt FROM elections GROUP BY status")) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int cnt = rs.getInt("cnt");
                    totalElections += cnt;
                    switch (status == null ? "" : status.toUpperCase()) {
                        case "OPEN"   -> openElections = cnt;
                        case "DRAFT"  -> draftElections = cnt;
                        case "CLOSED" -> closedElections = cnt;
                    }
                }
            }

            // === Total enrolled members across all elections ===
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                         "SELECT COUNT(*) FROM election_voters")) {
                if (rs.next()) totalMembers = rs.getInt(1);
            }

            // === Total votes cast ===
            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(
                         "SELECT COUNT(*) FROM election_voters WHERE status = 'VOTED'")) {
                if (rs.next()) totalVoted = rs.getInt(1);
            }

            // === Today's risk flags ===
            String today = LocalDate.now().toString();
            try (PreparedStatement ps = c.prepareStatement(
                         "SELECT COUNT(*) FROM audit_logs WHERE action LIKE 'RISK_%' AND timestamp >= ?")) {
                ps.setString(1, today);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) todayRiskFlags = rs.getInt(1);
                }
            }

            // === Per-election breakdown ===
            String perElectionQuery = """
                    SELECT e.election_id, e.name, e.status,
                           COUNT(ev.voter_id)                              AS enrolled,
                           SUM(CASE WHEN ev.status = 'VOTED' THEN 1 ELSE 0 END) AS voted
                    FROM elections e
                    LEFT JOIN election_voters ev ON e.election_id = ev.election_id
                    GROUP BY e.election_id, e.name, e.status
                    ORDER BY e.created_at DESC
                    LIMIT 20
                    """;

            try (Statement s = c.createStatement();
                 ResultSet rs = s.executeQuery(perElectionQuery)) {
                while (rs.next()) {
                    perElection.add(new ElectionStat(
                            rs.getString("election_id"),
                            rs.getString("name"),
                            rs.getString("status"),
                            rs.getInt("enrolled"),
                            rs.getInt("voted")
                    ));
                }
            }

            double overallTurnoutPct = totalMembers == 0 ? 0.0
                    : (totalVoted * 100.0 / totalMembers);

            return new ElectionContext(
                    totalElections, openElections, draftElections, closedElections,
                    totalMembers, totalVoted, overallTurnoutPct,
                    todayRiskFlags, perElection);

        } catch (SQLException e) {
            System.err.println("[ElectionStatsDAO] Failed to build context: " + e.getMessage());
            return new ElectionContext(0, 0, 0, 0, 0, 0, 0.0, 0, List.of());
        }
    }
}
