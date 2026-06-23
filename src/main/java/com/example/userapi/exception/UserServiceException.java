package com.example.userapi.exception;

import java.io.Serializable;

/**
 * Domain-level exception thrown by the service layer when an operation fails.
 *
 * <p>This exception is intentionally framework-agnostic so the service layer
 * does not depend on Spring's exception hierarchy. The
 * {@code GlobalExceptionHandler} translates it into an HTTP 500 response.</p>
 */
public class UserServiceException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the given message.
     *
     * @param message a human-readable description of the failure
     */
    public UserServiceException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given message and underlying cause.
     *
     * @param message a human-readable description of the failure
     * @param cause   the underlying cause of the failure
     */
    public UserServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}