package com.microservice.user.management.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.microservice.user.management.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.config.Customizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import com.microservice.user.management.service.services.CustomUserDetailsService;



@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	 private final JwtFilter jwtFilter;
	 private final UserRepository userRepo;
	
	/*
	 * Basic Authentication 
	 * 
	 * 
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
			.requestMatchers("/api/public").permitAll()
			.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults());
		
		return http.build();
	}*/
	
	
	/*
	 * Form-Based Authentication 
	 * 
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
			.requestMatchers("/api/public").permitAll()
			.requestMatchers("/api/admin").hasRole("ADMIN")
			.requestMatchers("/api/user").hasRole("USER")
			.anyRequest().hasRole("USER"))
			.formLogin(Customizer.withDefaults());
		
		return http.build();
	}*/
	
	
    /*
     * Required for in memory db testing purpose
     * 
     * @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("user")
                .password(passwordEncoder().encode("password"))
                .roles("USER")
                .build();

        UserDetails admin = User.withUsername("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, admin);
    }
    
    *
    *   @Bean
    	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    	}
    *
    *
    *
    */

	/*
	 * Configures the security filter chain to handle route-based authorization and
	 * attach JWT authentication before the UsernamePasswordAuthenticationFilter.
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/v1/auth/**").permitAll().requestMatchers("/api/v1/admin/**")
						.hasRole("ADMIN").requestMatchers("/api/v1/user/**").hasAnyRole("USER", "ADMIN").anyRequest()
						.authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configures the password encoder to use BCrypt.
	 */

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Custom UserDetailsService implementation wired with UserRepository.
	 */

	/*
	 * @Bean public UserDetailsService userDetailsService() { return new
	 * CustomUserDetailsService(userRepo); }
	 */

	/**
	 * Custom AuthenticationManager using DaoAuthenticationProvider that connects to
	 * DB via CustomUserDetailsService.
	 */

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		return new ProviderManager(daoAuthenticationProvider);
	}

}
