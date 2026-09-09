package com.electrovotesuperx.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Robust AES encryption and decryption utility for sensitive voter information
 * stored at rest in the SQLite database (full name, email, phone).
 *
 * All encrypted strings are prefixed with "ENC:" to distinguish ciphertext
 * from legacy or unencrypted values.
 */
public final class EncryptionUtil {

    private static final String PREFIX = "ENC:";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_SEED = "ElectroVote-SuperX-Offline-AES256-Key-2026";
    private static final SecretKeySpec SECRET_KEY;
    private static final SecureRandom RANDOM = new SecureRandom();

    static {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(SECRET_SEED.getBytes(StandardCharsets.UTF_8));
            SECRET_KEY = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to initialize AES encryption key: " + e.getMessage());
        }
    }

    private EncryptionUtil() {
    }

    /**
     * Encrypts plaintext string using AES-256 with random IV.
     *
     * @param plainText original string
     * @return ciphertext string prefixed with "ENC:", or empty string if input is blank
     */
    public static String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        if (plainText.isEmpty()) {
            return "";
        }

        try {
            byte[] iv = new byte[16];
            RANDOM.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV + EncryptedBytes
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return PREFIX + Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            System.err.println("[EncryptionUtil] Encryption failed: " + e.getMessage());
            return plainText; // Fallback to avoid data loss
        }
    }

    /**
     * Decrypts ciphertext prefixed with "ENC:".
     *
     * @param cipherText encrypted string
     * @return decrypted plaintext string
     * @throws Exception if decryption fails
     */
    public static String decrypt(String cipherText) throws Exception {
        if (cipherText == null) {
            return null;
        }
        if (cipherText.isEmpty()) {
            return "";
        }

        String payload = cipherText;
        if (payload.startsWith(PREFIX)) {
            payload = payload.substring(PREFIX.length());
        }

        byte[] combined = Base64.getDecoder().decode(payload);
        if (combined.length < 16) {
            throw new IllegalArgumentException("Invalid ciphertext length");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, 16);
        byte[] encryptedBytes = Arrays.copyOfRange(combined, 16, combined.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, ivSpec);

        byte[] plainBytes = cipher.doFinal(encryptedBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * Safely decrypts data if it is encrypted. If the input is not encrypted,
     * null, empty, or fails to decrypt, it returns the input string unchanged.
     * This provides 100% backward compatibility for existing records in SQLite.
     *
     * @param data encrypted or plaintext string
     * @return decrypted plaintext string
     */
    public static String decryptSafe(String data) {
        if (data == null) {
            return null;
        }
        if (!isEncrypted(data)) {
            return data;
        }

        try {
            return decrypt(data);
        } catch (Exception e) {
            // Decryption failure - return original string
            return data;
        }
    }

    /**
     * Checks if a string is encrypted (starts with "ENC:").
     */
    public static boolean isEncrypted(String data) {
        return data != null && data.startsWith(PREFIX);
    }
}
