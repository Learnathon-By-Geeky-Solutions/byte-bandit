package com.bytebandit.userservice.service;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.mapper.UserMapper;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.request.UpdateUserRequest;
import com.bytebandit.userservice.response.UserRegistrationResponse;
import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.projection.CreateUserAndTokenProjection;
import com.bytebandit.userservice.repository.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final RegistrationEmailService registrationEmailService;
    private final UserMapper userMapper;
    private static final String USER_NOT_FOUND_PREFIX = "User with Id : ";
    private static final String USER_NOT_FOUND_SUFFIX = " not found!";


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
    public UserRegistrationResponse register(
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
            return new UserRegistrationResponse(
                userAndToken.getId(),
                userAndToken.getFullName(),
                userAndToken.getEmail(),
                userAndToken.getVerified(),
                userAndToken.getCreatedAt()
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with provided email already exists.", e);
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


    @Override
    public UserEntity updateUser(UpdateUserRequest request, UUID userId) {
        return userRepository.findById(userId).map(existingUser -> {
            if (request.getFullName() != null) {
                existingUser.setFullName(request.getFullName());
            }
            if (request.getPassword() != null) {
                existingUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }
            return userRepository.save(existingUser);
        }).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND_PREFIX + userId + USER_NOT_FOUND_SUFFIX)
        );
    }

    @Override
    public UserEntity getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(USER_NOT_FOUND_PREFIX + userId +USER_NOT_FOUND_SUFFIX)
        );
    }

    @Override
    public void deleteUserById(UUID userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete,
                () -> {
                    throw new EntityNotFoundException(USER_NOT_FOUND_PREFIX + userId +USER_NOT_FOUND_SUFFIX);
                }
        );

    }

    @Override
    public UserDto convertToDto(UserEntity user) {
        return userMapper.toUserRegistrationResponse(user);
    }
}
