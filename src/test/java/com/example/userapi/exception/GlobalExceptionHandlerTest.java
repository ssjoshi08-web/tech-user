package com.example.userapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 *
 * <p>Exercises both the {@link UserServiceException} path and the
 * catch-all {@link Exception} path, asserting on the structured
 * {@link ErrorResponse} envelope.</p>
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/users");
    }

    @Test
    @DisplayName("handleUserServiceException: should return HTTP 500 with the error envelope")
    void handleUserServiceException() {
        // Arrange
        final UserServiceException ex = new UserServiceException("Failed to retrieve users");

        // Act
        final ResponseEntity<ErrorResponse> response = handler.handleUserServiceException(ex, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to retrieve users");
        assertThat(response.getBody().getPath()).isEqualTo("/api/users");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("handleUserServiceException_withCause: should propagate the cause's message through the cause")
    void handleUserServiceException_withCause() {
        // Arrange
        final RuntimeException cause = new RuntimeException("DB down");
        final UserServiceException ex = new UserServiceException("Service failure", cause);

        // Act
        final ResponseEntity<ErrorResponse> response = handler.handleUserServiceException(ex, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("Service failure");
        assertThat(ex.getCause()).isSameAs(cause);
    }

    @Test
    @DisplayName("handleGenericException: should return HTTP 500 with a generic message")
    void handleGenericException() {
        // Arrange
        final Exception ex = new IllegalStateException("Something exploded");

        // Act
        final ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage())
                .isEqualTo("An unexpected error occurred. Please contact support if the problem persists.");
        assertThat(response.getBody().getPath()).isEqualTo("/api/users");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}