package com.bytebandit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration class for setting up security settings in a Spring WebFlux application.
 * It defines the security filter chain and authorization rules for incoming HTTP requests.
 */
@Configuration
public class ProjectConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers(
                                        "/api/v1/user/login",
                                        "/api/v1/user/register"
                                ).permitAll()
                                .anyExchange().authenticated()
                );
        return http.build();
    }
}
