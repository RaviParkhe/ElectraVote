package com.electrovotesuperx.exception;

/**
 * Thrown when a local database (SQLite) operation fails.
 * Examples: query failure, connection error, transaction rollback.
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
