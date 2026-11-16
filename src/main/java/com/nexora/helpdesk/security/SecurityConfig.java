
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
            // Disable CSRF for stateless REST APIs
            .csrf(csrf -> csrf.disable())

            // Authentication provider
            .authenticationProvider(authenticationProvider())

            // HTTP Basic Authentication
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

            // Stateless session management
            .sessionManagement(session ->
                session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Public APIs
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/register",
                    "/api/auth/login"
                ).permitAll()

                // User management - AGENT only
                .requestMatchers("/api/users/**")
                    .hasRole("AGENT")

                // Ticket creation
                .requestMatchers(HttpMethod.POST, "/api/tickets")
                    .hasAnyRole("USER", "AGENT")

                // Ticket reading
                .requestMatchers(HttpMethod.GET, "/api/tickets/**")
                    .hasAnyRole("USER", "AGENT")

                // Ticket status update - AGENT only
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/tickets/*/status"
                ).hasRole("AGENT")

                // Ticket priority update - AGENT only
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/tickets/*/priority"
                ).hasRole("AGENT")

                // Comments
                .requestMatchers("/api/comments/**")
                    .hasAnyRole("USER", "AGENT")

                // Ticket history
                .requestMatchers("/api/ticket-history/**")
                    .hasAnyRole("USER", "AGENT")

                // All remaining APIs require authentication
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
