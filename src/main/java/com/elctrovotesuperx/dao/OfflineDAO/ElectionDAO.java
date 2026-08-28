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