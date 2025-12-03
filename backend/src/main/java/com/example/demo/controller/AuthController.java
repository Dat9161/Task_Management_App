package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Handles user registration, login, and logout.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user account.
     * 
     * @param userDTO User registration data
     * @return Created user information
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    /**
     * Authenticate user and generate JWT token.
     * 
     * @param loginRequest User credentials
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Logout user and invalidate session.
     * 
     * @param token JWT token from Authorization header
     * @return Success message
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        // Extract token from "Bearer <token>" format
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }
}
