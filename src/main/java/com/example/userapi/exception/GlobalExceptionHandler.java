package com.example.userapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler that centralizes error translation for the REST API.
 *
 * <p>Any uncaught exception thrown by controllers, services, or repositories is
 * intercepted here and converted into a consistent {@link ErrorResponse} payload.
 * The handler also ensures no internal stack traces are leaked to API consumers.</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link UserServiceException} by returning HTTP 500 with a stable
     * error envelope.
     *
     * @param ex      the service-layer exception
     * @param request the current HTTP request
     * @return a 500 response wrapping the failure
     */
    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<ErrorResponse> handleUserServiceException(
            final UserServiceException ex, final HttpServletRequest request) {
        LOGGER.error("Service-level error occurred while processing request to {}",
                request.getRequestURI(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(), request);
    }

    /**
     * Catch-all handler that guarantees a well-formed error response for any
     * unexpected exception. Sensitive details are intentionally omitted.
     *
     * @param ex      the unexpected exception
     * @param request the current HTTP request
     * @return a 500 response with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            final Exception ex, final HttpServletRequest request) {
        LOGGER.error("Unhandled exception occurred while processing request to {}",
                request.getRequestURI(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support if the problem persists.",
                request);
    }

    /**
     * Builds the standardized error response envelope.
     *
     * @param status  the HTTP status to return
     * @param message the user-facing message
     * @param request the originating request
     * @return a fully-populated {@link ResponseEntity}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            final HttpStatus status, final String message, final HttpServletRequest request) {
        final ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}