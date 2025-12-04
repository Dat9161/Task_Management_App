package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Service class for handling authentication operations.
 * Manages login, logout, and JWT token generation.
 */
@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    
    // In-memory token blacklist for logout functionality
    // In production, use Redis or database for distributed systems
    private final Set<String> tokenBlacklist = new HashSet<>();
    
    @Autowired
    public AuthService(AuthenticationManager authenticationManager, 
                      JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Autowired
    private UserService userService;
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest the login credentials
     * @return LoginResponse containing JWT token and user information
     * @throws UnauthorizedException if credentials are invalid
     */
    public LoginResponse login(LoginRequest loginRequest) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            // Get user details
            var user = userService.getUserByUsername(loginRequest.getUsername());
            
            return new LoginResponse(jwt, user.getId(), user.getUsername(), 
                                   user.getEmail(), user.getRole().toString());
            
        } catch (BadCredentialsException ex) {
            throw new UnauthorizedException("Invalid username or password");
        } catch (Exception ex) {
            throw new UnauthorizedException("Authentication failed: " + ex.getMessage());
        }
    }
    
    /**
     * Logout user by invalidating their JWT token.
     * Adds token to blacklist to prevent further use.
     * 
     * @param token the JWT token to invalidate
     */
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            // Add token to blacklist
            tokenBlacklist.add(token);
            
            // Clear security context
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * Check if a token has been invalidated (blacklisted).
     * 
     * @param token the JWT token to check
     * @return true if token is blacklisted, false otherwise
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.contains(token);
    }
    
    /**
     * Validate if a token is still valid and not blacklisted.
     * 
     * @param token the JWT token to validate
     * @return true if token is valid and not blacklisted
     */
    public boolean validateToken(String token) {
        return !isTokenBlacklisted(token) && jwtTokenProvider.validateToken(token);
    }
}
