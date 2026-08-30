package com.electrovotesuperx.config;

/**
 * SessionManager holds the currently logged-in admin's and voter's session
 * data.
 * Read by ElectionController, CandidateController, VoteController,
 * VoterDashboard, etc.
 */
public class SessionManager {

    // Authentication token (Firebase idToken)
    public static String idToken;

    // Detected role after login: "admin", "voter", or null
    public static String currentRole;

    // Organization join code (e.g. "EV-XXXX-XXXX")
    public static String joinCode;

    // Organization display name
    public static String organizationName;

    // Admin Firebase UID, Email & Name
    public static String adminUid;
    public static String adminEmail;
    public static String adminName;

    // Authenticated User Email (stored from Main Login / Sign In)
    public static String loggedInEmail;

    // Voter Session Data
    public static String voterUid;
    public static String voterEmail;
    public static String voterName;
    public static String voterStatus;
    public static String voterPhone;

    /** Clear session on logout */
    public static void clear() {
        idToken = null;
        currentRole = null;
        joinCode = null;
        organizationName = null;
        adminUid = null;
        adminEmail = null;
        adminName = null;
        loggedInEmail = null;
        voterUid = null;
        voterEmail = null;
        voterName = null;
        voterStatus = null;
        voterPhone = null;
    }

    public static void clearSession() {
        clear();
    }

    /** Returns true if an active authenticated session exists */
    public static boolean isLoggedIn() {
        return idToken != null && !idToken.isBlank();
    }
}