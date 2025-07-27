package com.microservice.user.management.service.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(@NotBlank(message = "First name is required") String firstName,

		@NotBlank(message = "Last name is required") String lastName,

		@NotBlank(message = "Username is required") @Size(min = 4, max = 20, message = "Username must be between 4-20 characters") String username,

		@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters") String password,

		@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

		Role role) {
}
