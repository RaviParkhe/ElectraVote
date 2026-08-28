package com.elctrovotesuperx.exception;

/**
 * Thrown when an election-related operation fails.
 * Examples: election creation, update, deletion, or status change failures.
 */
public class ElectionException extends Exception {

    public ElectionException(String message) {
        super(message);
    }

    public ElectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
