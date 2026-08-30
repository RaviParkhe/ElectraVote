package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.ElectionVoter;

import java.sql.*;

public class ElectionVoterDAO {

    public ElectionVoter find(String electionId, String voterId) throws SQLException {
        String sql = "SELECT election_id, voter_id, status FROM election_voters " +
                     "WHERE election_id=? AND voter_id=?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, electionId);
            ps.setString(2, voterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ElectionVoter(
                            rs.getString("election_id"),
                            rs.getString("voter_id"),
                            rs.getString("status")
                    );
                }
            }
        }
        return null;
    }

    public void markVoted(String electionId, String voterId) throws SQLException {
        String sql = "UPDATE election_voters SET status='VOTED' " +
                     "WHERE election_id=? AND voter_id=? AND status='NOT_VOTED'";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, electionId);
            ps.setString(2, voterId);
            ps.executeUpdate();
        }
    }

    public int countVoted(String electionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM election_voters WHERE election_id=? AND status='VOTED'";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public int countEligible(String electionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM election_voters WHERE election_id=?";
        try (Connection c = DatabaseConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, electionId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
