package com.elctrovotesuperx.exception;

/**
 * Thrown when a Firestore REST API operation fails.
 * Examples: document read/write failure, query error, connection timeout.
 */
public class FirestoreException extends Exception {

    public FirestoreException(String message) {
        super(message);
    }

    public FirestoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
