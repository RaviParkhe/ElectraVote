package com.electrovotesuperx.exception;

/**
 * Thrown when a voting operation fails.
 * Examples: vote casting failure, double-vote detection, invalid ballot.
 */
public class VotingException extends Exception {

    public VotingException(String message) {
        super(message);
    }

    public VotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
