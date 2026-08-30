package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDateTime;

public class AuditLogDAO {

    public void log(String action, String voterId, String electionId,
                    String token, String details) throws SQLException {
        String sql = "INSERT INTO audit_logs(action,voter_id,election_id,token,details,created_at) " +
                     "VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setString(2, voterId);
            ps.setString(3, electionId);
            ps.setString(4, token);
            ps.setString(5, details);
            ps.setString(6, LocalDateTime.now().toString());
            ps.executeUpdate();
        }
    }
}
