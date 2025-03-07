package com.bytebandit.userservice.service;

import com.bytebandit.userservice.dto.request.LoginRequestDto;
import com.bytebandit.userservice.dto.response.RegisterResponseDto;
import com.bytebandit.userservice.dto.request.RegisterRequestDto;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.exception.UserNotFoundException;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.UserRepository;
import com.bytebandit.userservice.util.CookieTokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.refresh.expiration}")
    private long refreshTokenExpiration;


    private UserEntity createUser(RegisterRequestDto request) {
        UserEntity newUser = UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(newUser);
        newUser.setPassword("");
        return newUser;
    }

    private boolean userAlreadyExists(RegisterRequestDto request) {
        return userRepository.existsByEmail(request.getEmail());
    }

    public RegisterResponseDto registerUser(RegisterRequestDto request) {
        if (userAlreadyExists(request)) {
            throw new UserAlreadyExistsException("User already exists");
        }
        UserEntity createdUser =  createUser(request);
        return RegisterResponseDto.builder()
                .email(createdUser.getEmail())
                .createdAt(createdUser.getCreatedAt())
                .build();

    }

    private boolean userExists(LoginRequestDto loginRequestDto) {
        return userRepository.existsByEmail(loginRequestDto.getEmail());
    }

    public void loginUser(
            LoginRequestDto loginRequestDto,
            HttpServletResponse servletResponse
    ) {
        UserDetails currentUser = User.builder()
                .username(loginRequestDto.getEmail())
                .password(loginRequestDto.getPassword())
                .build();
        if (!userExists(loginRequestDto)) {
            throw new UserNotFoundException("User not found");
        }
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            currentUser.getUsername(),
                            currentUser.getPassword()
                    )
            );
            log.info("User authentication begin");
            if (authentication.isAuthenticated()) {
                log.info("User authenticated");
                String jwtToken = jwtTokenService.generateToken(currentUser);
                String refreshToken = refreshTokenService.generateAndSaveRefreshToken(currentUser).getRefreshToken();
                CookieTokenUtil.saveTokenInCookie(jwtToken, "jwt", jwtExpiration, servletResponse);
                CookieTokenUtil.saveTokenInCookie(refreshToken, "refresh", refreshTokenExpiration, servletResponse);
                log.info("Jwt Token: {}", jwtToken);
                log.info("Refresh Token: {}", refreshToken);
            }
            log.info("User authentication end");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Wrong email or password");
        } catch (Exception e) {
            log.error("Error occurred during authentication", e);
        }
    }
}
