package org.example.progresssoft.exception;

/**
 * Exception thrown when deal validation fails.
 */
public class InvalidDealException extends RuntimeException {
    public InvalidDealException(String message) {
        super(message);
    }
}
