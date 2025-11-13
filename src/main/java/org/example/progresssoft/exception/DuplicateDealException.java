package org.example.progresssoft.exception;

/**
 * Exception thrown when attempting to import a duplicate FX deal.
 */
public class DuplicateDealException extends RuntimeException {
    public DuplicateDealException(String message) {
        super(message);
    }
}
