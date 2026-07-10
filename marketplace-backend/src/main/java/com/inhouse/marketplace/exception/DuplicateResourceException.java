package com.inhouse.marketplace.exception;

/**
 * Thrown when a duplicate entity (e.g., duplicate userId on registration) is encountered.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
