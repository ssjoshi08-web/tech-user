package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link UserRepository}.
 *
 * <p>Exercises the in-memory implementation to lock in the contract for
 * the sample response. A JPA-backed implementation can later be swapped
 * in without changing these expectations.</p>
 */
class UserRepositoryTest {

    @Test
    @DisplayName("findAll: should return the seeded users (Sachin and John)")
    void findAll_returnsSeededUsers() {
        // Arrange
        final UserRepository repository = new UserRepository();

        // Act
        final List<User> users = repository.findAll();

        // Assert
        assertThat(users).isNotNull().hasSize(2);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(0).getName()).isEqualTo("Sachin");
        assertThat(users.get(0).getEmail()).isEqualTo("sachin@example.com");
        assertThat(users.get(1).getId()).isEqualTo(2L);
        assertThat(users.get(1).getName()).isEqualTo("John");
        assertThat(users.get(1).getEmail()).isEqualTo("john@example.com");
    }
}