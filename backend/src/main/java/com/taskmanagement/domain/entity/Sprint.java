package com.taskmanagement.domain.entity;

import com.taskmanagement.domain.enums.SprintStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Sprint entity representing a sprint in the Scrum process
 * Contains tasks and tracks sprint lifecycle
 */
@Entity
@Table(name = "sprints")
public class Sprint {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, name = "project_id")
    private String projectId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;
    
    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SprintStatus status;
    
    @ElementCollection
    @CollectionTable(name = "sprint_tasks", joinColumns = @JoinColumn(name = "sprint_id"))
    @Column(name = "task_id")
    private List<String> taskIds = new ArrayList<>();
    
    // Constructors
    public Sprint() {
        this.status = SprintStatus.PLANNED;
        this.taskIds = new ArrayList<>();
    }
    
    public Sprint(String projectId, String name, LocalDate startDate, LocalDate endDate) {
        this();
        this.projectId = projectId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Business methods
    
    /**
     * Check if the sprint is currently active
     * @return true if sprint is active
     */
    public boolean isActive() {
        return this.status == SprintStatus.ACTIVE;
    }
    
    /**
     * Check if the sprint has expired (end date has passed)
     * @return true if sprint is expired
     */
    public boolean isExpired() {
        return LocalDate.now().isAfter(this.endDate);
    }
    
    /**
     * Add a task to the sprint
     * @param taskId the task ID to add
     */
    public void addTask(String taskId) {
        if (!taskIds.contains(taskId)) {
            taskIds.add(taskId);
        }
    }
    
    /**
     * Remove a task from the sprint
     * @param taskId the task ID to remove
     */
    public void removeTask(String taskId) {
        taskIds.remove(taskId);
    }
    
    /**
     * Get the duration of the sprint in days
     * @return number of days in the sprint
     */
    public int getDurationInDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    
    /**
     * Validate that start date is before end date
     * @return true if dates are valid
     */
    public boolean hasValidDates() {
        return startDate != null && endDate != null && !startDate.isAfter(endDate);
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public SprintStatus getStatus() {
        return status;
    }
    
    public void setStatus(SprintStatus status) {
        this.status = status;
    }
    
    public List<String> getTaskIds() {
        return taskIds;
    }
    
    public void setTaskIds(List<String> taskIds) {
        this.taskIds = taskIds;
    }
}
