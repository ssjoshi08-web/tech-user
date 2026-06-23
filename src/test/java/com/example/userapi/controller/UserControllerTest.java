package com.example.userapi.controller;

import com.example.userapi.dto.UserDto;
import com.example.userapi.exception.GlobalExceptionHandler;
import com.example.userapi.exception.UserServiceException;
import com.example.userapi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link UserController}.
 *
 * <p>Verifies HTTP routing, status code, response body, and content type.
 * Mocks the {@link UserService} dependency and wires in the
 * {@link GlobalExceptionHandler} so error paths are covered too.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String USERS_ENDPOINT = "/api/users";

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserDto user1;
    private UserDto user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        user1 = UserDto.builder().id(1L).name("Sachin").email("sachin@example.com").build();
        user2 = UserDto.builder().id(2L).name("John").email("john@example.com").build();
    }

    @Test
    @DisplayName("verifyGetAllUsers: should call the service exactly once when the endpoint is hit")
    void verifyGetAllUsers() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        mockMvc.perform(get(USERS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Assert
        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("verifyStatusCode200: should return HTTP 200 for the GET /api/users endpoint")
    void verifyStatusCode200() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act + Assert
        mockMvc.perform(get(USERS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("verifyResponseBody: should return a JSON array with the correct user fields")
    void verifyResponseBody() throws Exception {
        // Arrange
        final List<UserDto> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act + Assert
        mockMvc.perform(get(USERS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Sachin", "John")))
                .andExpect(jsonPath("$[*].email",
                        containsInAnyOrder("sachin@example.com", "john@example.com")));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("verifyResponseBody_empty: should return an empty JSON array when there are no users")
    void verifyResponseBody_empty() throws Exception {
        // Arrange
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // Act + Assert
        mockMvc.perform(get(USERS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }

    @Test
    @DisplayName("verifyErrorResponse: should return HTTP 500 with the error envelope on service failure")
    void verifyErrorResponse() throws Exception {
        // Arrange
        when(userService.getAllUsers())
                .thenThrow(new UserServiceException("Failed to retrieve users"));

        // Act + Assert
        mockMvc.perform(get(USERS_ENDPOINT)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Failed to retrieve users"))
                .andExpect(jsonPath("$.path").value(USERS_ENDPOINT));

        verify(userService, times(1)).getAllUsers();
        verifyNoMoreInteractions(userService);
    }
}