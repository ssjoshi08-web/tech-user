package com.example.userapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Data Transfer Object representing a user as returned by the REST API.
 *
 * <p>This DTO is intentionally decoupled from the persistence entity to avoid
 * leaking internal database structure into the public API contract.</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Represents a user exposed by the API")
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Full name of the user", example = "Sachin")
    private String name;

    @Schema(description = "Email address of the user", example = "sachin@example.com")
    private String email;
}