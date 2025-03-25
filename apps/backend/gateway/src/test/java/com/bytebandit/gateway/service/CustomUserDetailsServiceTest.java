package com.bytebandit.gateway.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.bytebandit.gateway.entity.UserEntity;
import com.bytebandit.gateway.exception.UserNotFoundException;
import com.bytebandit.gateway.repository.UserRepository;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    /** Test that determines loadUserByUserName should return UserDetails. */
    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        Random random = new Random();
        int randomInt = random.nextInt(1000);
        String email = "test" + randomInt + "@example.com";
        UserEntity mockUser = new UserEntity();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertFalse(userDetails.isEnabled());
    }

    /** Test to see if expected exception is thrown.*/
    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(email));
    }
}
