package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.response.RegisterResponseDto;
import com.bytebandit.userservice.dto.request.LoginRequestDto;
import com.bytebandit.userservice.dto.request.RegisterRequestDto;
import com.bytebandit.userservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody @Validated RegisterRequestDto registerRequestDto
    ) {
        log.info("AuthenticationController | registerUser");
        RegisterResponseDto registeredUser = authService.registerUser(registerRequestDto);
        log.info("Registration successful for user: {}", registerRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestBody @Validated LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        log.info("AuthenticationController | loginUser");
        authService.loginUser(loginRequestDto, response);
        log.info("Login successful");
        return ResponseEntity.status(HttpStatus.OK).body("Login successful");
    }
}
