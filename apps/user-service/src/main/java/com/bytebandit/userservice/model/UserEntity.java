package com.bytebandit.userservice.model;

import com.bytebandit.userservice.annotation.ValidEmail;
import com.bytebandit.userservice.annotation.ValidPassword;
import com.bytebandit.userservice.annotation.ValidUsername;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
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
public class UserEntity implements UserDetails, Principal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", unique = true)
    @ValidEmail
    private String email;

    @Column(name = "password", length = 72)
    @ValidPassword
    private String password;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "username")
    @ValidUsername
    private String username;

    @Column(name = "is_enabled", nullable = false)
    @ColumnDefault(value = "false")
    private boolean isEnabled;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
