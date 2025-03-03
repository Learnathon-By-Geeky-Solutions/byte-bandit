package com.bytebandit.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class ProjectConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults())
                .authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers(
                                        "/api/v1/**"
                                ).permitAll()
                                .anyExchange().authenticated()
                );
        return http.build();
    }
}
