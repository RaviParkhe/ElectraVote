package com.electrovotesuperx.service.OfflineService;

import com.electrovotesuperx.dao.OfflineDAO.AuditLogDAO;
import com.electrovotesuperx.dao.OfflineDAO.OtpAttemptDAO;

import java.sql.SQLException;

/**
 * Risk Analysis Service — ElectraVote Security Pipeline
 *
 * Sits between Eligibility and Authentication steps.
 * Evaluates three risk dimensions and returns a RiskVerdict:
 *
 *   1. LOCKED  — Voter has 5+ consecutive failed OTPs in the last 30 min.
 *                Action: block OTP send, show red lock banner.
 *
 *   2. FLOODED — Voter received 10+ OTP requests in the last 2 minutes.
 *                Action: warn officer with amber banner, log RISK_OTP_FLOOD.
 *
 *   3. VELOCITY — Officer account generated 200+ verifications in 60 minutes.
 *                 Action: warn officer with amber banner, log RISK_OFFICER_VELOCITY.
 *
 * CLEAR — None of the above. Proceed normally.
 */
public class RiskAnalysisService {

    // =========================================================
    // THRESHOLDS
    // =========================================================

    /** Consecutive OTP failures before voter is locked */
    private static final int LOCK_FAILURE_THRESHOLD = 5;

    /** OTP requests in window before flood is flagged */
    private static final int FLOOD_REQUEST_THRESHOLD = 10;

    /** Minutes for flood detection window */
    private static final int FLOOD_WINDOW_MINUTES = 2;

    /** Officer verifications in window before velocity is flagged */
    private static final int VELOCITY_THRESHOLD = 200;

    /** Minutes for officer velocity detection window */
    private static final int VELOCITY_WINDOW_MINUTES = 60;

    // =========================================================
    // RISK VERDICT LEVELS
    // =========================================================

    public enum RiskLevel {
        CLEAR,    // No issues
        FLAGGED,  // Warning — proceed but officer is warned
        LOCKED    // Blocked — do not send OTP
    }

    // =========================================================
    // RISK VERDICT RECORD
    // =========================================================

    public record RiskVerdict(
            RiskLevel level,
            String reason,
            boolean isLocked,
            boolean isFlagged) {

        public static RiskVerdict clear() {
            return new RiskVerdict(RiskLevel.CLEAR, null, false, false);
        }

        public static RiskVerdict locked(String reason) {
            return new RiskVerdict(RiskLevel.LOCKED, reason, true, false);
        }

        public static RiskVerdict flagged(String reason) {
            return new RiskVerdict(RiskLevel.FLAGGED, reason, false, true);
        }
    }

    // =========================================================
    // DAO
    // =========================================================

    private final OtpAttemptDAO otpAttemptDAO = new OtpAttemptDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    // =========================================================
    // ASSESS — main entry point
    //
    // Called before sending OTP (pre-authentication check).
    // Returns the most severe verdict found:
    //   LOCKED > FLAGGED > CLEAR
    // =========================================================

    public RiskVerdict assess(
            String voterId,
            String electionId,
            String officerEmail) {

        try {

            // =================================================
            // CHECK 1: Is voter temporarily locked?
            // =================================================

            if (otpAttemptDAO.isLocked(voterId, electionId)) {
                int failures = otpAttemptDAO.countRecentFailures(voterId, electionId);
                String reason = "Voter is locked after " + failures + " consecutive incorrect OTP entries. "
                        + "Lock auto-expires 30 minutes after the last failed attempt.";

                auditLogDAO.log(
                        "RISK_VOTER_LOCKED",
                        voterId,
                        electionId,
                        null,
                        reason,
                        officerEmail);

                return RiskVerdict.locked(reason);
            }

            // =================================================
            // CHECK 2: OTP Flood — too many requests in window
            // =================================================

            int recentRequests = otpAttemptDAO.countRecentRequests(
                    voterId, electionId, FLOOD_WINDOW_MINUTES);

            if (recentRequests >= FLOOD_REQUEST_THRESHOLD) {
                String reason = recentRequests + " OTP requests detected in the last "
                        + FLOOD_WINDOW_MINUTES + " minutes for this voter. "
                        + "Possible flooding or automated retry. Proceed with caution.";

                auditLogDAO.log(
                        "RISK_OTP_FLOOD",
                        voterId,
                        electionId,
                        null,
                        reason,
                        officerEmail);

                return RiskVerdict.flagged(reason);
            }

            // =================================================
            // CHECK 3: Officer velocity — unusually high rate
            // =================================================

            int officerVerifications = otpAttemptDAO.countOfficerVerifications(
                    officerEmail, VELOCITY_WINDOW_MINUTES);

            if (officerVerifications >= VELOCITY_THRESHOLD) {
                String reason = "Officer has processed " + officerVerifications + " verifications "
                        + "in the last " + VELOCITY_WINDOW_MINUTES + " minutes. "
                        + "This rate exceeds normal polling station capacity. Flag for supervisor review.";

                auditLogDAO.log(
                        "RISK_OFFICER_VELOCITY",
                        voterId,
                        electionId,
                        null,
                        reason,
                        officerEmail);

                return RiskVerdict.flagged(reason);
            }

            // =================================================
            // ALL CHECKS PASSED
            // =================================================

            return RiskVerdict.clear();

        } catch (SQLException e) {
            // Non-blocking — risk analysis failure should not prevent voting.
            // Log and return CLEAR (fail-open for availability).
            System.err.println("[RiskAnalysisService] Risk check failed (DB error): " + e.getMessage());
            return RiskVerdict.clear();
        }
    }

    // =========================================================
    // LOG OTP SEND (call when OTP is dispatched)
    // =========================================================

    public void logOtpSend(String voterId, String electionId, String officerEmail) {
        try {
            otpAttemptDAO.logAttempt(voterId, electionId, officerEmail, false);
        } catch (SQLException e) {
            System.err.println("[RiskAnalysisService] Could not log OTP send: " + e.getMessage());
        }
    }

    // =========================================================
    // LOG OTP FAILURE (call when OTP code is wrong)
    // =========================================================

    public void logOtpFailure(String voterId, String electionId, String officerEmail) {
        try {
            otpAttemptDAO.logAttempt(voterId, electionId, officerEmail, false);
        } catch (SQLException e) {
            System.err.println("[RiskAnalysisService] Could not log OTP failure: " + e.getMessage());
        }
    }

    // =========================================================
    // LOG OTP SUCCESS (call when OTP is verified correctly)
    // =========================================================

    public void logOtpSuccess(String voterId, String electionId, String officerEmail) {
        try {
            otpAttemptDAO.logAttempt(voterId, electionId, officerEmail, true);
        } catch (SQLException e) {
            System.err.println("[RiskAnalysisService] Could not log OTP success: " + e.getMessage());
        }
    }
}
