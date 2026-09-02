package com.electrovotesuperx.exception;

/**
 * Thrown when Firebase authentication operations fail.
 * Examples: sign-in, sign-up, token refresh failures.
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
