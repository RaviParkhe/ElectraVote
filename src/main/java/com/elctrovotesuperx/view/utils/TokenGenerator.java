package com.electrovotesuperx.utils;

import java.security.SecureRandom;

public final class TokenGenerator {
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private TokenGenerator() {}

    public static String generateToken() {
        StringBuilder token = new StringBuilder("EV-");
        for (int i = 0; i < 8; i++) {
            token.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return token.toString();
    }
}
