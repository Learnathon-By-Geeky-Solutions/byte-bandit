package com.bytebandit.userservice.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lib.core.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    /**
     * User's full name.
     */
    @NotNull
    @Size(max = 20, message = "Full name must be less than 20 characters")
    private String fullName;

    /**
     * Password must meet requirements defined in ValidPassword annotation.
     */
    @ValidPassword
    private String password;
}