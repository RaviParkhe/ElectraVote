package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.AuditLogEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    // =====================================================
    // LOG WITHOUT OFFICER (backward-compatible)
    // =====================================================

    public void log(String action, String voterId, String electionId,
                    String token, String details) throws SQLException {
        log(action, voterId, electionId, token, details, null);
    }

    // =====================================================
    // LOG WITH OFFICER EMAIL (officer accountability)
    // =====================================================

    public void log(String action, String voterId, String electionId,
                    String token, String details, String officerEmail) throws SQLException {
        String sql = "INSERT INTO audit_logs(action,voter_id,election_id,token,details,officer_email,created_at) " +
                     "VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, voterId);
            ps.setString(3, electionId);
            ps.setString(4, token);
            ps.setString(5, details);
            ps.setString(6, officerEmail);
            ps.setString(7, LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }

    // =====================================================
    // GET ALL LOGS
    // =====================================================

    public List<AuditLogEntry> getAllLogs() {
        List<AuditLogEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY id DESC";

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] Error retrieving logs: " + e.getMessage());
        }
        return list;
    }

    // =====================================================
    // GET LOGS BY ACTION
    // =====================================================

    public List<AuditLogEntry> getLogsByAction(String action) {
        List<AuditLogEntry> list = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE UPPER(action) = ? ORDER BY id DESC";

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, action.toUpperCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] Error filtering logs by action: " + e.getMessage());
        }
        return list;
    }

    // =====================================================
    // SEARCH LOGS
    // =====================================================

    public List<AuditLogEntry> searchLogs(String query) {
        List<AuditLogEntry> list = new ArrayList<>();
        if (query == null || query.isBlank()) {
            return getAllLogs();
        }

        String sql = """
                SELECT * FROM audit_logs
                WHERE action LIKE ? OR voter_id LIKE ? OR election_id LIKE ? OR token LIKE ? OR details LIKE ?
                ORDER BY id DESC
                """;

        String pattern = "%" + query.trim() + "%";

        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] Error searching logs: " + e.getMessage());
        }
        return list;
    }

    // =====================================================
    // COUNT LOGS
    // =====================================================

    public int countLogs() {
        String sql = "SELECT COUNT(*) FROM audit_logs";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[AuditLogDAO] Error counting logs: " + e.getMessage());
        }
        return 0;
    }

    // =====================================================
    // MAP ROW
    // =====================================================

    private AuditLogEntry mapRow(ResultSet rs) throws SQLException {
        return new AuditLogEntry(
                rs.getInt("id"),
                rs.getString("action"),
                rs.getString("voter_id"),
                rs.getString("election_id"),
                rs.getString("token"),
                rs.getString("details"),
                rs.getString("created_at")
        );
    }
}
