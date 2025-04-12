package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserDto;
import com.bytebandit.userservice.request.UserRegistrationRequest;
import com.bytebandit.userservice.service.user.IUserRegistrationService;
import jakarta.validation.Valid;
import java.time.Instant;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling user registration and management.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
@ControllerAdvice
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
    public ResponseEntity<ApiResponse<UserDto>> registerUser(
        @Valid @RequestBody UserRegistrationRequest request) {
        UserDto user = userRegistrationService.register(request);
        return buildResponse(
            HttpStatus.OK,
            "User registered successfully",
            user,
            "${api.prefix}/users/register"
        );
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

    private ResponseEntity<ApiResponse<UserDto>> buildResponse(
        HttpStatus status,
        String message,
        UserDto data,
        String path
    ) {
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
            .status(status.value())
            .message(message)
            .data(data)
            .timestamp(Instant.now().toString())
            .path(path)
            .build();
        return ResponseEntity.status(status).body(response);
    }
}