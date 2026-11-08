package com.nexora.helpdesk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.helpdesk.dto.LoginRequest;
import com.nexora.helpdesk.dto.RegisterRequest;
import com.nexora.helpdesk.dto.UserResponse;
import com.nexora.helpdesk.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final UserService userService;
	public AuthController(UserService userService) {
        this.userService = userService;
    }
// User Registration
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
 // User Login
    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@Valid @RequestBody LoginRequest request) {

        UserResponse response = userService.loginUser(request);

        return ResponseEntity.ok(response);
    }
}

