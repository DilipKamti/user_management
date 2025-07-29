package com.microservice.user.management.service.controllers;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.microservice.user.management.service.dto.ApiResponse;
import com.microservice.user.management.service.dto.AuthResponse;
import com.microservice.user.management.service.dto.LoginRequest;
import com.microservice.user.management.service.dto.RegisterRequest;
import com.microservice.user.management.service.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Auth endpoints for login and registration")
public class AuthController {

    private final UserService userService;

    @Operation(summary = "Register new user")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering user: {}", request.username());
        AuthResponse token = userService.register(request);
        log.info("Registration successful for user: {}", request.username());
        return ResponseEntity.ok(ApiResponse.success(token, "Registration successful"));
    }

    @Operation(summary = "Login with username and password")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.username());
        AuthResponse token = userService.login(request);
        log.info("Login successful for user: {}", request.username());
        return ResponseEntity.ok(ApiResponse.success(token, "Login successful"));
    }
}


