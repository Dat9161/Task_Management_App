package com.taskmanagement.domain.entity;

import com.taskmanagement.domain.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification entity representing a notification for a user
 * Used to inform users about important events in the system
 */
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, name = "user_id")
    private String userId;
    
    @Column(nullable = false, length = 500)
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;
    
    @Column(nullable = false)
    private boolean read;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
    
    public Notification(String userId, String message, NotificationType type) {
        this();
        this.userId = userId;
        this.message = message;
        this.type = type;
    }
    
    // Business methods
    
    /**
     * Mark the notification as read
     */
    public void markAsRead() {
        this.read = true;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
