package com.taskmanagement.domain.entity;

import com.taskmanagement.domain.enums.Priority;
import com.taskmanagement.domain.enums.TaskStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Task entity representing a task in the system
 * Tracks task lifecycle, status, and time tracking
 */
@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, name = "project_id")
    private String projectId;
    
    @Column(name = "sprint_id")
    private String sprintId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;
    
    @Column(name = "assignee_id")
    private String assigneeId;
    
    @Column(nullable = false, name = "estimated_hours")
    private int estimatedHours;
    
    @Column(nullable = false, name = "actual_hours")
    private int actualHours;
    
    @Column(name = "blocking_reason", length = 500)
    private String blockingReason;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Constructors
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TaskStatus.TODO;
        this.actualHours = 0;
    }
    
    public Task(String projectId, String title, String description, Priority priority, int estimatedHours) {
        this();
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
    }
    
    // Business methods
    
    /**
     * Update the task status and set appropriate timestamps
     * @param newStatus the new status to set
     */
    public void updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        // Set started_at when moving to IN_PROGRESS
        if (newStatus == TaskStatus.IN_PROGRESS && this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
        
        // Set completed_at when moving to DONE
        if (newStatus == TaskStatus.DONE && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Add actual hours spent on the task
     * @param hours the hours to add
     */
    public void addActualHours(int hours) {
        this.actualHours += hours;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calculate remaining hours (estimated - actual)
     * @return remaining hours
     */
    public int getRemainingHours() {
        int remaining = estimatedHours - actualHours;
        return Math.max(0, remaining); // Never return negative
    }
    
    /**
     * Check if the task is overdue
     * Note: This is a placeholder - actual implementation would need a due date field
     * @return true if task is overdue
     */
    public boolean isOverdue() {
        // TODO: Implement when due date field is added
        return false;
    }
    
    /**
     * Check if the task is blocked
     * @return true if task status is BLOCKED
     */
    public boolean isBlocked() {
        return this.status == TaskStatus.BLOCKED;
    }
    
    /**
     * Update the modification timestamp
     */
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getSprintId() {
        return sprintId;
    }
    
    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskStatus getStatus() {
        return status;
    }
    
    public void setStatus(TaskStatus status) {
        this.status = status;
    }
    
    public String getAssigneeId() {
        return assigneeId;
    }
    
    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getEstimatedHours() {
        return estimatedHours;
    }
    
    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getActualHours() {
        return actualHours;
    }
    
    public void setActualHours(int actualHours) {
        this.actualHours = actualHours;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getBlockingReason() {
        return blockingReason;
    }
    
    public void setBlockingReason(String blockingReason) {
        this.blockingReason = blockingReason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
