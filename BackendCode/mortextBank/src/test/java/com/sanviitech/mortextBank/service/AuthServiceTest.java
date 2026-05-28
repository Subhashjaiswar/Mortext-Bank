package com.sanviitech.mortextBank.service;

import com.sanviitech.mortextBank.dto.AuthResponse;
import com.sanviitech.mortextBank.dto.LoginRequest;
import com.sanviitech.mortextBank.dto.RegisterRequest;
import com.sanviitech.mortextBank.entity.User;
import com.sanviitech.mortextBank.exception.BadRequestException;
import com.sanviitech.mortextBank.exception.ResourceNotFoundException;
import com.sanviitech.mortextBank.repository.UserRepository;
import com.sanviitech.mortextBank.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setSecurityPin("1234");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setSecurityPin("1234");

        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setSecurityPin("1234");
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenProvider.generateToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getUserId());
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());

        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(tokenProvider, times(1)).generateToken(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testRegister_DuplicateEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(tokenProvider, never()).generateToken(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(tokenProvider.generateToken(any(UsernamePasswordAuthenticationToken.class))).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals(1L, response.getUserId());
        assertEquals("John Doe", response.getFullName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());

        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(tokenProvider, times(1)).generateToken(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("User not found with email: 'john@example.com'", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(tokenProvider, never()).generateToken(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testLogin_InvalidSecurityPin() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        LoginRequest invalidPinRequest = new LoginRequest();
        invalidPinRequest.setEmail("john@example.com");
        invalidPinRequest.setSecurityPin("9999");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            authService.login(invalidPinRequest);
        });

        assertEquals("Invalid security pin", exception.getMessage());

        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(tokenProvider, never()).generateToken(any(UsernamePasswordAuthenticationToken.class));
    }
}
