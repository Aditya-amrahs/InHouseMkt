package com.inhouse.marketplace.exception;

/** Thrown when an authenticated employee does not own the target resource. */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
