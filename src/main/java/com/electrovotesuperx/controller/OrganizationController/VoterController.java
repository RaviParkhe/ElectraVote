package com.electrovotesuperx.controller.OrganizationController;

import com.electrovotesuperx.dao.OrganizationDAO.VoterDAO;
import com.electrovotesuperx.exception.AuthenticationException;
import com.electrovotesuperx.exception.FirestoreException;

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
        } catch (AuthenticationException | FirestoreException e) {
            return VoterDAO.SignInResult.failure(
                    "Sign in failed. Please check your connection and try again.");
        } catch (Exception e) {
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
            String phone,
            String category,
            String department,
            String yearOrRole,
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
                    phone != null ? phone.trim() : "",
                    category != null && !category.isBlank() ? category.trim() : "Student",
                    department != null && !department.isBlank() ? department.trim() : "General",
                    yearOrRole != null && !yearOrRole.isBlank() ? yearOrRole.trim() : "Member",
                    joinCode.trim().toUpperCase());
        } catch (AuthenticationException | FirestoreException e) {
            return "Registration failed. Please check your connection and try again.";
        } catch (Exception e) {
            return "Registration failed: " + (e.getMessage() != null ? e.getMessage() : "Unknown error");
        }
    }

    public static String signUp(
            String fullName,
            String email,
            String password,
            String joinCode) {
        return signUp(fullName, email, password, "", "Student", "General", "Member", joinCode);
    }
}
