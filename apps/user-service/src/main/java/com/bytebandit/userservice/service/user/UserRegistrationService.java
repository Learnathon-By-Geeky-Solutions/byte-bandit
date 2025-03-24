package com.bytebandit.userservice.service.user;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.mapper.UserMapper;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.UserRepository;
import com.bytebandit.userservice.request.UpdateUserRequest;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationService implements IUserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final UserMapper userMapper;
    private static final String USER_NOT_FOUND_PREFIX = "User with Id : ";
    private static final String USER_NOT_FOUND_SUFFIX = " not found!";


    @Override
    public UserEntity register(
            UserRegistrationRequest registrationRequest
    ) {
        UserEntity createdUser = UserEntity.builder()
                .email(registrationRequest.getEmail())
                .passwordHash(
                        passwordEncoder.encode(registrationRequest.getPassword())
                )
                .fullName(registrationRequest.getFullName())
                .verified(false)
                .build();
        try {
            return transactionTemplate.execute(
                    result -> userRepository.save(createdUser)
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with provided email: "+registrationRequest.getEmail()+" already exists.", e);
        }
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
