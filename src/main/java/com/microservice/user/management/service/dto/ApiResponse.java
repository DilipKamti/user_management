package com.microservice.user.management.service.dto;

import java.time.LocalDateTime;

public record ApiResponse<T>(T data, String message, boolean success, LocalDateTime timestamp) {

	public static <T> ApiResponse<T> success(T data, String message) {
		return new ApiResponse<>(data, message, true, LocalDateTime.now());
	}

	public static <T> ApiResponse<T> failure(String message) {
		return new ApiResponse<>(null, message, false, LocalDateTime.now());
	}

}
