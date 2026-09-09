package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.Election;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElectionDAO {

        // =====================================================
        // GET ALL ELECTIONS
        // =====================================================

        public List<Election> getAllElections()
                        throws SQLException {

                List<Election> elections = new ArrayList<>();

                String sql = """
                                    SELECT election_id, name, status
                                    FROM elections
                                    ORDER BY name
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet resultSet = statement.executeQuery()) {

                        while (resultSet.next()) {

                                Election election = new Election(

                                                resultSet.getString("election_id"),

                                                resultSet.getString("name"),

                                                resultSet.getString("status"));

                                elections.add(election);
                        }
                }

                return elections;
        }

        // =====================================================
        // GET ONLY ACTIVE / OPEN ELECTIONS
        // =====================================================

        public List<Election> findOpenElections()
                        throws SQLException {

                List<Election> elections = new ArrayList<>();

                String sql = """
                                    SELECT election_id, name, status
                                    FROM elections
                                    WHERE status = 'OPEN'
                                    ORDER BY name
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet resultSet = statement.executeQuery()) {

                        while (resultSet.next()) {

                                Election election = new Election(

                                                resultSet.getString("election_id"),

                                                resultSet.getString("name"),

                                                resultSet.getString("status"));

                                elections.add(election);
                        }
                }

                return elections;
        }

        // =====================================================
        // FIND ELECTION BY ID
        // =====================================================

        public Election findById(String electionId)
                        throws SQLException {

                String sql = """
                                    SELECT election_id, name, status
                                    FROM elections
                                    WHERE election_id = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, electionId);

                        try (
                                        ResultSet resultSet = statement.executeQuery()) {

                                if (resultSet.next()) {

                                        return new Election(

                                                        resultSet.getString(
                                                                        "election_id"),

                                                        resultSet.getString(
                                                                        "name"),

                                                        resultSet.getString(
                                                                        "status"));
                                }
                        }
                }

                return null;
        }

        // =====================================================
        // CHECK ELECTION NAME
        // =====================================================

        public boolean electionExists(String name)
                        throws SQLException {

                String sql = """
                                    SELECT COUNT(*)
                                    FROM elections
                                    WHERE name = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, name);

                        try (
                                        ResultSet resultSet = statement.executeQuery()) {

                                if (resultSet.next()) {

                                        return resultSet.getInt(1) > 0;
                                }
                        }
                }

                return false;
        }

        // =====================================================
        // CREATE ELECTION
        // =====================================================

        public void createElection(String name)
                        throws SQLException {

                // Generate simple election ID
                String electionId = generateElectionId();

                String sql = """
                                    INSERT INTO elections
                                    (election_id, name, status)
                                    VALUES (?, ?, ?)
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, electionId);

                        statement.setString(2, name);

                        // New election is ACTIVE / OPEN
                        statement.setString(3, "OPEN");

                        statement.executeUpdate();
                }
        }

        // =====================================================
        // CLOSE ELECTION
        // =====================================================

        public void closeElection(String electionId)
                        throws SQLException {

                String sql = """
                                    UPDATE elections
                                    SET status = 'CLOSED'
                                    WHERE election_id = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, electionId);

                        statement.executeUpdate();
                }
        }

        // =====================================================
        // REOPEN ELECTION
        // =====================================================

        public void reopenElection(String electionId)
                        throws SQLException {

                String sql = """
                                    UPDATE elections
                                    SET status = 'OPEN'
                                    WHERE election_id = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, electionId);

                        statement.executeUpdate();
                }
        }

        // =====================================================
        // OPEN ELECTION (ALIAS FOR reopenElection – BACKWARD COMPAT)
        // =====================================================

        public void openElection(String electionId)
                        throws SQLException {
                reopenElection(electionId);
        }

        // =====================================================
        // UPDATE ELECTION
        // =====================================================

        public boolean updateElection(String electionId, String newName, String newStatus)
                        throws SQLException {

                String sql = """
                                    UPDATE elections
                                    SET name = ?, status = ?
                                    WHERE election_id = ?
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql)) {

                        statement.setString(1, newName.trim());
                        statement.setString(2, newStatus.trim().toUpperCase());
                        statement.setString(3, electionId.trim());

                        return statement.executeUpdate() > 0;
                }
        }

        // =====================================================
        // GET VOTER COUNT FOR ELECTION
        // =====================================================

        public int getVoterCountForElection(String electionId) {
                String sql = "SELECT COUNT(*) FROM election_voters WHERE election_id = ?";

                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setString(1, electionId.trim());
                        try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next()) {
                                        return rs.getInt(1);
                                }
                        }
                } catch (SQLException e) {
                        System.err.println("[ElectionDAO] Error getting voter count: " + e.getMessage());
                }
                return 0;
        }

        // =====================================================
        // DELETE ELECTION
        // =====================================================

        public boolean deleteElection(String electionId) throws SQLException {
                String sql = "DELETE FROM elections WHERE election_id = ?";

                try (Connection conn = DatabaseConfig.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {

                        ps.setString(1, electionId.trim());
                        return ps.executeUpdate() > 0;
                }
        }

        // =====================================================
        // GENERATE ELECTION ID
        // =====================================================

        private String generateElectionId()
                        throws SQLException {

                String sql = """
                                    SELECT COUNT(*)
                                    FROM elections
                                """;

                try (
                                Connection connection = DatabaseConfig.getConnection();

                                PreparedStatement statement = connection.prepareStatement(sql);

                                ResultSet resultSet = statement.executeQuery()) {

                        int count = 0;

                        if (resultSet.next()) {
                                count = resultSet.getInt(1);
                        }

                        return "ELEC" +
                                        String.format(
                                                        "%03d",
                                                        count + 1);
                }
        }
}