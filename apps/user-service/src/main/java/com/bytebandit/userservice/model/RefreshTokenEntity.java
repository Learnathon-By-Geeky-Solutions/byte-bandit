package com.bytebandit.userservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * Represents a refresh token entity that is used for managing JWT refresh tokens.
 *
 * Approach:
 * - Contains a `refreshToken` string to store the actual refresh token.
 * - Defines a one-to-one relationship with {@link UserEntity} to link each refresh token to a specific user.
 *
 */

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens")
@EntityListeners(AuditingEntityListener.class)
public class RefreshTokenEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "refresh_token", updatable = false)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id",nullable = false)
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "validated_at", nullable = false)
    private Timestamp lastValidatedAt;
}
