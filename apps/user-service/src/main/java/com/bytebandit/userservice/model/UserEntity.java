package com.bytebandit.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Represents a user entity, implementing {@link UserDetails} and {@link Principal}.
 * It is used to manage user-related data and is integrated with Spring Security for authentication.
 *
 * Purpose:
 * - Maps to the `users` table in the database and stores user details.
 * - Implements {@link UserDetails} to provide user-specific data required for authentication and authorization.
 * - Implements {@link Principal} to represent the current user in the security context.
 *
 * The class uses {@link AuditingEntityListener} to automatically capture auditing information like creation and modification timestamps.
 *
 * Related to:
 * - Implement {@link org.springframework.security.core.userdetails.UserDetailsService} to load user details from a database or external source.
 *
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@Builder
@EntityListeners(AuditingEntityListener.class)
public class UserEntity implements UserDetails, Principal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true)
    @Email(message = "Invalid email format detected")
    @NotNull(message = "Email field cannot be null")
    private String email;

    @Column(name = "password", length = 72)
    @NotNull(message = "Password field cannot be null")
    private String password;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @PrePersist
    protected void onCreate() {
        this.isEnabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    /**
     * Returns {@code true}, indicating the user is enabled;
     * useful when implementing email verification logic.
     */
    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getName() {
        return getEmail();
    }
}
