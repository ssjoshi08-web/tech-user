package com.example.userapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * JPA entity representing a user in the system.
 *
 * <p>This entity maps to the {@code users} table in the database and is
 * used as the persistence model. The DTO layer is responsible for shaping
 * the data exposed via the REST API.</p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    /**
     * Two users are considered equal when they share the same id.
     *
     * @param o the object to compare against
     * @return {@code true} if both objects have the same id; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Hash code based on the entity identifier for consistent behavior in hash-based collections.
     *
     * @return the hash code derived from {@link #id}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}