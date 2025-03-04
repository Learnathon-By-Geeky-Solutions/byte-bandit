package com.bytebandit.userservice.service;

import com.bytebandit.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} to load user details by email.
 * It interacts with the {@link UserRepository} to retrieve user data from the database.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        return userRepository.findUserEntityByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("User with email " + userEmail + " not found!")
        );
    }
}
