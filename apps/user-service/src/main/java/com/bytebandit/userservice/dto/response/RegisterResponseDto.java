package com.bytebandit.userservice.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Data
@Builder
public class RegisterResponseDto {
    private String email;
    private Timestamp createdAt;
}
