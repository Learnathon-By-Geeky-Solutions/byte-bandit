package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface  RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> { }
