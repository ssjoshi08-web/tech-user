package com.example.userapi.controller;

import com.example.userapi.dto.UserDto;
import com.example.userapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing user-related endpoints.
 *
 * <p>All endpoints are documented via OpenAPI annotations so the
 * generated Swagger UI is self-explanatory.</p>
 */
@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "Operations for managing users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * Retrieves all users available in the system.
     *
     * @return a 200 OK response with a JSON array of {@link UserDto}
     */
    @Operation(
            summary = "Get all users",
            description = "Returns the complete list of registered users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = com.example.userapi.exception.ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        LOGGER.info("GET /api/users invoked");
        final List<UserDto> users = userService.getAllUsers();
        LOGGER.info("Returning {} user(s)", users.size());
        return ResponseEntity.ok(users);
    }
}