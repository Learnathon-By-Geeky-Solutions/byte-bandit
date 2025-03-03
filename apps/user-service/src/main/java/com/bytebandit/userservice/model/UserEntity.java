package com.bytebandit.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)

/*
    * UserDetails is an interface that Spring Security uses to represent user details.
    * It contains information about the user such as username, password, authorities, etc.
    * Principal is an interface that represents the identity of a user.
    * It is used to represent the user in the context of authentication.
 */

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
    private Boolean isEnabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    /*
        * will be needed when (if) we want to implement email verification
    */

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return getEmail();
    }
}
