package com.electrovotesuperx.dao.OfflineDAO;

import com.electrovotesuperx.config.DatabaseConfig;
import com.electrovotesuperx.model.OfflineModel.PollingOfficerRequest;

import java.security.SecureRandom;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PollingOfficerDAO {

    private static final SecureRandom RANDOM = new SecureRandom();

    // =========================================================
    // INSERT OR UPDATE REQUEST
    // =========================================================

    public boolean saveRequest(String uid, String name, String email,
                               String stationName, String phone, String status,
                               String approvalPin) {

        String checkSql = "SELECT id FROM polling_officer_requests WHERE email = ? OR uid = ?";
        String updateSql = """
                UPDATE polling_officer_requests
                SET name = ?, station_name = ?, phone = ?, status = ?, approval_pin = ?, pin_generated_at = ?
                WHERE email = ? OR uid = ?
                """;
        String insertSql = """
                INSERT INTO polling_officer_requests
                (uid, name, email, station_name, phone, status, approval_pin, created_at, pin_generated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String now = LocalDateTime.now().toString();

        try (Connection conn = DatabaseConfig.getConnection()) {
            Integer existingId = null;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, email != null ? email.trim() : "");
                checkPs.setString(2, uid != null ? uid.trim() : "");
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getInt("id");
                    }
                }
            }

            if (existingId != null) {
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setString(1, name);
                    updatePs.setString(2, stationName);
                    updatePs.setString(3, phone);
                    updatePs.setString(4, status != null ? status : "PENDING");
                    updatePs.setString(5, approvalPin);
                    updatePs.setString(6, now);
                    updatePs.setString(7, email);
                    updatePs.setString(8, uid);
                    return updatePs.executeUpdate() > 0;
                }
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setString(1, uid);
                    insertPs.setString(2, name);
                    insertPs.setString(3, email);
                    insertPs.setString(4, stationName);
                    insertPs.setString(5, phone);
                    insertPs.setString(6, status != null ? status : "PENDING");
                    insertPs.setString(7, approvalPin);
                    insertPs.setString(8, now);
                    insertPs.setString(9, now);
                    return insertPs.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error saving request: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // RENEW APPROVAL PIN (8-HOUR FRESH PIN)
    // =========================================================

    public String renewApprovalPin(String uidOrEmail) {
        int pinNumber = 100_000 + RANDOM.nextInt(900_000);
        String newPin = String.valueOf(pinNumber);
        String now = LocalDateTime.now().toString();

        String sql = """
                UPDATE polling_officer_requests
                SET approval_pin = ?, pin_generated_at = ?, status = 'APPROVED', reviewed_at = ?
                WHERE uid = ? OR email = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPin);
            ps.setString(2, now);
            ps.setString(3, now);
            ps.setString(4, uidOrEmail.trim());
            ps.setString(5, uidOrEmail.trim());

            int updated = ps.executeUpdate();
            if (updated > 0) {
                return newPin;
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error renewing PIN: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // VERIFY PIN WITH 8-HOUR EXPIRY
    // =========================================================

    public boolean verifyPinWithExpiry(String uidOrEmail, String pin) {
        if (pin == null || pin.isBlank()) {
            return false;
        }

        PollingOfficerRequest req = findByUidOrEmail(uidOrEmail);
        if (req == null || req.getApprovalPin() == null) {
            return false;
        }

        if (!req.getApprovalPin().trim().equals(pin.trim())) {
            return false;
        }

        // Check if status is approved / accepted
        if (!"APPROVED".equalsIgnoreCase(req.getStatus()) && !"ACCEPTED".equalsIgnoreCase(req.getStatus())) {
            // Auto-approve if correct PIN matches
            updateStatus(uidOrEmail, "APPROVED");
        }

        // Check 8-hour expiry
        return !req.isPinExpired();
    }

    // =========================================================
    // GET ALL REQUESTS
    // =========================================================

    public List<PollingOfficerRequest> getAllRequests() {
        List<PollingOfficerRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM polling_officer_requests ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error getting requests: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // GET REQUESTS BY STATUS
    // =========================================================

    public List<PollingOfficerRequest> getRequestsByStatus(String status) {
        List<PollingOfficerRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM polling_officer_requests WHERE UPPER(status) = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase().trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error getting requests by status: " + e.getMessage());
        }
        return list;
    }

    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public boolean updateStatus(String uidOrEmail, String newStatus) {
        String sql = """
                UPDATE polling_officer_requests
                SET status = ?, reviewed_at = ?
                WHERE uid = ? OR email = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toUpperCase().trim());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setString(3, uidOrEmail.trim());
            ps.setString(4, uidOrEmail.trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStatusById(int id, String newStatus) {
        String sql = """
                UPDATE polling_officer_requests
                SET status = ?, reviewed_at = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus.toUpperCase().trim());
            ps.setString(2, LocalDateTime.now().toString());
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error updating status by id: " + e.getMessage());
            return false;
        }
    }

    // =========================================================
    // FIND BY UID OR EMAIL
    // =========================================================

    public PollingOfficerRequest findByUidOrEmail(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM polling_officer_requests WHERE uid = ? OR LOWER(email) = LOWER(?) LIMIT 1";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, identifier.trim());
            ps.setString(2, identifier.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error finding request: " + e.getMessage());
        }
        return null;
    }

    // =========================================================
    // COUNT
    // =========================================================

    public int countByStatus(String status) {
        String sql = (status == null || "ALL".equalsIgnoreCase(status))
                ? "SELECT COUNT(*) FROM polling_officer_requests"
                : "SELECT COUNT(*) FROM polling_officer_requests WHERE UPPER(status) = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (status != null && !"ALL".equalsIgnoreCase(status)) {
                ps.setString(1, status.toUpperCase().trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[PollingOfficerDAO] Error counting: " + e.getMessage());
        }
        return 0;
    }

    private PollingOfficerRequest mapRow(ResultSet rs) throws SQLException {
        String pinGenAt = null;
        try {
            pinGenAt = rs.getString("pin_generated_at");
        } catch (SQLException ignored) {}

        return new PollingOfficerRequest(
                rs.getInt("id"),
                rs.getString("uid"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("station_name"),
                rs.getString("phone"),
                rs.getString("status"),
                rs.getString("approval_pin"),
                rs.getString("created_at"),
                rs.getString("reviewed_at"),
                pinGenAt
        );
    }
}
