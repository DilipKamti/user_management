package com.microservice.user.management.service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.user.management.service.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User APIs", description = "Endpoints accessible by USER and ADMIN")
public class UserController {

    @GetMapping("/info")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user info (USER or ADMIN access)")
    public ResponseEntity<ApiResponse<String>> userInfo() {
        log.info("User info endpoint accessed");
        return ResponseEntity.ok(ApiResponse.success("This is USER-only or ADMIN-accessible data", "Fetched successfully"));
    }
}


