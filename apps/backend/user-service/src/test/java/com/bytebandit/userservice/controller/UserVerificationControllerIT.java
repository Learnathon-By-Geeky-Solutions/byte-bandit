package com.bytebandit.userservice.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bytebandit.userservice.configurer.AbstractPostgresContainer;
import com.bytebandit.userservice.exception.EmailAlreadyVerifiedException;
import com.bytebandit.userservice.exception.FailedEmailVerificationAttemptException;
import com.bytebandit.userservice.exception.GlobalExceptionHandler;
import com.bytebandit.userservice.service.UserRegistrationService;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lib.core.enums.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserVerificationControllerIT extends AbstractPostgresContainer {

    @BeforeEach
    void setup(@Value("${local.server.port}") int port) {
        RestAssured.port = port;
    }

    private RequestSpecification requestSpec() {
        return given()
            .contentType("application/json");
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRegistrationService userRegistrationService;

    @Value("${client.host.uri}")
    private String clientHostUri;

    private static final String RESEND_PATH = "/resend-verification";

    @BeforeEach
    void resetMocks() {
        Mockito.reset(userRegistrationService);
    }

    /**
     * When the service throws FailedEmailVerificationAttemptException.
     * The GlobalExceptionHandler should redirect (302) to {clientHostUri}/email-verification
     */
    @Test
    void resendVerification_shouldRedirectOnFailedAttempt() throws Exception {
        doThrow(new FailedEmailVerificationAttemptException("Failed attempt"))
            .when(userRegistrationService).resendVerificationEmail("foo@example.com");

        String json = "{\"email\":\"foo@example.com\"}";

        mockMvc.perform(post(RESEND_PATH)
                .contentType("application/json")
                .content(json))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", clientHostUri + "/email-verification"));
    }

    /**
     * Unit-test the exception handler fallback. When HttpServletResponse.sendRedirect throws
     * IOException,
     * the handler should return 500 with the expected body.
     */
    @Test
    void handleFailedVerificationAttemptException_shouldReturnErrorBodyWhenRedirectFails()
        throws IOException {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        setField(handler, "clientHostUri", clientHostUri);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        doThrow(new IOException("redirect failed"))
            .when(response).sendRedirect(Mockito.anyString());

        ResponseEntity<String> result = handler.handleFailedVerificationAttemptException(
            new FailedEmailVerificationAttemptException("Failed attempt"), response
        );

        assertNotNull(result);

        assert result.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR;
        assert result.getBody() != null && result.getBody().contains(
            "Failed to redirect, please go to " + clientHostUri + "/login"
        );
    }

    /**
     * When UserRegistrationService.resendVerificationEmail(...) throws
     * EmailAlreadyVerifiedException, the GlobalExceptionHandler should
     * catch it and return 400 with the proper ErrorResponse JSON.
     */
    @Test
    void resendVerification_shouldReturnBadRequestWhenAlreadyVerified() {

        String email = "foo@example.com";
        doThrow(new EmailAlreadyVerifiedException("Email already verified for " + email))
            .when(userRegistrationService).resendVerificationEmail(email);

        String json = """
                {"email":"foo@example.com"}
                """;

        requestSpec()
            .body(json)
            .when()
            .post(RESEND_PATH)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("errorCode", equalTo(ErrorCode.EMAIL_ALREADY_VERIFIED.getCode()))
            .body("message", equalTo(ErrorCode.EMAIL_ALREADY_VERIFIED.getMessage()))
            .body("details", equalTo("Email already verified for " + email))
            .body("path", equalTo(RESEND_PATH))
            .body("timestamp", org.hamcrest.Matchers.notNullValue());
    }
}