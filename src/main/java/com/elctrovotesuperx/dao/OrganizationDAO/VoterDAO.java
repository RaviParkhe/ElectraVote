package com.elctrovotesuperx.dao.OrganizationDAO;

import com.elctrovotesuperx.config.firebaseConfig.FirebaseAuthService;
import com.elctrovotesuperx.config.firebaseConfig.FirebaseDatabaseService;
import com.elctrovotesuperx.model.OnlineVotingModel.Voter;
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

        private SignInResult(boolean success, String message, Voter voter) {
            this.success = success;
            this.message = message;
            this.voter = voter;
        }

        public static SignInResult success(Voter voter) {
            return new SignInResult(true, null, voter);
        }

        public static SignInResult failure(String message) {
            return new SignInResult(false, message, null);
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

        // STEP 5: Build Voter object
        String orgName = org.has("organizationName")
                ? org.get("organizationName").getAsString()
                : joinCode;

        String memberName = member.has("name")
                ? member.get("name").getAsString()
                : email;

        Voter voter = new Voter();
        voter.setUid(uid);
        voter.setEmail(email);
        voter.setFullName(memberName);
        voter.setJoinCode(joinCode);
        voter.setRole("VOTER");
        voter.setOrganizationName(orgName);

        return SignInResult.success(voter);
    }

    // =========================================================
    // SIGN UP VOTER
    // Steps:
    //   1. Create Firebase Auth user (get uid + idToken)
    //   2. Verify join code exists in RTDB
    //   3. Save voter to RTDB as a member with role VOTER
    // Returns: null on success, error message string on failure
    // =========================================================

    public String signUp(
            String fullName,
            String email,
            String password,
            String joinCode) throws Exception {

        // STEP 1: Create Firebase Auth user
        FirebaseAuthService.AuthResult auth =
                FirebaseAuthService.createUser(email, password);

        if (!auth.isSuccess()) {
            return auth.getMessage();
        }

        String uid = auth.getLocalId();
        String idToken = auth.getIdToken();

        // STEP 2: Verify the organization join code
        JsonObject org = FirebaseDatabaseService.getOrganization(joinCode, idToken);

        if (org == null) {
            return "Invalid join code. No organization found with code: " + joinCode;
        }

        // STEP 3: Save voter membership in RTDB
        boolean saved = FirebaseDatabaseService.saveVoter(
                joinCode, uid, fullName, email, idToken);

        if (!saved) {
            return "Registration failed. Could not save voter data. Please try again.";
        }

        return null; // null = success
    }
}
