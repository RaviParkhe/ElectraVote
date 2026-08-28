package com.electrovotesuperx.controller.OfflineController;

import com.elctrovotesuperx.exception.DatabaseException;
import com.electrovotesuperx.service.OfflineService.OtpService;
import com.electrovotesuperx.service.OfflineService.VoterVerificationService;

public class OfflineVerificationController {

    private final VoterVerificationService service = new VoterVerificationService();
    private final OtpService otpService = OtpService.getInstance();

    public VoterVerificationService.VerificationResult verify(
            String voterId,
            String electionId) throws DatabaseException {
        return service.verify(voterId, electionId);
    }

    // =========================================================
    // OTP GENERATION
    // =========================================================

    public String generateOtp(String electionId, String voterId) {
        return otpService.generateOtp(electionId, voterId);
    }

    // =========================================================
    // OTP VERIFICATION
    // =========================================================

    public boolean verifyOtp(
            String electionId,
            String voterId,
            String inputOtp) {
        return otpService.verifyOtp(electionId, voterId, inputOtp);
    }

    // =========================================================
    // OTP EXPIRY CHECK
    // =========================================================

    public boolean isOtpExpired(String electionId, String voterId) {
        return otpService.isExpired(electionId, voterId);
    }

    // =========================================================
    // OTP REMAINING SECONDS
    // =========================================================

    public int getOtpRemainingSeconds(String electionId, String voterId) {
        return otpService.getRemainingSeconds(electionId, voterId);
    }
}
