package com.electrovotesuperx.exception;

/**
 * Thrown when input validation fails.
 * Examples: empty required fields, invalid formats, constraint violations.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
