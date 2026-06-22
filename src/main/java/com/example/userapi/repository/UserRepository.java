package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository abstraction for {@link User} domain objects.
 *
 * <p>This is an in-memory implementation that returns a hardcoded list of
 * users. It is registered as a Spring {@code @Repository} so it can be
 * injected wherever a data-access collaborator is needed. The class is
 * intentionally simple and is the seam where a real persistence mechanism
 * (JPA, JDBC, etc.) can later be substituted without changing the service
 * or controller layers.</p>
 */
@Repository
public class UserRepository {

    /**
     * Returns all known users.
     *
     * @return a non-null list of {@link User} entities
     */
    public List<User> findAll() {
        return List.of(
                User.builder().id(1L).name("Sachin").email("sachin@example.com").build(),
                User.builder().id(2L).name("John").email("john@example.com").build()
        );
    }
}