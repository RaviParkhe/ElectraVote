package com.electrovotesuperx.service.OfflineService;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory fake OTP service for offline voter verification.
 *
 * Generates a random 6-digit OTP per (electionId, voterId) pair,
 * stores it with a 60-second expiry, and verifies user input.
 *
 * This is a simulated/offline OTP — no external SMS or email
 * service is involved. Suitable for demonstration and local use.
 */
public class OtpService {

    // =====================================================
    // SINGLETON
    // =====================================================

    private static final OtpService INSTANCE = new OtpService();

    public static OtpService getInstance() {
        return INSTANCE;
    }

    private OtpService() {
    }

    // =====================================================
    // OTP STORAGE
    // =====================================================

    private static final ConcurrentHashMap<String, OtpEntry> otpStore =
            new ConcurrentHashMap<>();

    private static final SecureRandom RANDOM = new SecureRandom();

    /** OTP validity duration in milliseconds (60 seconds) */
    private static final long OTP_VALIDITY_MS = 60_000;

    // =====================================================
    // GENERATE OTP
    // =====================================================

    /**
     * Generates a new 6-digit OTP for the given election + voter.
     * Any previous OTP for the same pair is replaced.
     *
     * @param electionId the election identifier
     * @param voterId    the voter identifier
     * @return the generated 6-digit OTP string
     */
    public String generateOtp(String electionId, String voterId) {

        String key = buildKey(electionId, voterId);

        // Generate 6-digit numeric OTP (100000 – 999999)
        int otpNumber = 100_000 + RANDOM.nextInt(900_000);
        String otp = String.valueOf(otpNumber);

        long now = System.currentTimeMillis();
        long expiresAt = now + OTP_VALIDITY_MS;

        otpStore.put(key, new OtpEntry(otp, now, expiresAt));

        return otp;
    }

    // =====================================================
    // VERIFY OTP
    // =====================================================

    /**
     * Verifies the user-provided OTP against the stored OTP.
     *
     * @param electionId the election identifier
     * @param voterId    the voter identifier
     * @param inputOtp   the OTP entered by the user
     * @return true if the OTP matches and has not expired
     */
    public boolean verifyOtp(
            String electionId,
            String voterId,
            String inputOtp) {

        if (inputOtp == null || inputOtp.isBlank()) {
            return false;
        }

        String key = buildKey(electionId, voterId);

        OtpEntry entry = otpStore.get(key);

        if (entry == null) {
            return false;
        }

        // Check expiry
        if (System.currentTimeMillis() > entry.expiresAt) {
            otpStore.remove(key);
            return false;
        }

        // Compare OTP
        boolean match = entry.otp.equals(inputOtp.trim());

        if (match) {
            // Remove OTP after successful verification (single-use)
            otpStore.remove(key);
        }

        return match;
    }

    // =====================================================
    // CHECK IF OTP IS EXPIRED
    // =====================================================

    /**
     * Checks whether the OTP for the given pair has expired.
     *
     * @param electionId the election identifier
     * @param voterId    the voter identifier
     * @return true if expired or no OTP exists
     */
    public boolean isExpired(String electionId, String voterId) {

        String key = buildKey(electionId, voterId);

        OtpEntry entry = otpStore.get(key);

        if (entry == null) {
            return true;
        }

        return System.currentTimeMillis() > entry.expiresAt;
    }

    // =====================================================
    // GET REMAINING SECONDS
    // =====================================================

    /**
     * Returns the number of seconds remaining before the OTP expires.
     *
     * @param electionId the election identifier
     * @param voterId    the voter identifier
     * @return remaining seconds, or 0 if expired/not found
     */
    public int getRemainingSeconds(String electionId, String voterId) {

        String key = buildKey(electionId, voterId);

        OtpEntry entry = otpStore.get(key);

        if (entry == null) {
            return 0;
        }

        long remaining = entry.expiresAt - System.currentTimeMillis();

        return remaining > 0 ? (int) (remaining / 1000) : 0;
    }

    // =====================================================
    // BUILD KEY
    // =====================================================

    private String buildKey(String electionId, String voterId) {
        return electionId + "|" + voterId;
    }

    // =====================================================
    // OTP ENTRY
    // =====================================================

    private static class OtpEntry {

        final String otp;
        final long createdAt;
        final long expiresAt;

        OtpEntry(String otp, long createdAt, long expiresAt) {
            this.otp = otp;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }
    }
}
