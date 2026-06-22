package com.example.userapi.repository;

import com.example.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link User} entities.
 *
 * <p>Provides CRUD operations and pagination support out of the box.
 * Custom query methods can be added here when needed.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}