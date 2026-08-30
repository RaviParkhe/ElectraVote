package com.electrovotesuperx.dao.OrganizationDAO;

import com.electrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.electrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.electrovotesuperx.model.OnlineVotingModel.Voter;
import com.google.gson.JsonObject;

/**
 * VoterDAO handles all Firebase REST API calls for voter authentication
 * and registration. Uses FirebaseAuthService (Auth REST API) and
 * FirebaseDatabaseService (Realtime Database REST API).
 *
 * Data structure in Firebase Realtime Database:
 *   /organizations/{joinCode}              → org info
 *   /organizations/{joinCode}/members/{uid} → member info (role: VOTER or ADMIN)
 */
public class VoterDAO {

    // =========================================================
    // SIGN IN RESULT
    // =========================================================

    public static class SignInResult {

        private final boolean success;
        private final String message;
        private final Voter voter;
        private final String idToken;

        private SignInResult(boolean success, String message, Voter voter, String idToken) {
            this.success = success;
            this.message = message;
            this.voter = voter;
            this.idToken = idToken;
        }

        public static SignInResult success(Voter voter) {
            return new SignInResult(true, null, voter, null);
        }

        public static SignInResult success(Voter voter, String idToken) {
            return new SignInResult(true, null, voter, idToken);
        }

        public static SignInResult failure(String message) {
            return new SignInResult(false, message, null, null);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Voter getVoter() {
            return voter;
        }

        public String getIdToken() {
            return idToken;
        }
    }

    // =========================================================
    // SIGN IN VOTER
    // Steps:
    //   1. Firebase Auth REST sign-in (get uid + idToken)
    //   2. Fetch org from RTDB to verify joinCode exists
    //   3. Fetch member from RTDB to verify role = VOTER
    // =========================================================

    public SignInResult signIn(
            String email,
            String password,
            String joinCode) throws Exception {

        // STEP 1: Firebase Authentication
        FirebaseAuthService.AuthResult auth =
                FirebaseAuthService.signIn(email, password);

        if (!auth.isSuccess()) {
            return SignInResult.failure(auth.getMessage());
        }

        String uid = auth.getLocalId();
        String idToken = auth.getIdToken();

        // STEP 2: Verify the organization join code exists
        JsonObject org = FirebaseDatabaseService.getOrganization(joinCode, idToken);

        if (org == null) {
            return SignInResult.failure(
                    "Invalid join code. No organization found with code: " + joinCode);
        }

        // STEP 3: Check voter membership
        JsonObject member = FirebaseDatabaseService.getMember(joinCode, uid, idToken);

        if (member == null) {
            return SignInResult.failure(
                    "You are not a member of this organization. Please sign up first.");
        }

        // STEP 4: Verify role is VOTER (not ADMIN)
        String role = member.has("role")
                ? member.get("role").getAsString()
                : null;

        if (!"VOTER".equalsIgnoreCase(role)) {
            return SignInResult.failure(
                    "Access denied. Please use the Organization Sign-In for admin access.");
        }

        // STEP 5: Verify status is ACCEPTED
        String status = member.has("status")
                ? member.get("status").getAsString()
                : "PENDING";

        if ("PENDING".equalsIgnoreCase(status)) {
            return SignInResult.failure(
                    "Your voter registration is pending approval by the organization administrator. Please wait for verification.");
        }

        if ("REJECTED".equalsIgnoreCase(status)) {
            return SignInResult.failure(
                    "Your voter registration request has been rejected by the administrator.");
        }

        // STEP 6: Build Voter object
        String orgName = org.has("organizationName")
                ? org.get("organizationName").getAsString()
                : joinCode;

        String memberName = member.has("name")
                ? member.get("name").getAsString()
                : (member.has("fullName") ? member.get("fullName").getAsString() : email);

        String phone = member.has("phone") ? member.get("phone").getAsString() : "";

        Voter voter = new Voter();
        voter.setUid(uid);
        voter.setEmail(email);
        voter.setFullName(memberName);
        voter.setJoinCode(joinCode);
        voter.setRole("VOTER");
        voter.setOrganizationName(orgName);
        voter.setStatus(status);
        voter.setPhone(phone);

        return SignInResult.success(voter, idToken);
    }

    // =========================================================
    // SIGN UP VOTER
    // Steps:
    //   1. Create Firebase Auth user (get uid + idToken)
    //   2. Verify join code exists in RTDB
    //   3. Save voter to RTDB as a member with role VOTER & status PENDING
    // Returns: null on success, error message string on failure
    // =========================================================

    public String signUp(
            String fullName,
            String email,
            String password,
            String phone,
            String category,
            String department,
            String yearOrRole,
            String joinCode) throws Exception {

        // STEP 1: Create or Authenticate Firebase Auth user
        FirebaseAuthService.AuthResult auth =
                FirebaseAuthService.createUser(email, password);

        if (!auth.isSuccess()) {
            // If email is already registered, authenticate with password to link voter membership
            if (auth.getMessage() != null
                    && (auth.getMessage().contains("already registered")
                            || auth.getMessage().contains("EMAIL_EXISTS"))) {
                auth = FirebaseAuthService.signIn(email, password);
            }

            if (!auth.isSuccess()) {
                return auth.getMessage();
            }
        }

        String uid = auth.getLocalId();
        String idToken = auth.getIdToken();

        // STEP 2: Verify the organization join code
        JsonObject org = FirebaseDatabaseService.getOrganization(joinCode, idToken);

        if (org == null) {
            return "Invalid join code. No organization found with code: " + joinCode;
        }

        String voterId = "VOT" + String.format("%03d", (int)(Math.random() * 900) + 100);

        // STEP 3: Save voter membership in RTDB
        boolean saved = FirebaseDatabaseService.saveVoter(
                joinCode, uid, voterId, fullName, email, phone, category, department, yearOrRole, idToken);

        if (!saved) {
            return "Registration failed. Could not save voter data. Please try again.";
        }

        // STEP 4: Save Firestore Users/{uid} for role auto-detection on next login
        try {
            FirestoreDAO.saveUser(uid, fullName, email, "VOTER", joinCode, idToken);
        } catch (Exception e) {
            // Non-critical — voter can still sign in via the manual flow
            System.err.println("[VoterDAO] Could not save Firestore user doc: " + e.getMessage());
        }

        return null; // null = success
    }

    public String signUp(
            String fullName,
            String email,
            String password,
            String joinCode) throws Exception {
        return signUp(fullName, email, password, "", "Student", "General", "Member", joinCode);
    }
}
