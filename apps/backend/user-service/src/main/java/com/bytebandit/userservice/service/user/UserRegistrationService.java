package com.bytebandit.userservice.service.user;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.projection.CreateUserAndTokenProjection;
import com.bytebandit.userservice.repository.UserRepository;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import com.bytebandit.userservice.service.RegistrationEmailService;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class UserRegistrationService implements IUserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final RegistrationEmailService registrationEmailService;
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    /**
     * Registers a new user by creating user and token entries, sending a verification email, and
     * returning a response containing user details.
     *
     * @param registrationRequest the user registration request including email, password, and full
     *                            name.
     *
     * @return a UserRegistrationResponse containing user details such as ID, full name, email,
     *     verification status, and creation timestamp.
     * @throws UserAlreadyExistsException if a user with the provided email already exists.
     * @throws IllegalStateException      if the user registration process fails.
     */
    @Override
    public UserDto register(
        UserRegistrationRequest registrationRequest
    ) {
        String passwordHash = passwordEncoder.encode(registrationRequest.getPassword());
        UUID token = UUID.randomUUID();
        String tokenHash = passwordEncoder.encode(token.toString());
        Timestamp tokenExpiresAt = Timestamp.from(Instant.now().plus(24, ChronoUnit.HOURS));

        try {
            CreateUserAndTokenProjection userAndToken = transactionTemplate.execute(
                result -> userRepository.createUserAndToken(
                    registrationRequest.getEmail(),
                    passwordHash,
                    registrationRequest.getFullName(),
                    tokenHash,
                    TokenType.EMAIL_VERIFICATION.name(),
                    tokenExpiresAt
                )
            );
            if (userAndToken == null) {
                throw new IllegalStateException("User registration failed.");
            }
            sendEmail(
                userAndToken,
                token.toString()
            );
            return UserDto.builder()
                .id(userAndToken.getId())
                .fullName(userAndToken.getFullName())
                .email(userAndToken.getEmail())
                .verified(userAndToken.getVerified())
                .createdAt(userAndToken.getCreatedAt())
                .build();
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: {}", e.getMessage());
            throw new UserAlreadyExistsException("User with provided email already exists.", e);
        } catch (Exception e) {
            logger.error("Unexpected error during user registration: {}", e.getMessage());
            throw new IllegalStateException(
                "User registration process failed due to an unexpected error.", e);
        }
    }

    private void sendEmail(
        CreateUserAndTokenProjection user,
        String token
    ) {
        registrationEmailService.sendEmail(
            user.getEmail(),
            user.getFullName(),
            token,
            user.getId()
        );
    }
}
