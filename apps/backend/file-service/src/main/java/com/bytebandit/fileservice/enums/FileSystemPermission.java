package com.bytebandit.fileservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSystemPermission {
    
    VIEWER("viewer"),
    EDITOR("editor"),
    OWNER("owner"),
    NO_ACCESS("no_access");
    
    private final String permission;
    
    @JsonValue
    public String getValue() {
        return permission;
    }
    
    /**
     * Convert a string value to a FileSystemPermission enum.
     *
     * @param value the string value to convert
     *
     * @return the corresponding FileSystemPermission enum
     */
    @JsonCreator
    public static FileSystemPermission fromValue(String value) {
        for (FileSystemPermission permission : FileSystemPermission.values()) {
            if (permission.permission.equalsIgnoreCase(value)) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Invalid permission: " + value);
    }
    
}
