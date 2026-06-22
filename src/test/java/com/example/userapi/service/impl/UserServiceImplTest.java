package com.example.userapi.service.impl;

import com.example.userapi.dto.UserDto;
import com.example.userapi.entity.User;
import com.example.userapi.exception.UserServiceException;
import com.example.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * <p>Covers the happy path, the empty-collection path, and the
 * repository-failure path. All dependencies are mocked.</p>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).name("Sachin").email("sachin@example.com").build();
        user2 = User.builder().id(2L).name("John").email("john@example.com").build();
    }

    @Test
    @DisplayName("getAllUsers_success: should map and return a list of UserDto")
    void getAllUsers_success() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        final List<UserDto> result = userService.getAllUsers();

        // Assert
        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getName()).isEqualTo("Sachin");
        assertThat(result.get(0).getEmail()).isEqualTo("sachin@example.com");
        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getName()).isEqualTo("John");
        assertThat(result.get(1).getEmail()).isEqualTo("john@example.com");

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getAllUsers_emptyList: should return an empty list when the repository has no users")
    void getAllUsers_emptyList() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        final List<UserDto> result = userService.getAllUsers();

        // Assert
        assertThat(result).isNotNull().isEmpty();
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("getAllUsers_repositoryException: should wrap repository failures in UserServiceException")
    void getAllUsers_repositoryException() {
        // Arrange
        final DataAccessResourceFailureException cause =
                new DataAccessResourceFailureException("Database unavailable");
        when(userRepository.findAll()).thenThrow(cause);

        // Act + Assert
        assertThatThrownBy(() -> userService.getAllUsers())
                .isInstanceOf(UserServiceException.class)
                .hasMessageContaining("Failed to retrieve users")
                .hasCause(cause);

        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }
}