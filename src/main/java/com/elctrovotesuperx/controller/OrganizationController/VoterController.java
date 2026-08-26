package com.elctrovotesuperx.controller.OrganizationController;

import com.elctrovotesuperx.dao.OrganizationDAO.VoterDAO;

/**
 * VoterController handles validation and business logic
 * for voter sign-in and sign-up. Delegates actual Firebase
 * calls to VoterDAO.
 */
public class VoterController {

    private static final VoterDAO dao = new VoterDAO();

    // =========================================================
    // SIGN IN
    // Returns a SignInResult (success + Voter data, or failure + message)
    // =========================================================

    public static VoterDAO.SignInResult signIn(
            String email,
            String password,
            String joinCode) {

        // Validate inputs
        if (email == null || email.isBlank()) {
            return VoterDAO.SignInResult.failure("Please enter your email address.");
        }

        if (password == null || password.isBlank()) {
            return VoterDAO.SignInResult.failure("Please enter your password.");
        }

        if (joinCode == null || joinCode.isBlank()) {
            return VoterDAO.SignInResult.failure("Please enter your organization join code.");
        }

        try {
            return dao.signIn(email.trim(), password, joinCode.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return VoterDAO.SignInResult.failure(
                    "Sign in failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    // =========================================================
    // SIGN UP
    // Returns null on success, or an error message string on failure
    // =========================================================

    public static String signUp(
            String fullName,
            String email,
            String password,
            String joinCode) {

        // Validate inputs
        if (fullName == null || fullName.isBlank()) {
            return "Please enter your full name.";
        }

        if (email == null || email.isBlank()) {
            return "Please enter your email address.";
        }

        if (password == null || password.isBlank()) {
            return "Please enter a password.";
        }

        if (password.length() < 6) {
            return "Password must be at least 6 characters long.";
        }

        if (joinCode == null || joinCode.isBlank()) {
            return "Please enter your organization join code.";
        }

        try {
            return dao.signUp(
                    fullName.trim(),
                    email.trim(),
                    password,
                    joinCode.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            return "Registration failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }
}
