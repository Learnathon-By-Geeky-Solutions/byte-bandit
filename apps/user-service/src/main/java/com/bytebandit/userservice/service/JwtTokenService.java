package com.bytebandit.userservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class responsible for generating, validating, and extracting information from JWT tokens.
 * Provides utility methods to interact with JWT tokens, including creation, validation, and claim extraction.
 *
 * Purpose:
 * - Handles JWT token creation, validation, and extraction of claims.
 * - Provides methods to generate tokens for user authentication and validate the integrity of received tokens.
 * - Extracts essential information from the token, such as username and expiration date.
 *
 */

@Service
public class JwtTokenService extends TokenService {

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    @Override
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }
}