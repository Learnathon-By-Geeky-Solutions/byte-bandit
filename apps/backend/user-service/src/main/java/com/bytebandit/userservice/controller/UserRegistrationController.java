package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.request.UpdateUserRequest;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import com.bytebandit.userservice.response.ApiResponse;
import com.bytebandit.userservice.service.user.IUserRegistrationService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user registration and management.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserRegistrationController {

    private final IUserRegistrationService userRegistrationService;

    /**
     * Registers a new user.
     *
     * @param request the user registration request
     *
     * @return the response entity containing the API response
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
        @Valid @RequestBody UserRegistrationRequest request) {
        UserDto user = userRegistrationService.register(request);
        return buildResponse(
            HttpStatus.OK,
            "User registered successfully",
            user,
            "/users/register"
        );
    }

    /**
     * Updates an existing user.
     *
     * @param userId  the user ID
     * @param request the update user request
     *
     * @return the response entity containing the API response
     */
    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(
        @PathVariable UUID userId,
        @Valid @RequestBody UpdateUserRequest request) {
        UserEntity user = userRegistrationService.updateUser(request, userId);
        UserDto userDto = userRegistrationService.convertToDto(user);
        return buildResponse(
            HttpStatus.OK,
            "User updated successfully",
            userDto,
            "/users/update"
        );
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId the user ID
     *
     * @return the response entity containing the API response
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable UUID userId) {
        UserEntity user = userRegistrationService.getUserById(userId);
        UserDto userDto = userRegistrationService.convertToDto(user);
        return buildResponse(
            HttpStatus.OK,
            "User retrieved successfully",
            userDto,
            "/users/user/" + userId);
    }

    /**
     * Deletes a user by ID.
     *
     * @param userId the user ID
     *
     * @return the response entity containing the API response
     */
    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUserById(@PathVariable UUID userId) {
        userRegistrationService.deleteUserById(userId);
        return buildResponse(
            HttpStatus.OK,
            "User deleted successfully",
            null,
            "/users/delete/" + userId);
    }


    /**
     * Builds an API response.
     *
     * @param status  the HTTP status
     * @param message the message
     * @param data    the data
     * @param path    the path
     *
     * @return the response entity containing the API response
     */

    private ResponseEntity<ApiResponse> buildResponse(
        HttpStatus status,
        String message,
        Object data,
        String path
    ) {
        ApiResponse response = ApiResponse.builder()
            .status(status.value())
            .message(message)
            .data(data)
            .timestamp(Instant.now().toString())
            .path(path)
            .build();
        return ResponseEntity.status(status).body(response);
    }
}