package com.bytebandit.userservice.filter;

import com.bytebandit.userservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Custom filter that extends {@link OncePerRequestFilter} to handle JWT-based authentication in incoming HTTP requests.
 * It verifies the JWT token and sets the authenticated user in the {@link SecurityContextHolder}.
 *
 * @see JwtService
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see OncePerRequestFilter
 * @see SecurityContextHolder
 */
@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Purpose:
     * - Intercepts incoming HTTP requests to validate JWT authentication.
     * - Allows permitted routes to pass through without authentication checks.
     * - Extracts and verifies JWT token from Authorization header.
     * - Sets authenticated user in {@link SecurityContextHolder} if token is valid.
     *
     * Approach:
     * - Defines a list of permitted routes that do not require authentication.
     * - Extracts the `Authorization` header and checks for a Bearer token.
     * - If the token is present, extracts the username using {@link JwtService}.
     * - Loads user details using {@link org.springframework.security.core.userdetails.UserDetailsService}.
     * - Validates the token and sets authentication in {@link SecurityContextHolder}.
     * - Proceeds with the request filter chain after authentication checks.
     *
     * Alternative:
     * - Use API Gateway for token validation instead of handling it in the filter.
     * - Implement refresh tokens to prevent users from having to log in frequently.
     * - Use session-based authentication instead of JWT if stateless authentication is not required.
     *
     * @param request {@link HttpServletRequest} - Incoming HTTP request.
     * @param response {@link HttpServletResponse} - Outgoing HTTP response.
     * @param filterChain {@link FilterChain} - Chain of filters in the request lifecycle.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException If an I/O error occurs while processing the request.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final List<String> permittedRoutes = List.of(
                "/api/v1/user/register",
                "/api/v1/user/login",
                "/api/v1/user/test"
                );
        if (permittedRoutes.contains(request.getServletPath())) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // todo: Consider returning an explicit 401 when missing or malformed Bearer token.
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isValidToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
