package com.bytebandit.gateway.service;

import com.bytebandit.gateway.exception.UserNotFoundException;
import com.bytebandit.gateway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUserByUserEmail(username);
    }

    private UserDetails loadUserByUserEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(
                    "User not found with email: " + userEmail
                ));
    }
}
