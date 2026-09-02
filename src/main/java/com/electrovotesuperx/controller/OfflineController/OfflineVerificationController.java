package com.electrovotesuperx.controller.OfflineController;

import com.electrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.model.OfflineModel.Voter;
import com.electrovotesuperx.service.OfflineService.TwilioOtpService;
import com.electrovotesuperx.service.OfflineService.VoterVerificationService;

public class OfflineVerificationController {

    private final VoterVerificationService service = new VoterVerificationService();
    private final TwilioOtpService twilioService = TwilioOtpService.getInstance();

    // =========================================================
    // PRE-VERIFICATION ELIGIBILITY CHECK
    // =========================================================

    public VoterVerificationService.EligibilityResult validateEligibility(
            String voterId,
            String electionId) throws DatabaseException {
        return service.validateEligibility(voterId, electionId);
    }

    // =========================================================
    // ISSUE TOKEN AFTER SUCCESSFUL OTP AUTHENTICATION
    // =========================================================

    public VoterVerificationService.VerificationResult issueTokenAfterVerification(
            Voter voter,
            String electionId,
            String authMethod) throws DatabaseException {
        return service.issueTokenAfterVerification(voter, electionId, authMethod);
    }

    // =========================================================
    // TWILIO SMS OTP SERVICE
    // =========================================================

    public void sendTwilioSmsOtp(
            String electionId,
            String voterId,
            String voterName,
            String phone,
            TwilioOtpService.TwilioOtpCallback callback) {
        twilioService.sendOtp(electionId, voterId, voterName, phone, callback);
    }

    public boolean verifyTwilioOtp(
            String electionId,
            String voterId,
            String inputOtp) {
        return twilioService.verifyOtp(electionId, voterId, inputOtp);
    }

    public boolean isOtpExpired(String electionId, String voterId) {
        return twilioService.isExpired(electionId, voterId);
    }

    public int getOtpRemainingSeconds(String electionId, String voterId) {
        return twilioService.getRemainingSeconds(electionId, voterId);
    }

    // =========================================================
    // DIRECT VERIFICATION (FALLBACK)
    // =========================================================

    public VoterVerificationService.VerificationResult verify(
            String voterId,
            String electionId) throws DatabaseException {
        return service.verify(voterId, electionId);
    }
}
