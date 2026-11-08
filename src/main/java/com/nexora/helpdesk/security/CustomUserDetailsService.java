package com.nexora.helpdesk.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nexora.helpdesk.entity.User;
import com.nexora.helpdesk.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

   @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
    	this.userRepository = userRepository;
    }

 // Load user from database using email
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

    	String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->new UsernameNotFoundException("User not found with email: " + normalizedEmail));
        
// Validate database role
        String userRole = user.getRole().name();

        if (!userRole.equals("USER") && !userRole.equals("AGENT")) {
            throw new UsernameNotFoundException("Invalid user role");
        }

// Convert role into Spring Security authority
        String role = "ROLE_" + userRole;


// Return UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}