package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.Member;
import com.electrovotesuperx.model.OfflineModel.Voter;
import com.electrovotesuperx.utils.EncryptionUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoterDAO {

    // =====================================================
    // FIND VOTER BY VOTER ID (DECRYPTED)
    // =====================================================

    public Voter findById(String voterId) throws SQLException {

        String sql = """
                SELECT
                    voter_id,
                    full_name,
                    phone,
                    status
                FROM voters
                WHERE voter_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, voterId.trim());

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Voter(
                            result.getString("voter_id"),
                            EncryptionUtil.decryptSafe(result.getString("full_name")),
                            EncryptionUtil.decryptSafe(result.getString("phone")),
                            result.getString("status"));
                }
            }
        }

        return null;
    }

    // =====================================================
    // GET ALL VOTERS (DECRYPTED OR RAW)
    // =====================================================

    public List<Member> getAllVoters(boolean decrypted) throws SQLException {
        List<Member> list = new ArrayList<>();

        String sql = """
                SELECT
                    v.voter_id,
                    v.full_name,
                    v.email,
                    v.gender,
                    v.phone,
                    v.status,
                    (SELECT GROUP_CONCAT(election_id, ', ') FROM election_voters ev WHERE ev.voter_id = v.voter_id) AS elections
                FROM voters v
                ORDER BY v.rowid DESC
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String elections = rs.getString("elections");

                if (decrypted) {
                    name = EncryptionUtil.decryptSafe(name);
                    email = EncryptionUtil.decryptSafe(email);
                    phone = EncryptionUtil.decryptSafe(phone);
                }

                Member m = new Member(
                        rs.getString("voter_id"),
                        name,
                        email,
                        rs.getString("gender"),
                        phone,
                        rs.getString("status"),
                        elections != null ? elections : "None"
                );
                list.add(m);
            }
        }

        return list;
    }

    // =====================================================
    // INSERT SINGLE VOTER (ENCRYPTED AT REST)
    // =====================================================

    public String insertVoterEncrypted(String name, String email, String gender,
                                      String phone, String electionId) throws SQLException {

        String voterId = "VOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String encName = EncryptionUtil.encrypt(name != null ? name.trim() : "");
        String encEmail = (email != null && !email.isBlank()) ? EncryptionUtil.encrypt(email.trim()) : null;
        String encPhone = EncryptionUtil.encrypt(phone != null ? phone.trim() : "");
        String cleanGender = (gender != null && !gender.isBlank()) ? gender.trim() : "Other";

        String insertVoterSql = """
                INSERT INTO voters (voter_id, full_name, email, gender, phone, status, joined_date)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?)
                """;

        String linkSql = """
                INSERT OR IGNORE INTO election_voters (election_id, voter_id, status)
                VALUES (?, ?, 'NOT_VOTED')
                """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(insertVoterSql)) {
                    ps.setString(1, voterId);
                    ps.setString(2, encName);
                    ps.setString(3, encEmail);
                    ps.setString(4, cleanGender);
                    ps.setString(5, encPhone);
                    ps.setString(6, LocalDate.now().toString());
                    ps.executeUpdate();
                }

                if (electionId != null && !electionId.isBlank()) {
                    try (PreparedStatement psLink = conn.prepareStatement(linkSql)) {
                        psLink.setString(1, electionId.trim());
                        psLink.setString(2, voterId);
                        psLink.executeUpdate();
                    }
                }

                conn.commit();
                return voterId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // =====================================================
    // BATCH IMPORT VOTERS FROM CSV/LIST (ENCRYPTED)
    // =====================================================

    public static class BatchImportResult {
        public int imported = 0;
        public int skipped = 0;
        public List<String> errors = new ArrayList<>();
    }

    public BatchImportResult batchImportVoters(List<String[]> voterRows, String electionId) {
        BatchImportResult result = new BatchImportResult();
        if (voterRows == null || voterRows.isEmpty()) {
            return result;
        }

        String insertVoterSql = """
                INSERT INTO voters (voter_id, full_name, email, gender, phone, status, joined_date)
                VALUES (?, ?, ?, ?, ?, 'ACTIVE', ?)
                """;

        String linkSql = """
                INSERT OR IGNORE INTO election_voters (election_id, voter_id, status)
                VALUES (?, ?, 'NOT_VOTED')
                """;

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psVoter = conn.prepareStatement(insertVoterSql);
                 PreparedStatement psLink = conn.prepareStatement(linkSql)) {

                String today = LocalDate.now().toString();

                for (String[] row : voterRows) {
                    if (row.length < 1 || row[0] == null || row[0].trim().isEmpty()) {
                        result.skipped++;
                        continue;
                    }

                    String name = row[0].trim();
                    String email = row.length > 1 && row[1] != null ? row[1].trim() : "";
                    String gender = row.length > 2 && row[2] != null ? row[2].trim() : "Other";
                    String phone = row.length > 3 && row[3] != null ? row[3].trim() : "";
                    String rowElectionId = (row.length > 4 && row[4] != null && !row[4].isBlank())
                            ? row[4].trim()
                            : electionId;

                    String voterId = "VOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    String encName = EncryptionUtil.encrypt(name);
                    String encEmail = !email.isEmpty() ? EncryptionUtil.encrypt(email) : null;
                    String encPhone = !phone.isEmpty() ? EncryptionUtil.encrypt(phone) : "";

                    psVoter.setString(1, voterId);
                    psVoter.setString(2, encName);
                    psVoter.setString(3, encEmail);
                    psVoter.setString(4, gender);
                    psVoter.setString(5, encPhone);
                    psVoter.setString(6, today);
                    psVoter.addBatch();

                    if (rowElectionId != null && !rowElectionId.isBlank()) {
                        psLink.setString(1, rowElectionId.trim());
                        psLink.setString(2, voterId);
                        psLink.addBatch();
                    }

                    result.imported++;
                }

                psVoter.executeBatch();
                psLink.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                result.errors.add("Database transaction error: " + e.getMessage());
                result.imported = 0;
            }

        } catch (SQLException e) {
            result.errors.add("Connection error: " + e.getMessage());
        }

        return result;
    }

    // =====================================================
    // UPDATE VOTER STATUS (ACTIVE / SUSPENDED)
    // =====================================================

    public boolean updateStatus(String voterId, String status) throws SQLException {
        String sql = "UPDATE voters SET status = ? WHERE voter_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.toUpperCase().trim());
            ps.setString(2, voterId.trim());
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================
    // DELETE VOTER
    // =====================================================

    public boolean deleteVoter(String voterId) throws SQLException {
        String deleteTokens = "DELETE FROM tokens WHERE voter_id = ?";
        String deleteElectionVoters = "DELETE FROM election_voters WHERE voter_id = ?";
        String deleteVoters = "DELETE FROM voters WHERE voter_id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(deleteTokens)) {
                    ps.setString(1, voterId.trim());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(deleteElectionVoters)) {
                    ps.setString(1, voterId.trim());
                    ps.executeUpdate();
                }
                int count;
                try (PreparedStatement ps = conn.prepareStatement(deleteVoters)) {
                    ps.setString(1, voterId.trim());
                    count = ps.executeUpdate();
                }
                conn.commit();
                return count > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // =====================================================
    // CHECK WHETHER VOTER EXISTS
    // =====================================================

    public boolean exists(String voterId) throws SQLException {
        String sql = "SELECT 1 FROM voters WHERE voter_id = ? LIMIT 1";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, voterId.trim());
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    // =====================================================
    // CHECK WHETHER VOTER IS ACTIVE
    // =====================================================

    public boolean isActive(String voterId) throws SQLException {
        String sql = "SELECT status FROM voters WHERE voter_id = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, voterId.trim());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return "ACTIVE".equalsIgnoreCase(result.getString("status"));
                }
            }
        }
        return false;
    }

    // =====================================================
    // COUNT ALL VOTERS
    // =====================================================

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM voters";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return result.getInt(1);
            }
        }
        return 0;
    }
}