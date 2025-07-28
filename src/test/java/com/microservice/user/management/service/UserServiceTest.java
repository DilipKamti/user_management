package com.microservice.user.management.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import com.microservice.user.management.service.dto.*;
import com.microservice.user.management.service.model.User;
import com.microservice.user.management.service.repository.UserRepository;
import com.microservice.user.management.service.services.JwtService;
import com.microservice.user.management.service.services.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterNewUser_WhenValidInputProvided() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john_doe", "password123", "john@example.com", Role.USER);

        User mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .username("john_doe")
                .email("john@example.com")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken("john_doe", "USER")).thenReturn("jwt-token");

        AuthResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void shouldThrowException_WhenUsernameAlreadyExistsDuringRegistration() {
        RegisterRequest request = new RegisterRequest("Jane", "Smith", "john_doe", "password123", "jane@example.com", Role.USER);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(new User()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.register(request));

        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully_WhenValidCredentialsProvided() {
        LoginRequest request = new LoginRequest("john_doe", "password123");

        User mockUser = User.builder()
                .id(1L)
                .username("john_doe")
                .password("encoded-password")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken("john_doe", "USER")).thenReturn("jwt-token");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        AuthResponse response = userService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void shouldThrowException_WhenUserDoesNotExistDuringLogin() {
        LoginRequest request = new LoginRequest("unknown_user", "password123");

        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        assertThrows(NoSuchElementException.class, () -> userService.login(request));
    }
}
