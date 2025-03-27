package com.bytebandit.userservice.service.user;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.request.UpdateUserRequest;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import java.util.UUID;

public interface IUserRegistrationService {


    /**
     *      * Registers a new user in the system.
     *      *
     *      * @param registrationRequest Contains user registration details
     *      * @return UserDto containing the registered user information
     *      * @throws ValidationException If registration data is invalid
     * 
     */
    UserDto register(UserRegistrationRequest registrationRequest);


    /**
     *      * Converts a user entity to a data transfer object.
     *      *
     *      * @param user The user entity to convert
     *      * @return UserDto representation of the user entity
     * 
     */
    UserDto convertToDto(UserEntity user);


    /**
     *      * Updates an existing user's information.
     *      *
     *      * @param request Update request containing new user data
     *      * @param userId Identifier of the user to update
     *      * @return Updated user entity
     *      * @throws EntityNotFoundException If user with given ID not found
     * 
     */
    UserEntity updateUser(UpdateUserRequest request, UUID userId);


    /**
     *      * Retrieves a user by their ID.
     *      *
     *      * @param userId Identifier of the user to retrieve
     *      * @return User entity if found
     *      * @throws EntityNotFoundException If user with given ID not found
     * 
     */
    UserEntity getUserById(UUID userId);


    /**
     *      * Deletes a user by their ID.
     *      *
     *      * @param userId Identifier of the user to delete
     *      * @throws EntityNotFoundException If user with given ID not found
     * 
     */
    void deleteUserById(UUID userId);

}
