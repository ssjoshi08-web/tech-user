package com.example.userapi.service.impl;

import com.example.userapi.dto.UserDto;
import com.example.userapi.entity.User;
import com.example.userapi.exception.UserServiceException;
import com.example.userapi.repository.UserRepository;
import com.example.userapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link UserService}.
 *
 * <p>Handles conversion between domain entities and DTOs, and converts
 * lower-level repository failures into a domain-specific exception that
 * the {@code GlobalExceptionHandler} can translate into a meaningful
 * HTTP response.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     *
     * @throws UserServiceException if the underlying repository call fails
     */
    @Override
    public List<UserDto> getAllUsers() {
        LOGGER.info("Fetching all users from the repository");
        try {
            final List<User> users = userRepository.findAll();
            LOGGER.debug("Retrieved {} user(s) from the repository", users.size());

            if (users.isEmpty()) {
                LOGGER.info("No users found in the repository");
                return Collections.emptyList();
            }

            return users.stream()
                    .filter(Objects::nonNull)
                    .map(this::toDto)
                    .toList();
        } catch (Exception ex) {
            LOGGER.error("Failed to retrieve users from the repository", ex);
            throw new UserServiceException("Failed to retrieve users", ex);
        }
    }

    /**
     * Maps a {@link User} entity to a {@link UserDto}.
     *
     * @param user the domain entity; must not be {@code null}
     * @return the corresponding DTO
     */
    private UserDto toDto(final User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}