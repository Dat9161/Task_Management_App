package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    
    @Mock
    private Authentication authentication;
    
    @InjectMocks
    private AuthService authService;
    
    private LoginRequest validLoginRequest;
    
    @BeforeEach
    void setUp() {
        validLoginRequest = new LoginRequest("testuser", "password123");
    }
    
    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("test-jwt-token");
        
        // Act
        LoginResponse response = authService.login(validLoginRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals("Bearer", response.getType());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);
    }
    
    @Test
    void login_WithInvalidCredentials_ShouldThrowUnauthorizedException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Invalid credentials"));
        
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> authService.login(validLoginRequest));
        verify(jwtTokenProvider, never()).generateToken(any());
    }
    
    @Test
    void logout_WithValidToken_ShouldBlacklistToken() {
        // Arrange
        String token = "test-jwt-token";
        
        // Act
        authService.logout(token);
        
        // Assert
        assertTrue(authService.isTokenBlacklisted(token));
    }
    
    @Test
    void validateToken_WithBlacklistedToken_ShouldReturnFalse() {
        // Arrange
        String token = "test-jwt-token";
        authService.logout(token);
        
        // Act
        boolean isValid = authService.validateToken(token);
        
        // Assert
        assertFalse(isValid);
        // Token provider should not be called for blacklisted tokens
        verify(jwtTokenProvider, never()).validateToken(token);
    }
    
    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = "test-jwt-token";
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        
        // Act
        boolean isValid = authService.validateToken(token);
        
        // Assert
        assertTrue(isValid);
    }
}
