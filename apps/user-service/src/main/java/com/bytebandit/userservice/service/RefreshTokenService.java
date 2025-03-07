package com.bytebandit.userservice.service;

import com.bytebandit.userservice.exception.UserNotFoundException;
import com.bytebandit.userservice.model.RefreshTokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.RefreshTokenRepository;
import com.bytebandit.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RefreshTokenService extends TokenService{

    @Value("${application.security.refresh.expiration}")
    private long refreshTokenExpiration;
    private final RefreshTokenRepository refreshTokenRepository;

    private final UserRepository userRepository;

    // todo: inefficient methods
    public RefreshTokenEntity generateAndSaveRefreshToken(UserDetails user) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(user.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(userEntity)
                .refreshToken(generateToken(user))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, refreshTokenExpiration);
    }
}
