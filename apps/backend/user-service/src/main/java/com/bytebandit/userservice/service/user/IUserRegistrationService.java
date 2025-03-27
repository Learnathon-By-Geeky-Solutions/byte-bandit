package com.bytebandit.userservice.service.user;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.request.UpdateUserRequest;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import com.bytebandit.userservice.response.UserRegistrationResponse;
import java.util.UUID;

public interface IUserRegistrationService {

    UserDto register(UserRegistrationRequest registrationRequest);

    UserDto convertToDto(UserEntity user);

    UserEntity updateUser(UpdateUserRequest request, UUID userId);

    UserEntity getUserById(UUID userId);

    void deleteUserById(UUID userId);
}
