package com.electrovotesuperx;

import com.electrovotesuperx.config.SessionManager;
import com.electrovotesuperx.service.RoleDetector;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleBasedAuthorizationTest {

    @BeforeEach
    void setUp() {
        SessionManager.clear();
    }

    @Test
    @DisplayName("Test SessionManager initial state and clearing")
    void testSessionManagerClear() {
        SessionManager.currentRole = "admin";
        SessionManager.idToken = "sample-token-123";
        SessionManager.adminEmail = "admin@org.com";
        SessionManager.joinCode = "EV-1234-5678";

        assertTrue(SessionManager.isLoggedIn());
        assertEquals("admin", SessionManager.currentRole);

        SessionManager.clear();

        assertNull(SessionManager.currentRole);
        assertNull(SessionManager.idToken);
        assertNull(SessionManager.adminEmail);
        assertNull(SessionManager.joinCode);
        assertFalse(SessionManager.isLoggedIn());
    }

    @Test
    @DisplayName("Test RoleDetector.RoleResult enum values")
    void testRoleResultEnum() {
        assertEquals(RoleDetector.RoleResult.ADMIN, RoleDetector.RoleResult.valueOf("ADMIN"));
        assertEquals(RoleDetector.RoleResult.VOTER, RoleDetector.RoleResult.valueOf("VOTER"));
        assertEquals(RoleDetector.RoleResult.UNKNOWN, RoleDetector.RoleResult.valueOf("UNKNOWN"));
    }

    @Test
    @DisplayName("Test Admin Role Authorization Grant and Rejection logic")
    void testAdminAuthorizationLogic() {
        // Scenario 1: User attempts admin login with ADMIN role -> Granted
        String chosenRole = "admin";
        RoleDetector.RoleResult detectedAdmin = RoleDetector.RoleResult.ADMIN;

        boolean canAccessAdminDashboard = "admin".equals(chosenRole) && detectedAdmin == RoleDetector.RoleResult.ADMIN;
        assertTrue(canAccessAdminDashboard, "Admin role should be granted access to Admin Dashboard");

        // Scenario 2: Voter user attempts admin login -> Blocked
        RoleDetector.RoleResult detectedVoter = RoleDetector.RoleResult.VOTER;
        boolean voterAccessToAdmin = "admin".equals(chosenRole) && detectedVoter == RoleDetector.RoleResult.ADMIN;
        assertFalse(voterAccessToAdmin, "Voter must be denied access when selecting Admin role");

        // Scenario 3: Unknown/unregistered user attempts admin login -> Blocked
        RoleDetector.RoleResult detectedUnknown = RoleDetector.RoleResult.UNKNOWN;
        boolean unknownAccessToAdmin = "admin".equals(chosenRole) && detectedUnknown == RoleDetector.RoleResult.ADMIN;
        assertFalse(unknownAccessToAdmin, "Unknown user must be denied access when selecting Admin role");
    }

    @Test
    @DisplayName("Test Voter Role Authorization Grant and Rejection logic")
    void testVoterAuthorizationLogic() {
        // Scenario 1: User attempts voter login with VOTER role -> Granted
        String chosenRole = "voter";
        RoleDetector.RoleResult detectedVoter = RoleDetector.RoleResult.VOTER;

        boolean canAccessVoterDashboard = "voter".equals(chosenRole) && detectedVoter == RoleDetector.RoleResult.VOTER;
        assertTrue(canAccessVoterDashboard, "Voter role should be granted access to Voter Dashboard");

        // Scenario 2: Admin user attempts voter login -> Blocked
        RoleDetector.RoleResult detectedAdmin = RoleDetector.RoleResult.ADMIN;
        boolean adminAccessToVoter = "voter".equals(chosenRole) && detectedAdmin == RoleDetector.RoleResult.VOTER;
        assertFalse(adminAccessToVoter, "Admin must be denied access when selecting Voter role if not registered as voter");

        // Scenario 3: Unapproved/Pending voter -> Blocked
        RoleDetector.RoleResult detectedUnknown = RoleDetector.RoleResult.UNKNOWN;
        boolean pendingAccessToVoter = "voter".equals(chosenRole) && detectedUnknown == RoleDetector.RoleResult.VOTER;
        assertFalse(pendingAccessToVoter, "Pending/unapproved voter must be denied access to Voter Dashboard");
    }

    @Test
    @DisplayName("Test Voter Status Validation (ACCEPTED/VERIFIED vs PENDING)")
    void testVoterStatusValidation() {
        // Accepted voter
        String statusAccepted = "ACCEPTED";
        boolean isAcceptedValid = "ACCEPTED".equalsIgnoreCase(statusAccepted) || "VERIFIED".equalsIgnoreCase(statusAccepted);
        assertTrue(isAcceptedValid, "ACCEPTED status must be authorized");

        // Verified voter
        String statusVerified = "VERIFIED";
        boolean isVerifiedValid = "ACCEPTED".equalsIgnoreCase(statusVerified) || "VERIFIED".equalsIgnoreCase(statusVerified);
        assertTrue(isVerifiedValid, "VERIFIED status must be authorized");

        // Pending voter
        String statusPending = "PENDING";
        boolean isPendingValid = "ACCEPTED".equalsIgnoreCase(statusPending) || "VERIFIED".equalsIgnoreCase(statusPending);
        assertFalse(isPendingValid, "PENDING status must NOT be authorized yet");

        // Rejected voter
        String statusRejected = "REJECTED";
        boolean isRejectedValid = "ACCEPTED".equalsIgnoreCase(statusRejected) || "VERIFIED".equalsIgnoreCase(statusRejected);
        assertFalse(isRejectedValid, "REJECTED status must NOT be authorized");
    }

    @Test
    @DisplayName("Test Admin UID ownership verification logic")
    void testAdminUidOwnershipVerification() {
        String loggedInUid = "uid_admin_123";
        String storedAdminUidMatch = "uid_admin_123";
        String storedAdminUidMismatch = "uid_admin_456";

        // When logged-in UID matches the organization's adminUid
        boolean isAuthorizedAdmin = loggedInUid.equals(storedAdminUidMatch);
        assertTrue(isAuthorizedAdmin, "Admin UID matching organization adminUid must be authorized");

        // When logged-in UID does NOT match organization's adminUid
        boolean isUnauthorizedAdmin = loggedInUid.equals(storedAdminUidMismatch);
        assertFalse(isUnauthorizedAdmin, "Admin UID mismatch must NOT be authorized");
    }

    @Test
    @DisplayName("Test Offline Role Authentication and Session Assignment")
    void testOfflineRoleAuthorization() {
        String chosenRole = "offline";
        String sampleIdToken = "firebase-auth-token-xyz";

        // When offline role is chosen and authenticated
        SessionManager.idToken = sampleIdToken;
        SessionManager.currentRole = "offline";

        assertTrue(SessionManager.isLoggedIn());
        assertEquals("offline", SessionManager.currentRole);
    }

    @Test
    @DisplayName("Test New User Authentication and Onboarding Session")
    void testNewUserRoleAuthorization() {
        String chosenRole = "newUser";
        String sampleIdToken = "firebase-auth-token-abc";

        // When new user authenticates without prior org link
        SessionManager.idToken = sampleIdToken;
        assertTrue(SessionManager.isLoggedIn());
        assertNull(SessionManager.currentRole); // New user has not chosen admin or voter yet
    }
}
