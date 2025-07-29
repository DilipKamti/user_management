package com.microservice.user.management.service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class HelloController {

	@GetMapping("/public")
	public String publicEndpoint() {
		return "This is a public endpoint";
	}

	@GetMapping("/private")
	public String privateEndpoint() {
		return "This is a secured private endpoint";
	}

	@GetMapping("/user")
	public String userEndpoint() {
		return "This is a USER secured endpoint.";
	}

	@GetMapping("/admin")
	public String adminEndpoint() {
		return "This is an ADMIN secured endpoint.";
	}
}
