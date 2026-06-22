package com.example.userapi.service;

import com.example.userapi.dto.UserDto;

import java.util.List;

/**
 * Service contract for user-related business operations.
 *
 * <p>Concrete implementations live in the {@code impl} package. This interface
 * keeps controllers decoupled from specific service implementations and
 * supports easier testing and future substitution.</p>
 */
public interface UserService {

    /**
     * Retrieves all users in the system.
     *
     * @return an immutable list of {@link UserDto}; never {@code null}
     * @throws com.example.userapi.exception.UserServiceException if retrieval fails
     */
    List<UserDto> getAllUsers();
}