package com.bytebandit.userservice.service.user;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.request.UserRegistrationRequest;

public interface IUserRegistrationService {


    /**
     * * Registers a new user in the system. * * @param registrationRequest Contains user
     * registration details * @return UserDto containing the registered user information * @throws
     * ValidationException If registration data is invalid
     */
    UserDto register(UserRegistrationRequest registrationRequest);


}
