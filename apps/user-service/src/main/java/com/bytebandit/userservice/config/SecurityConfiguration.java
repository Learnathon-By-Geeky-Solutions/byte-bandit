package com.bytebandit.userservice.config;

import com.bytebandit.userservice.filter.JwtFilter;
import com.bytebandit.userservice.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * this is a configuration class for security and authentication
 *
 * Purpose:
 * - Defines beans required for authentication, password encoding, and security filters.
 * - Configures authentication provider and security filter chain for handling requests.
 *
 * Approach:
 * - Provides {@link PasswordEncoder} for secure password hashing.
 * - Configures {@link SecurityFilterChain} to define authentication and authorization rules.
 * - Registers {@link AuthenticationProvider} to manage user authentication.
 * - Uses JWT-based authentication by integrating a custom JWT filter.
 *
 * Alternative (Enhancements):
 * - Use OAuth2 for delegated authentication along with JWT.
 *
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Value("${application.bcrypt.strength}")
    private Integer bcryptPasswordStrength;

    /**
     * Provides a {@link BCryptPasswordEncoder} bean for encoding passwords.
     *
     * @return a {@link PasswordEncoder} instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptPasswordStrength);
    }

    /**
     * Purpose:
     * - Configures the security filter chain for handling authentication
     * - Stateless session management
     * Approach:
     * - Enables CORS and CSRF protection with default configurations.
     * - Allows unrestricted access to `/api/v1/user/login`, /api/v1/user/register, requires authentication for others.
     * - Configures session management as stateless {@link SessionCreationPolicy.STATELESS}.
     * - Adds a custom JWT authentication filter before {@link UsernamePasswordAuthenticationFilter}.
     * - Builds and returns the configured {@link SecurityFilterChain}.
     * Alternative:
     * - Use OAuth2 authentication with JWT
     *
     * @param http {@link HttpSecurity} - The security configuration object.
     * @return {@link SecurityFilterChain} - The configured security filter chain.
     * @throws Exception If any error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        req -> req.requestMatchers(
                                "/api/v1/user/login",
                                "/api/v1/user/register"
                        ).permitAll().anyRequest().authenticated()
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Purpose:
     * - Configures an authentication provider
     * - Uses {@link DaoAuthenticationProvider} to retrieve user details and verify passwords.
     * Approach:
     * - Creates an instance of {@link DaoAuthenticationProvider} (Data Access Object authentication provider).
     * - Sets a custom {@link org.springframework.security.core.userdetails.UserDetailsService} to load user details.
     * - Configures password encoding using {@link #passwordEncoder()}.
     * - Returns the configured authentication provider .
     * <p>
     * Alternative:
     *
     * @return {@link AuthenticationProvider} - Configured authentication provider bean.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
