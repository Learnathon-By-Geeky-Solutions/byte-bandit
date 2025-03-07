package com.bytebandit.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class RegisterRequestDto {

    @Email(message = "Invalid email format detected")
    @NotNull(message = "Email field cannot be null")
    private String email;

    @NotNull(message = "Password field cannot be null")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}