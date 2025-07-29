package com.microservice.user.management.service.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${skip-auth-endpoints}")
    private String skipEnpoints;

	private List<String> skipAuthEndpoints;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// Initialize skip list if not already done
        if (skipAuthEndpoints == null) {
            skipAuthEndpoints = List.of(skipEnpoints.split("\\s*,\\s*"));
        }


		String path = httpRequest.getRequestURI();
		log.info("➡️ Incoming request: {}", path);

        // ✅ Skip endpoints matching the configured list
        if (skipAuthEndpoints.stream().anyMatch(path::startsWith)) {
            log.info("⏭️ Skipping JWT filter for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

		String auth = httpRequest.getHeader("Authorization");

		if (auth != null && auth.startsWith("Bearer ")) {
			String token = auth.substring(7);
			if (jwtService.isTokenValid(token)) {
				String username = jwtService.extractUsername(token);
				String role = jwtService.extractRole(token);

				// ✅ Validate that user actually exists in DB
				if (userRepository.findByUsername(username).isEmpty()) {
					log.warn("User {} not found in database. Token is invalid.", username);
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}

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
