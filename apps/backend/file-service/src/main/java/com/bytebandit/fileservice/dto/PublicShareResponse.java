package com.bytebandit.fileservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublicShareResponse {
    private String link;
    private String permission;
}
