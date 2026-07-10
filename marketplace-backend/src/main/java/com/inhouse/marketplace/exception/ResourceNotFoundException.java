package com.inhouse.marketplace.exception;

/**
 * Thrown when a requested entity is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id);
    }
}
