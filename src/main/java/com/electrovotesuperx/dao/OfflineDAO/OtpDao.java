// package com.electrovotesuperx.dao.OfflineDAO;

// import com.electrovotesuperx.config.DatabaseConfig;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.Statement;

// public class OtpDAO {

// // =====================================================
// // CONSTRUCTOR
// // =====================================================

// public OtpDAO() {

// try {
// createTable();
// } catch (SQLException e) {
// e.printStackTrace();
// }
// }

// // =====================================================
// // CREATE OTP TABLE
// // =====================================================

// private void createTable() throws SQLException {

// String sql = """
// CREATE TABLE IF NOT EXISTS voter_otps (
// election_id TEXT NOT NULL,
// voter_id TEXT NOT NULL,
// otp_code TEXT NOT NULL,
// created_at INTEGER NOT NULL,
// expires_at INTEGER NOT NULL,
// verified INTEGER NOT NULL DEFAULT 0,
// PRIMARY KEY (election_id, voter_id)
// )
// """;

// try (
// Connection connection = DatabaseConfig.getConnection();
// Statement statement = connection.createStatement()) {

// statement.execute(sql);
// }
// }

// // =====================================================
// // SAVE / REPLACE LATEST OTP
// //
// // Because election_id + voter_id is PRIMARY KEY,
// // inserting another OTP replaces the previous OTP.
// // =====================================================

// public void saveOtp(
// String electionId,
// String voterId,
// String otp,
// long createdAt,
// long expiresAt)
// throws SQLException {

// String sql = """
// INSERT INTO voter_otps
// (
// election_id,
// voter_id,
// otp_code,
// created_at,
// expires_at,
// verified
// )
// VALUES (?, ?, ?, ?, ?, 0)

// ON CONFLICT(election_id, voter_id)
// DO UPDATE SET
// otp_code = excluded.otp_code,
// created_at = excluded.created_at,
// expires_at = excluded.expires_at,
// verified = 0
// """;

// try (
// Connection connection = DatabaseConfig.getConnection();
// PreparedStatement statement = connection.prepareStatement(sql)) {

// statement.setString(1, electionId);
// statement.setString(2, voterId);
// statement.setString(3, otp);
// statement.setLong(4, createdAt);
// statement.setLong(5, expiresAt);

// statement.executeUpdate();
// }
// }

// // =====================================================
// // FIND OTP
// // =====================================================

// public OtpRecord find(
// String electionId,
// String voterId)
// throws SQLException {

// String sql = """
// SELECT
// election_id,
// voter_id,
// otp_code,
// created_at,
// expires_at,
// verified
// FROM voter_otps
// WHERE election_id = ?
// AND voter_id = ?
// """;

// try (
// Connection connection = DatabaseConfig.getConnection();
// PreparedStatement statement = connection.prepareStatement(sql)) {

// statement.setString(1, electionId);
// statement.setString(2, voterId);

// try (ResultSet result = statement.executeQuery()) {

// if (result.next()) {

// return new OtpRecord(
// result.getString("election_id"),
// result.getString("voter_id"),
// result.getString("otp_code"),
// result.getLong("created_at"),
// result.getLong("expires_at"),
// result.getInt("verified") == 1);
// }
// }
// }

// return null;
// }

// // =====================================================
// // MARK OTP VERIFIED
// // =====================================================

// public void markVerified(
// String electionId,
// String voterId)
// throws SQLException {

// String sql = """
// UPDATE voter_otps
// SET verified = 1
// WHERE election_id = ?
// AND voter_id = ?
// """;

// try (
// Connection connection = DatabaseConfig.getConnection();
// PreparedStatement statement = connection.prepareStatement(sql)) {

// statement.setString(1, electionId);
// statement.setString(2, voterId);

// statement.executeUpdate();
// }
// }

// // =====================================================
// // OTP RECORD
// // =====================================================

// public record OtpRecord(
// String electionId,
// String voterId,
// String otpCode,
// long createdAt,
// long expiresAt,
// boolean verified) {
// }
// }