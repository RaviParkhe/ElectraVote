package com.electrovotesuperx.service;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.dao.OrganizationDAO.FirestoreDAO;
import com.google.gson.JsonObject;

/**
 * RoleDetector queries Firestore and Firebase Realtime Database after
 * Firebase Authentication to determine whether the user is an
 * admin, voter, polling officer, or unknown (new) user.
 *
 * It prioritizes the role selected by the user on the Login screen if provided.
 * On success it populates SessionManager so the caller can
 * route directly to AdminDashboard or VoterDashboard.
 */
public class RoleDetector {

    // =========================================================
    // ROLE RESULT
    // =========================================================

    public enum RoleResult {
        ADMIN,
        POLLING_OFFICER,
        VOTER,
        UNKNOWN
    }

    // =========================================================
    // DETECT ROLE (OVERLOADS)
    // =========================================================

    public static RoleResult detectRole(
            String uid,
            String email,
            String idToken) {
        return detectRole(uid, email, idToken, null);
    }

    /**
     * Looks up Firestore Organizations, Users/{uid}, and RTDB records.
     * Prioritizes the chosen role from the UI if specified.
     *
     * @param uid           Firebase Auth localId
     * @param email         Authenticated email
     * @param idToken       Firebase Auth idToken
     * @param preferredRole Selected role from UI ("admin", "voter", "offline", "new_user")
     * @return RoleResult.ADMIN, RoleResult.POLLING_OFFICER, RoleResult.VOTER, or RoleResult.UNKNOWN
     */
    public static RoleResult detectRole(
            String uid,
            String email,
            String idToken,
            String preferredRole) {

        try {
            String pref = preferredRole != null ? preferredRole.trim().toLowerCase() : "";

            // ─── 1. User explicitly selected ADMIN ───
            if ("admin".equals(pref)) {
                RoleResult adminRes = checkAdminRole(uid, email, idToken);
                if (adminRes == RoleResult.ADMIN) {
                    return RoleResult.ADMIN;
                }
            }

            // ─── 2. User explicitly selected VOTER ───
            if ("voter".equals(pref)) {
                RoleResult voterRes = checkVoterRole(uid, email, idToken);
                if (voterRes == RoleResult.VOTER) {
                    return RoleResult.VOTER;
                }
                // An admin is also permitted to enter the voter view of their own organization
                RoleResult adminAsVoter = checkAdminRole(uid, email, idToken);
                if (adminAsVoter == RoleResult.ADMIN) {
                    return RoleResult.ADMIN;
                }
            }

            // ─── 3. User explicitly selected OFFLINE / POLLING OFFICER ───
            if ("offline".equals(pref) || "polling_officer".equals(pref)) {
                RoleResult officerRes = checkPollingOfficerRole(uid, email, idToken);
                if (officerRes == RoleResult.POLLING_OFFICER) {
                    return RoleResult.POLLING_OFFICER;
                }
            }

            // ─── 4. General Auto-Detection (Fallback if no role selected or initial match not found) ───

            // Check Admin first (Direct Firestore Organizations & RTDB)
            RoleResult adminFallback = checkAdminRole(uid, email, idToken);
            if (adminFallback == RoleResult.ADMIN) {
                return RoleResult.ADMIN;
            }

            // Check Voter second
            RoleResult voterFallback = checkVoterRole(uid, email, idToken);
            if (voterFallback == RoleResult.VOTER) {
                return RoleResult.VOTER;
            }

            // Check Polling Officer third
            RoleResult officerFallback = checkPollingOfficerRole(uid, email, idToken);
            if (officerFallback == RoleResult.POLLING_OFFICER) {
                return RoleResult.POLLING_OFFICER;
            }

            return RoleResult.UNKNOWN;

        } catch (Exception e) {
            System.err.println("[RoleDetector] Could not detect role: " + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }

    // =========================================================
    // CHECK ADMIN ROLE
    // =========================================================

    private static RoleResult checkAdminRole(String uid, String email, String idToken) {
        try {
            // A. Direct Firestore Organizations collection lookup (matches adminUid or adminEmail)
            JsonObject directOrg = FirestoreDAO.findOrganizationByAdmin(uid, email, idToken);
            if (directOrg != null) {
                String joinCode = FirestoreDAO.getString(directOrg, "joinCode");
                String orgName = FirestoreDAO.getString(directOrg, "organizationName");
                String adminName = FirestoreDAO.getString(directOrg, "adminName");

                SessionManager.idToken = idToken;
                SessionManager.currentRole = "admin";
                SessionManager.joinCode = joinCode != null ? joinCode : "";
                SessionManager.organizationName = orgName != null ? orgName : "Organization";
                SessionManager.adminUid = uid;
                SessionManager.adminEmail = email;
                SessionManager.adminName = (adminName != null && !adminName.isBlank()) ? adminName : "Administrator";
                return RoleResult.ADMIN;
            }

            // B. Multi-Organization Realtime Database Discovery
            java.util.List<com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership> orgs =
                    FirebaseDatabaseService.getUserOrganizations(uid, email, idToken);

            if (orgs != null && !orgs.isEmpty()) {
                for (com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership m : orgs) {
                    if ("ADMIN".equalsIgnoreCase(m.getRole())) {
                        SessionManager.idToken = idToken;
                        SessionManager.currentRole = "admin";
                        SessionManager.joinCode = m.getJoinCode();
                        SessionManager.organizationName = m.getOrganizationName();
                        SessionManager.adminUid = uid;
                        SessionManager.adminEmail = email;
                        SessionManager.adminName = m.getMemberName() != null ? m.getMemberName() : "Administrator";
                        return RoleResult.ADMIN;
                    }
                }
            }

            // C. Firestore Users/{uid} document lookup
            JsonObject userFields = FirestoreDAO.getUser(uid, idToken);
            if (userFields != null) {
                String role = FirestoreDAO.getString(userFields, "role");
                String joinCode = FirestoreDAO.getString(userFields, "organization");
                if ("ADMIN".equalsIgnoreCase(role) && joinCode != null && !joinCode.isBlank()) {
                    RoleResult res = populateAdmin(uid, email, joinCode, idToken);
                    if (res == RoleResult.ADMIN) return RoleResult.ADMIN;
                }
            }
        } catch (Exception e) {
            System.err.println("[RoleDetector] Admin check note: " + e.getMessage());
        }
        return RoleResult.UNKNOWN;
    }

    // =========================================================
    // CHECK VOTER ROLE
    // =========================================================

    private static RoleResult checkVoterRole(String uid, String email, String idToken) {
        try {
            // A. Realtime Database memberships
            java.util.List<com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership> orgs =
                    FirebaseDatabaseService.getUserOrganizations(uid, email, idToken);

            if (orgs != null && !orgs.isEmpty()) {
                // Priority 1: Accepted or verified voter
                com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership bestVoter = null;
                for (com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership m : orgs) {
                    if ("VOTER".equalsIgnoreCase(m.getRole()) &&
                            ("ACCEPTED".equalsIgnoreCase(m.getStatus()) || "VERIFIED".equalsIgnoreCase(m.getStatus()))) {
                        bestVoter = m;
                        break;
                    }
                }
                // Priority 2: Any voter membership (e.g. PENDING)
                if (bestVoter == null) {
                    for (com.electrovotesuperx.model.OnlineVotingModel.UserOrganizationMembership m : orgs) {
                        if ("VOTER".equalsIgnoreCase(m.getRole())) {
                            bestVoter = m;
                            break;
                        }
                    }
                }

                if (bestVoter != null) {
                    SessionManager.idToken = idToken;
                    SessionManager.currentRole = "voter";
                    SessionManager.joinCode = bestVoter.getJoinCode();
                    SessionManager.organizationName = bestVoter.getOrganizationName();
                    SessionManager.voterUid = uid;
                    SessionManager.voterEmail = email;
                    String vName = bestVoter.getMemberName() != null && !bestVoter.getMemberName().isBlank()
                            ? bestVoter.getMemberName()
                            : (email != null && email.contains("@") ? email.split("@")[0] : "Voter");
                    SessionManager.voterName = vName;
                    SessionManager.voterStatus = bestVoter.getStatus();
                    return RoleResult.VOTER;
                }
            }

            // B. Firestore Users/{uid} document lookup
            JsonObject userFields = FirestoreDAO.getUser(uid, idToken);
            if (userFields != null) {
                String role = FirestoreDAO.getString(userFields, "role");
                String joinCode = FirestoreDAO.getString(userFields, "organization");
                if ("VOTER".equalsIgnoreCase(role) && joinCode != null && !joinCode.isBlank()) {
                    RoleResult res = populateVoter(uid, email, joinCode, idToken);
                    if (res == RoleResult.VOTER) return RoleResult.VOTER;
                }
            }
        } catch (Exception e) {
            System.err.println("[RoleDetector] Voter check note: " + e.getMessage());
        }
        return RoleResult.UNKNOWN;
    }

    // =========================================================
    // CHECK POLLING OFFICER ROLE
    // =========================================================

    private static RoleResult checkPollingOfficerRole(String uid, String email, String idToken) {
        try {
            // A. Direct Firestore PollingOfficers/{uid} lookup
            JsonObject officerFields = FirestoreDAO.getPollingOfficer(uid, idToken);
            if (officerFields != null) {
                String name = FirestoreDAO.getString(officerFields, "name");
                SessionManager.idToken = idToken;
                SessionManager.currentRole = "polling_officer";
                SessionManager.officerUid = uid;
                SessionManager.officerEmail = email;
                SessionManager.officerName = (name != null && !name.isBlank()) ? name : "Polling Officer";
                return RoleResult.POLLING_OFFICER;
            }

            // B. Firestore Users/{uid} document lookup
            JsonObject userFields = FirestoreDAO.getUser(uid, idToken);
            if (userFields != null) {
                String role = FirestoreDAO.getString(userFields, "role");
                String joinCode = FirestoreDAO.getString(userFields, "organization");
                if ("POLLING_OFFICER".equalsIgnoreCase(role) || "OFFICER".equalsIgnoreCase(role)) {
                    RoleResult res = populatePollingOfficer(uid, email, joinCode != null ? joinCode : "", idToken);
                    if (res == RoleResult.POLLING_OFFICER) return RoleResult.POLLING_OFFICER;
                }
            }
        } catch (Exception e) {
            System.err.println("[RoleDetector] Polling Officer check note: " + e.getMessage());
        }
        return RoleResult.UNKNOWN;
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
            if (storedAdminUid != null && !storedAdminUid.isBlank() && !uid.equals(storedAdminUid)) {
                return RoleResult.UNKNOWN;
            }

            if (adminName == null || adminName.isBlank()) {
                adminName = "Administrator";
            }

            // Populate SessionManager
            SessionManager.idToken = idToken;
            SessionManager.currentRole = "admin";
            SessionManager.joinCode = joinCode;
            SessionManager.organizationName = organizationName != null ? organizationName : "Organization";
            SessionManager.adminUid = uid;
            SessionManager.adminEmail = email;
            SessionManager.adminName = adminName;

            return RoleResult.ADMIN;

        } catch (Exception e) {
            System.err.println("[RoleDetector] Admin lookup failed: " + e.getMessage());
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

            String orgName = org != null && org.has("organizationName")
                    ? org.get("organizationName").getAsString()
                    : joinCode;

            // Read RTDB member record
            JsonObject member =
                    FirebaseDatabaseService.getMember(
                            joinCode, uid, idToken);

            String status = "PENDING";
            String fullName = email != null && email.contains("@") ? email.split("@")[0] : "Voter";
            String phone = "";

            if (member != null) {
                status = member.has("status")
                        ? member.get("status").getAsString()
                        : "PENDING";

                fullName = member.has("name")
                        ? member.get("name").getAsString()
                        : (member.has("fullName")
                                ? member.get("fullName").getAsString()
                                : email);

                phone = member.has("phone")
                        ? member.get("phone").getAsString()
                        : "";
            }

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
            System.err.println("[RoleDetector] Voter lookup failed: " + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }

    // =========================================================
    // POPULATE POLLING OFFICER SESSION
    // =========================================================

    private static RoleResult populatePollingOfficer(
            String uid,
            String email,
            String joinCode,
            String idToken) {

        try {
            JsonObject orgFields = (joinCode != null && !joinCode.isBlank())
                    ? FirestoreDAO.getOrganization(joinCode, idToken)
                    : null;

            String organizationName = orgFields != null
                    ? FirestoreDAO.getString(orgFields, "organizationName")
                    : (joinCode != null && !joinCode.isBlank() ? joinCode : "Offline Portal");

            JsonObject userFields = FirestoreDAO.getUser(uid, idToken);
            String name = userFields != null ? FirestoreDAO.getString(userFields, "name") : null;
            if (name == null || name.isBlank()) {
                name = userFields != null ? FirestoreDAO.getString(userFields, "fullName") : "Polling Officer";
            }

            SessionManager.idToken = idToken;
            SessionManager.currentRole = "polling_officer";
            SessionManager.joinCode = joinCode != null ? joinCode : "";
            SessionManager.organizationName = organizationName;
            SessionManager.officerUid = uid;
            SessionManager.officerEmail = email;
            SessionManager.officerName = name;

            return RoleResult.POLLING_OFFICER;

        } catch (Exception e) {
            System.err.println("[RoleDetector] Polling Officer lookup failed: " + e.getMessage());
            return RoleResult.UNKNOWN;
        }
    }
}
