package com.nexora.helpdesk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

// Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

// Authentication Provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(customUserDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

 // Security Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authenticationProvider(authenticationProvider())

            .httpBasic(basic -> basic
                .authenticationEntryPoint((request, response, ex) -> {
                    response.setStatus(
                            HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                        "{\"status\":401,\"error\":\"Unauthorized\","
                        + "\"message\":\"Authentication required\"}");
                })
            )

            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            .authorizeHttpRequests(auth -> auth

            		.requestMatchers(
            			    "/",
            			    "/index.html",
            			    "/register.html",
            			    "/user-dashboard.html",
            			    "/agent-dashboard.html",
            			    "/create-ticket.html",
            			    "/my-tickets.html",
            			    "/ticket-details.html",
            			    "/css/**",
            			    "/js/**"
            			).permitAll()
                
            		// Public APIs
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll()

                // User Management - AGENT
                .requestMatchers("/api/users/**")
                    .hasRole("AGENT")

                // Ticket Creation
                .requestMatchers(HttpMethod.POST, "/api/tickets")
                    .hasAnyRole("USER", "AGENT")

                // Ticket Reading
                .requestMatchers(HttpMethod.GET, "/api/tickets/**")
                    .hasAnyRole("USER", "AGENT")

                // Ticket Status Update
                .requestMatchers(
                    HttpMethod.PUT, "/api/tickets/*/status")
                    .hasRole("AGENT")

                // Ticket Priority Update
                .requestMatchers(
                    HttpMethod.PUT, "/api/tickets/*/priority")
                    .hasRole("AGENT")

                // Comments
                .requestMatchers("/api/comments/**")
                .hasAnyRole("USER", "AGENT")

                // Ticket History
                .requestMatchers("/api/ticket-history/**")
                    .hasAnyRole("USER", "AGENT")

                // Remaining APIs
                .anyRequest().authenticated()
            );

        return http.build();
    }
}