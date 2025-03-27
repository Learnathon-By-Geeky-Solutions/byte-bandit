package com.bytebandit.userservice.request;


import com.bytebandit.userservice.annotation.ValidPassword;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotNull
    @Size(max = 20, message = "Username must be less than 20 characters")
    private String fullName;
    /**
     * Password must meet requirements defined in ValidPassword annotation.
     */
    @ValidPassword
    private String password;
}
