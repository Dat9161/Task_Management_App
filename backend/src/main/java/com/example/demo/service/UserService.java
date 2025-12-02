package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing User operations.
 * Handles user registration, retrieval, and updates with password encryption.
 */
@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user with encrypted password.
     * Validates that username and email are unique.
     * 
     * @param userDTO the user data transfer object containing registration information
     * @return the created User entity
     * @throws ConflictException if username or email already exists
     * @throws ValidationException if user data is invalid
     */
    public User registerUser(UserDTO userDTO) {
        // Validate input
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }
        
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        // Check for unique username
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ConflictException("Username already exists: " + userDTO.getUsername());
        }
        
        // Check for unique email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ConflictException("Email already exists: " + userDTO.getEmail());
        }
        
        // Create new user with encrypted password
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole() != null ? userDTO.getRole() : UserRole.MEMBER);
        
        return userRepository.save(user);
    }
    
    /**
     * Get a user by their ID.
     * 
     * @param id the user ID
     * @return the User entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        if (id == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
    
    /**
     * Get a user by their username.
     * 
     * @param username the username
     * @return the User entity
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be null or empty");
        }
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username '" + username + "' not found"));
    }
    
    /**
     * Update an existing user's information.
     * Does not allow updating username or password through this method.
     * 
     * @param id the user ID
     * @param userDTO the updated user data
     * @return the updated User entity
     * @throws ResourceNotFoundException if user is not found
     * @throws ConflictException if email already exists for another user
     */
    public User updateUser(Long id, UserDTO userDTO) {
        if (id == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        if (userDTO == null) {
            throw new ValidationException("User data cannot be null");
        }
        
        User existingUser = getUserById(id);
        
        // Update email if provided and different
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail())) {
            // Check if new email is already taken by another user
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new ConflictException("Email already exists: " + userDTO.getEmail());
            }
            existingUser.setEmail(userDTO.getEmail());
        }
        
        // Update role if provided
        if (userDTO.getRole() != null) {
            existingUser.setRole(userDTO.getRole());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Check if a username already exists.
     * 
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if an email already exists.
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
