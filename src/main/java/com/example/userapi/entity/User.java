package com.example.userapi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * Domain entity representing a user in the system.
 *
 * <p>This class is intentionally a plain POJO — the application does not
 * require a database. It is kept in the {@code entity} package to preserve
 * the layered architecture (entity → repository → service → controller).</p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Domain entity representing a user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    @Schema(description = "Full name of the user", example = "Sachin")
    private String name;

    @Schema(description = "Email address of the user", example = "sachin@example.com")
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