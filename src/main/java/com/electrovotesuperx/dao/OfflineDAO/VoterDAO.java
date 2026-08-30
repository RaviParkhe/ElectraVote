package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.Voter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoterDAO {

    // =====================================================
    // FIND VOTER BY VOTER ID
    //
    // The voter_id is generated when the member is created.
    // It remains the same for that person across elections.
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

            statement.setString(1, voterId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new Voter(
                            result.getString("voter_id"),
                            result.getString("full_name"),
                            result.getString("phone"),
                            result.getString("status"));
                }
            }
        }

        return null;
    }

    // =====================================================
    // CHECK WHETHER VOTER EXISTS
    // =====================================================

    public boolean exists(String voterId) throws SQLException {

        String sql = """
                SELECT 1
                FROM voters
                WHERE voter_id = ?
                LIMIT 1
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, voterId);

            try (ResultSet result = statement.executeQuery()) {

                return result.next();
            }
        }
    }

    // =====================================================
    // CHECK WHETHER VOTER IS ACTIVE
    // =====================================================

    public boolean isActive(String voterId) throws SQLException {

        String sql = """
                SELECT status
                FROM voters
                WHERE voter_id = ?
                """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, voterId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return "ACTIVE".equalsIgnoreCase(
                            result.getString("status"));
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

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()) {

            if (result.next()) {
                return result.getInt(1);
            }
        }

        return 0;
    }
}