package com.electrovotesuperx.service;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.google.gson.JsonObject;

/**
 * RoleDetector queries the Firestore Users/{uid} document after
 * Firebase Authentication to determine whether the user is an
 * admin, voter, or unknown (new) user.
 *
 * On success it populates SessionManager so the caller can
 * route directly to AdminDashboard or VoterDashboard.
 */
public class RoleDetector {

    // =========================================================
    // ROLE RESULT
    // =========================================================

    public enum RoleResult {
        ADMIN,
        VOTER,
        UNKNOWN
    }

    // =========================================================
    // DETECT ROLE
    // =========================================================

    /**
     * Looks up the Firestore Users/{uid} document.
     * If a role is found, populates SessionManager and returns
     * the detected role. Returns UNKNOWN if the user has no
     * Firestore record or the role is unrecognized.
     *
     * @param uid     Firebase Auth localId
     * @param email   Authenticated email
     * @param idToken Firebase Auth idToken
     * @return RoleResult.ADMIN, RoleResult.VOTER, or RoleResult.UNKNOWN
     */
    public static RoleResult detectRole(
            String uid,
            String email,
            String idToken) {

        try {

            // ─── Step 1: Read Users/{uid} from Firestore ───

            JsonObject userFields =
                    FirestoreDAO.getUser(uid, idToken);

            if (userFields == null) {
                return RoleResult.UNKNOWN;
            }

            String role = FirestoreDAO.getString(userFields, "role");
            String joinCode = FirestoreDAO.getString(userFields, "organization");

            if (role == null || joinCode == null) {
                return RoleResult.UNKNOWN;
            }

            // ─── Step 2: Route by role ───

            if ("ADMIN".equalsIgnoreCase(role)) {
                return populateAdmin(uid, email, joinCode, idToken);
            }

            if ("VOTER".equalsIgnoreCase(role)) {
                return populateVoter(uid, email, joinCode, idToken);
            }

            return RoleResult.UNKNOWN;

        } catch (Exception e) {

            // Any network or parse failure → fall through to HomePage
            System.err.println(
                    "[RoleDetector] Could not detect role: "
                            + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }

    // =========================================================
    // POPULATE ADMIN SESSION
    // =========================================================

    private static RoleResult populateAdmin(
            String uid,
            String email,
            String joinCode,
            String idToken) {

        try {

            // Read organization document from Firestore
            JsonObject orgFields =
                    FirestoreDAO.getOrganization(joinCode, idToken);

            if (orgFields == null) {
                return RoleResult.UNKNOWN;
            }

            String organizationName =
                    FirestoreDAO.getString(orgFields, "organizationName");

            String adminName =
                    FirestoreDAO.getString(orgFields, "adminName");

            String storedAdminUid =
                    FirestoreDAO.getString(orgFields, "adminUid");

            // Verify this user is actually the admin of this org
            if (!uid.equals(storedAdminUid)) {
                return RoleResult.UNKNOWN;
            }

            if (adminName == null || adminName.isBlank()) {
                adminName = "Administrator";
            }

            // Populate SessionManager
            SessionManager.idToken = idToken;
            SessionManager.currentRole = "admin";
            SessionManager.joinCode = joinCode;
            SessionManager.organizationName = organizationName;
            SessionManager.adminUid = uid;
            SessionManager.adminEmail = email;
            SessionManager.adminName = adminName;

            return RoleResult.ADMIN;

        } catch (Exception e) {

            System.err.println(
                    "[RoleDetector] Admin lookup failed: "
                            + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }

    // =========================================================
    // POPULATE VOTER SESSION
    // =========================================================

    private static RoleResult populateVoter(
            String uid,
            String email,
            String joinCode,
            String idToken) {

        try {

            // Read RTDB organization to get org name
            JsonObject org =
                    FirebaseDatabaseService.getOrganization(
                            joinCode, idToken);

            if (org == null) {
                return RoleResult.UNKNOWN;
            }

            String orgName = org.has("organizationName")
                    ? org.get("organizationName").getAsString()
                    : joinCode;

            // Read RTDB member record
            JsonObject member =
                    FirebaseDatabaseService.getMember(
                            joinCode, uid, idToken);

            if (member == null) {
                return RoleResult.UNKNOWN;
            }

            // Verify role and status
            String memberRole = member.has("role")
                    ? member.get("role").getAsString()
                    : null;

            if (!"VOTER".equalsIgnoreCase(memberRole)) {
                return RoleResult.UNKNOWN;
            }

            String status = member.has("status")
                    ? member.get("status").getAsString()
                    : "PENDING";

            if (!"ACCEPTED".equalsIgnoreCase(status)
                    && !"VERIFIED".equalsIgnoreCase(status)) {
                // Not yet approved — don't auto-route
                return RoleResult.UNKNOWN;
            }

            String fullName = member.has("name")
                    ? member.get("name").getAsString()
                    : (member.has("fullName")
                            ? member.get("fullName").getAsString()
                            : email);

            String phone = member.has("phone")
                    ? member.get("phone").getAsString()
                    : "";

            // Populate SessionManager
            SessionManager.idToken = idToken;
            SessionManager.currentRole = "voter";
            SessionManager.joinCode = joinCode;
            SessionManager.organizationName = orgName;
            SessionManager.voterUid = uid;
            SessionManager.voterEmail = email;
            SessionManager.voterName = fullName;
            SessionManager.voterStatus = status;
            SessionManager.voterPhone = phone;

            return RoleResult.VOTER;

        } catch (Exception e) {

            System.err.println(
                    "[RoleDetector] Voter lookup failed: "
                            + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }
}
