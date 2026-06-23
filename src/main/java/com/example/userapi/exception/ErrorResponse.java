package com.example.userapi.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

/**
 * Standardized error response envelope returned by the API.
 *
 * <p>This wrapper ensures all error responses share a consistent shape, which
 * simplifies client-side handling.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard error response envelope")
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Epoch millisecond timestamp of when the error occurred")
    private Instant timestamp;

    @Schema(description = "HTTP status code", example = "500")
    private int status;

    @Schema(description = "Short error name", example = "Internal Server Error")
    private String error;

    @Schema(description = "Detailed message describing the failure")
    private String message;

    @Schema(description = "Request path that triggered the error")
    private String path;
}