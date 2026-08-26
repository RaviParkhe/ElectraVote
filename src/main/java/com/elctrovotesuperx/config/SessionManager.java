package com.elctrovotesuperx.config;

/**
 * SessionManager holds the currently logged-in admin's session data.
 * Set once after a successful admin sign-in (SignInOrganization).
 * Read by ElectionController, CandidateController, VoteController, etc.
 */
public class SessionManager {

    // Admin Authentication token (Firebase idToken)
    public static String idToken;

    // Organization join code (e.g. "EV-XXXX-XXXX")
    public static String joinCode;

    // Organization display name
    public static String organizationName;

    // Admin Firebase UID
    public static String adminUid;

    // Admin email
    public static String adminEmail;

    /** Clear session on logout */
    public static void clear() {
        idToken = null;
        joinCode = null;
        organizationName = null;
        adminUid = null;
        adminEmail = null;
    }

    /** Returns true if an admin session is active */
    public static boolean isLoggedIn() {
        return idToken != null && !idToken.isBlank();
    }
}
