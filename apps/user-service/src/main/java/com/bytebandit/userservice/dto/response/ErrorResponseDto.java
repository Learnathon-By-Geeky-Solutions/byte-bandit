package com.bytebandit.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

// ToDo: Implement the ErrorResponseDto class

@Getter
@Setter
@Builder
public class ErrorResponseDto {
    private int status;
    private String message;
    private Map<String, String> errors;

    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
        this.errors = new HashMap<>();
    }

    public ErrorResponseDto(
            int status,
            String message,
            Map<String, String> errors
    ) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }
}
