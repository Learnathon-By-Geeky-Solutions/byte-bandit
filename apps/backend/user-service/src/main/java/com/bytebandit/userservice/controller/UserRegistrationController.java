package com.bytebandit.userservice.controller;

    import com.bytebandit.userservice.dto.UserDto;
    import com.bytebandit.userservice.model.UserEntity;
    import com.bytebandit.userservice.request.UpdateUserRequest;
    import com.bytebandit.userservice.request.UserRegistrationRequest;
    import com.bytebandit.userservice.response.ApiResponse;
    import com.bytebandit.userservice.service.user.IUserRegistrationService;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.Instant;
    import java.util.UUID;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("${api.prefix}/users")
    public class UserRegistrationController {

        private final IUserRegistrationService userRegistrationService;

        @PostMapping("/register")
        public ResponseEntity<ApiResponse> registerUser(
                @Valid @RequestBody UserRegistrationRequest request) {
            UserEntity user = userRegistrationService.register(request);
            UserDto userDto = userRegistrationService.convertToDto(user);
            return buildResponse(
                    HttpStatus.OK,
                    "User registered successfully",
                    userDto,
                    "/users/register"
            );
        }

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

        @DeleteMapping("/{userId}/delete")
        public ResponseEntity<ApiResponse> deleteUserById(@PathVariable UUID userId) {
            userRegistrationService.deleteUserById(userId);
            return buildResponse(
                    HttpStatus.OK,
                    "User deleted successfully",
                    null,
                    "/users/delete/" + userId);
        }

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