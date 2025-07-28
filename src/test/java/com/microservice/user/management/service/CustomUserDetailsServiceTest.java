package com.microservice.user.management.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microservice.user.management.service.dto.Role;
import com.microservice.user.management.service.model.User;
import com.microservice.user.management.service.repository.UserRepository;
import com.microservice.user.management.service.services.CustomUserDetailsService;

//This tells JUnit 5 to use Mockito's extension, which enables @Mock and @InjectMocks annotations
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

	// This creates a mock (fake) object of UserRepository
	@Mock
	private UserRepository userRepository;

	// This creates an instance of CustomUserDetailsService and automatically
	// injects the mock userRepository into it
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	// Test case 1: When the user is found in the repository
	@Test
	void testLoadUserByUsername_UserExists() {
		// Arrange: Prepare test data
		String username = "john";
		User mockUser = User.builder().id(1L).username(username).password("password").role(Role.USER).build();

		// When userRepository.findByUsername is called, return mockUser
		Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

		// Act: Call the service method
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

		// Assert: Verify the results
		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
	}

	// Test case 2: When the user is NOT found in the repository
	@Test
	void testLoadUserByUsername_UserNotFound() {
		String username = "not_found";

		// Simulate that userRepository returns empty (user doesn't exist)
		Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

		// Assert that UsernameNotFoundException is thrown
		assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
	}
	
	@Test
	void testLoadUserByUsername_VerifyRepoCall() {
	    String username = "john";
	    User mockUser = User.builder().username(username).build();

	    Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

	    customUserDetailsService.loadUserByUsername(username);

	    // Verify that the repository method was called once with "john"
	    Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
	}

}
