package com.bytebandit.userservice.response;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

/**
 * Standardized error response format for API errors. Used by GlobalExceptionHandler to create
 * consistent error responses across the application. Includes unique error ID, timestamp, HTTP
 * status, error details and path information.
 */
@Value
@Builder
public class ErrorResponse {
    String errorId;
    Instant timestamp;
    int status;
    String error;
    String message;
    String errorCode;
    String details;
    String path;

    /**
     * Create a new ErrorResponse object with the given parameters.
     *
     * @param status       HTTP status code
     * @param error        Error type
     * @param message      Error message
     * @param errorCode    Error code
     * @param details      Error details
     * @param path         Request path
     * @param uuidSupplier Supplier for generating unique error IDs
     *
     * @return New ErrorResponse object
     */

    public static ErrorResponse create(
        HttpStatus status,
        String error,
        String message,
        String errorCode,
        String details,
        String path,
        Supplier<UUID> uuidSupplier) {
        return ErrorResponse.builder()
            .errorId(uuidSupplier.get().toString())
            .timestamp(Instant.now())
            .status(status.value())
            .error(error)
            .message(message)
            .errorCode(errorCode)
            .details(details)
            .path(path)
            .build();
    }
}
