package com.microservice.user.management.service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.microservice.user.management.service.repository.UserRepository;
import com.microservice.user.management.service.services.JwtService;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		String path = httpRequest.getRequestURI();
		log.info("================> {}", path);

		// ✅ Skip auth endpoints (like /auth/register or /auth/login)
		if (path.startsWith("/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		String auth = httpRequest.getHeader("Authorization");

		if (auth != null && auth.startsWith("Bearer ")) {
			String token = auth.substring(7);
			if (jwtService.isTokenValid(token)) {
				String username = jwtService.extractUsername(token);
				String role = jwtService.extractRole(token);

				log.info("Setting security context for user: {}, role: {}", username, role);

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						username, null, List.of(() -> "ROLE_" + role));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			} else {
				log.warn("Invalid JWT token detected");
			}
		}

		filterChain.doFilter(request, response);

	}
}
