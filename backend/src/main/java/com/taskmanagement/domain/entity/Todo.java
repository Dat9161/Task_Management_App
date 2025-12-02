package com.taskmanagement.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Todo entity representing a personal todo item
 * Tracks personal tasks for individual users
 */
@Entity
@Table(name = "todos")
public class Todo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, name = "user_id")
    private String userId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(name = "due_date")
    private LocalDate dueDate;
    
    @Column(nullable = false)
    private boolean completed;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Constructors
    public Todo() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }
    
    public Todo(String userId, String title, LocalDate dueDate) {
        this();
        this.userId = userId;
        this.title = title;
        this.dueDate = dueDate;
    }
    
    // Business methods
    
    /**
     * Mark the todo as completed and set completion timestamp
     */
    public void markAsCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Check if the todo is overdue
     * @return true if todo has a due date and it's in the past
     */
    public boolean isOverdue() {
        if (dueDate == null || completed) {
            return false;
        }
        return LocalDate.now().isAfter(dueDate);
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
