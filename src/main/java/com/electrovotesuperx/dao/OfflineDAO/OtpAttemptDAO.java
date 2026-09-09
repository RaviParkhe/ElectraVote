package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Data access layer for the otp_attempts table.
 *
 * Every OTP send and verification attempt is logged here.
 * RiskAnalysisService queries these to detect:
 *
 *   - Repeated OTP failures  → lock after 5 consecutive failures (30 min)
 *   - OTP request flooding   → flag if 10+ requests in 2 min
 *   - Unusual officer speed  → flag if 200+ verifications in 60 min
 */
public class OtpAttemptDAO {

    // =========================================================
    // LOG ATTEMPT
    // =========================================================

    public void logAttempt(
            String voterId,
            String electionId,
            String officerEmail,
            boolean success) throws SQLException {

        String sql = """
                INSERT INTO otp_attempts
                    (voter_id, election_id, officer_email, success, attempted_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, voterId);
            statement.setString(2, electionId);
            statement.setString(3, officerEmail);
            statement.setInt(4, success ? 1 : 0);
            statement.setString(5, LocalDateTime.now().toString());
            statement.executeUpdate();
        }
    }

    // =========================================================
    // COUNT CONSECUTIVE FAILURES (since last success or ever)
    // =========================================================

    public int countRecentFailures(
            String voterId,
            String electionId) throws SQLException {

        // Find last success timestamp
        String lastSuccessQuery = """
                SELECT attempted_at FROM otp_attempts
                WHERE voter_id = ? AND election_id = ? AND success = 1
                ORDER BY attempted_at DESC LIMIT 1
                """;

        String lastSuccessAt = null;
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(lastSuccessQuery)) {
            ps.setString(1, voterId);
            ps.setString(2, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastSuccessAt = rs.getString("attempted_at");
            }
        }

        String failureQuery = lastSuccessAt != null
                ? """
                  SELECT COUNT(*) FROM otp_attempts
                  WHERE voter_id = ? AND election_id = ? AND success = 0
                    AND attempted_at > ?
                  """
                : """
                  SELECT COUNT(*) FROM otp_attempts
                  WHERE voter_id = ? AND election_id = ? AND success = 0
                  """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(failureQuery)) {
            ps.setString(1, voterId);
            ps.setString(2, electionId);
            if (lastSuccessAt != null) ps.setString(3, lastSuccessAt);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // =========================================================
    // COUNT RECENT REQUESTS (flood detection)
    // =========================================================

    public int countRecentRequests(
            String voterId,
            String electionId,
            int windowMinutes) throws SQLException {

        String cutoff = LocalDateTime.now().minusMinutes(windowMinutes).toString();
        String sql = """
                SELECT COUNT(*) FROM otp_attempts
                WHERE voter_id = ? AND election_id = ? AND attempted_at >= ?
                """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, voterId);
            ps.setString(2, electionId);
            ps.setString(3, cutoff);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // =========================================================
    // COUNT OFFICER VERIFICATIONS (velocity detection)
    // =========================================================

    public int countOfficerVerifications(
            String officerEmail,
            int windowMinutes) throws SQLException {

        if (officerEmail == null || officerEmail.isBlank()) return 0;

        String cutoff = LocalDateTime.now().minusMinutes(windowMinutes).toString();
        String sql = """
                SELECT COUNT(*) FROM otp_attempts
                WHERE officer_email = ? AND attempted_at >= ?
                """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, officerEmail);
            ps.setString(2, cutoff);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    // =========================================================
    // IS VOTER LOCKED?
    //
    // Lock is triggered by 5+ consecutive failures.
    // Lock auto-expires 30 minutes after the triggering failure.
    // =========================================================

    public boolean isLocked(
            String voterId,
            String electionId) throws SQLException {

        if (countRecentFailures(voterId, electionId) < 5) return false;

        // Find timestamp of the 5th-most-recent failure
        String sql = """
                SELECT attempted_at FROM otp_attempts
                WHERE voter_id = ? AND election_id = ? AND success = 0
                ORDER BY attempted_at DESC LIMIT 1 OFFSET 4
                """;

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, voterId);
            ps.setString(2, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        LocalDateTime lockTime = LocalDateTime.parse(rs.getString("attempted_at"));
                        return LocalDateTime.now().isBefore(lockTime.plusMinutes(30));
                    } catch (Exception ignored) {
                        return true; // conservative
                    }
                }
            }
        }
        return false;
    }
}
