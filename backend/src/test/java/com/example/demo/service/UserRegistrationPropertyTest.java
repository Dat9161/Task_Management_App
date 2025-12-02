package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.ConflictException;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Property-based tests for User Registration.
 * Tests Property 1 with 100 iterations using random data generation.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserRegistrationPropertyTest {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Random random = new Random();
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        userRepository.deleteAll();
    }
    
    /**
     * Feature: task-project-management, Property 1: User registration creates unique accounts
     * Validates: Requirements 1.1
     * 
     * For any valid registration data (unique username, valid email, password), 
     * creating a user account should result in a new user with a unique identifier 
     * and all provided information correctly stored.
     */
    @Test
    void userRegistrationCreatesUniqueAccounts() {
        // Run 100 iterations with random data
        for (int i = 0; i < 100; i++) {
            // Generate random test data
            String username = generateRandomUsername();
            String email = username.toLowerCase() + i + "@example.com";
            String password = generateRandomPassword();
            UserRole role = random.nextBoolean() ? UserRole.MEMBER : UserRole.PROJECT_MANAGER;
            
            // Arrange
            UserDTO userDTO = new UserDTO(username, email, password, role);
            
            // Act
            User createdUser = userService.registerUser(userDTO);
            
            // Assert - User should be created with unique identifier
            assertNotNull(createdUser, 
                String.format("Iteration %d: Created user should not be null", i));
            assertNotNull(createdUser.getId(), 
                String.format("Iteration %d: User should have a unique identifier", i));
            
            // Assert - All provided information should be correctly stored
            assertEquals(username, createdUser.getUsername(), 
                String.format("Iteration %d: Username should match the provided value", i));
            assertEquals(email, createdUser.getEmail(), 
                String.format("Iteration %d: Email should match the provided value", i));
            assertEquals(role, createdUser.getRole(), 
                String.format("Iteration %d: Role should match the provided value", i));
            
            // Assert - Password should be encrypted (not stored in plain text)
            assertNotEquals(password, createdUser.getPassword(), 
                String.format("Iteration %d: Password should be encrypted, not stored in plain text", i));
            assertTrue(passwordEncoder.matches(password, createdUser.getPassword()),
                String.format("Iteration %d: Encrypted password should match the original password when verified", i));
            
            // Assert - User should be retrievable from database
            User retrievedUser = userRepository.findById(createdUser.getId()).orElse(null);
            assertNotNull(retrievedUser, 
                String.format("Iteration %d: User should be retrievable from database", i));
            assertEquals(createdUser.getId(), retrievedUser.getId(), 
                String.format("Iteration %d: Retrieved user should have the same ID", i));
            
            // Assert - Username and email should be unique (attempting duplicate should fail)
            UserDTO duplicateUsernameDTO = new UserDTO(username, "different" + i + "@example.com", password, role);
            assertThrows(ConflictException.class, 
                () -> userService.registerUser(duplicateUsernameDTO),
                String.format("Iteration %d: Registering with duplicate username should throw ConflictException", i));
            
            UserDTO duplicateEmailDTO = new UserDTO("differentuser" + i, email, password, role);
            assertThrows(ConflictException.class, 
                () -> userService.registerUser(duplicateEmailDTO),
                String.format("Iteration %d: Registering with duplicate email should throw ConflictException", i));
        }
    }
    
    /**
     * Generate a random username (3-50 characters, alphanumeric).
     */
    private String generateRandomUsername() {
        // Generate base username (3-30 characters) to leave room for uniqueness suffix
        int length = 3 + random.nextInt(28); // 3 to 30 characters
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // Add a shorter unique suffix to ensure uniqueness while staying under 50 chars
        sb.append(random.nextInt(100000)); // 5 digits max
        return sb.toString();
    }
    
    /**
     * Generate a random password (8-72 characters, BCrypt limit).
     */
    private String generateRandomPassword() {
        int length = 8 + random.nextInt(65); // 8 to 72 characters (BCrypt limit)
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
