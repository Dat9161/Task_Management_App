package com.taskmanagement.domain.entity;

import com.taskmanagement.domain.enums.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User entity representing a user in the system
 * Supports authentication and role-based access control
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.role = Role.TEAM_MEMBER; // Default role
    }
    
    public User(String username, String email, String passwordHash, Role role) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    
    // Business methods
    
    /**
     * Check if user has a specific role
     * @param role the role to check
     * @return true if user has the role
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }
    
    /**
     * Update the last login timestamp to current time
     */
    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
