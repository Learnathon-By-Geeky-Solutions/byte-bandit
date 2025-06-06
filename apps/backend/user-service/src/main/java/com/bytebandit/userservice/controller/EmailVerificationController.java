package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.ResendVerificationRequest;
import com.bytebandit.userservice.exception.EmailVerificationExpiredException;
import com.bytebandit.userservice.exception.InvalidEmailVerificationLinkException;
import com.bytebandit.userservice.service.TokenVerificationService;
import com.bytebandit.userservice.service.UserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "Email verification related endpoints.")
public class EmailVerificationController {

    private final TokenVerificationService tokenVerificationService;
    private final UserRegistrationService userRegistrationService;

    @Value("${client.host.uri}")
    private String clientHostUri;

    /**
     * Verifies the email address of a user.
     *
     * @param token    The verification token.
     * @param userid   The user ID.
     * @param response The HTTP response.
     *
     * @return ResponseEntity with a redirect to the email verification confirmation page.
     */

    @Operation(
        summary = "Verify email",
        description = "Verifies the email address of a user using the provided token and user ID."
    )
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
        @RequestParam("token") @NotNull String token,
        @RequestParam("userid") @NotNull String userid,
        HttpServletResponse response
    ) {
        try {
            tokenVerificationService.verifyToken(
                token,
                userid,
                TokenType.EMAIL_VERIFICATION
            );
            response.sendRedirect(clientHostUri + "/email-verification?status=success");
            return ResponseEntity.status(HttpStatus.FOUND).build();

        } catch (EmailVerificationExpiredException ex) {
            try {
                response.sendRedirect(clientHostUri + "/email-verification?status=expired");
                return ResponseEntity.status(HttpStatus.FOUND).build();
            } catch (IOException ioEx) {
                return ResponseEntity.status(HttpStatus.GONE).body(
                    "Verification link expired. Please request a new one.");
            }
        } catch (InvalidEmailVerificationLinkException ex) {
            try {
                response.sendRedirect(clientHostUri + "/email-verification?status=invalid");
                return ResponseEntity.status(HttpStatus.FOUND).build();
            } catch (IOException ioEx) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Invalid verification link. Please check the URL or request a new one.");
            }
        } catch (Exception ex) {
            try {
                response.sendRedirect(clientHostUri + "/email-verification?status=error");
                return ResponseEntity.status(HttpStatus.FOUND).build();
            } catch (IOException ioEx) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Something went wrong. Please try again later.");
            }
        }
    }


    /**
     * Resends the verification email to the user.
     *
     * @param resendVerificationRequest The request containing the email address.
     *
     * @return ResponseEntity with the status of the resend operation.
     */
    @Operation(
        summary = "Resend verification email",
        description = "Resends the verification email to the user."
    )
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(
        @RequestBody ResendVerificationRequest resendVerificationRequest
    ) {
        log.info("Resending verification email to: {}", resendVerificationRequest.getEmail());
        userRegistrationService.resendVerificationEmail(resendVerificationRequest.getEmail());
        return ResponseEntity.ok("Verification email resent successfully.");
    }
}
