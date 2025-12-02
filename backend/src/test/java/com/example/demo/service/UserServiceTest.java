package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.ConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    private UserDTO validUserDTO;
    private User validUser;
    
    @BeforeEach
    void setUp() {
        validUserDTO = new UserDTO("testuser", "test@example.com", "password123", UserRole.MEMBER);
        validUser = new User("testuser", "test@example.com", "encodedPassword", UserRole.MEMBER);
        validUser.setId(1L);
    }
    
    @Test
    void registerUser_WithValidData_ShouldCreateUser() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        
        // Act
        User result = userService.registerUser(validUserDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void registerUser_WithDuplicateUsername_ShouldThrowConflictException() {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        
        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.registerUser(validUserDTO));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void registerUser_WithDuplicateEmail_ShouldThrowConflictException() {
        // Arrange
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.registerUser(validUserDTO));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void registerUser_WithNullData_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> userService.registerUser(null));
    }
    
    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        
        // Act
        User result = userService.getUserById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }
    
    @Test
    void getUserById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }
    
    @Test
    void updateUser_WithValidData_ShouldUpdateUser() {
        // Arrange
        UserDTO updateDTO = new UserDTO(null, "newemail@example.com", null, UserRole.PROJECT_MANAGER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(validUser);
        
        // Act
        User result = userService.updateUser(1L, updateDTO);
        
        // Assert
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowConflictException() {
        // Arrange
        UserDTO updateDTO = new UserDTO(null, "existing@example.com", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(validUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        // Act & Assert
        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDTO));
        verify(userRepository, never()).save(any(User.class));
    }
}
