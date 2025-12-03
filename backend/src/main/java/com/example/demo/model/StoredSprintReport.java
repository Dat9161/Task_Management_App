package com.example.demo.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity representing a stored sprint report in the database.
 * Used for maintaining historical reports.
 */
@Entity
@Table(name = "stored_sprint_reports")
public class StoredSprintReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;
    
    @Column(nullable = false)
    private String sprintName;
    
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @Column(length = 2000)
    private String sprintSummary;
    
    @Column(nullable = false)
    private double taskCompletionRate;
    
    @Column(nullable = false)
    private double velocity;
    
    @Column(nullable = false)
    private int totalTasks;
    
    @Column(nullable = false)
    private int completedTasks;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date generatedAt;
    
    // Constructors
    public StoredSprintReport() {
        this.generatedAt = new Date();
    }
    
    public StoredSprintReport(Sprint sprint, String sprintName, Date startDate, Date endDate,
                             String sprintSummary, double taskCompletionRate, double velocity,
                             int totalTasks, int completedTasks) {
        this.sprint = sprint;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintSummary = sprintSummary;
        this.taskCompletionRate = taskCompletionRate;
        this.velocity = velocity;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.generatedAt = new Date();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Sprint getSprint() {
        return sprint;
    }
    
    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }
    
    public String getSprintName() {
        return sprintName;
    }
    
    public void setSprintName(String sprintName) {
        this.sprintName = sprintName;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public String getSprintSummary() {
        return sprintSummary;
    }
    
    public void setSprintSummary(String sprintSummary) {
        this.sprintSummary = sprintSummary;
    }
    
    public double getTaskCompletionRate() {
        return taskCompletionRate;
    }
    
    public void setTaskCompletionRate(double taskCompletionRate) {
        this.taskCompletionRate = taskCompletionRate;
    }
    
    public double getVelocity() {
        return velocity;
    }
    
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
    
    public int getTotalTasks() {
        return totalTasks;
    }
    
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    
    public int getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(int completedTasks) {
        this.completedTasks = completedTasks;
    }
    
    public Date getGeneratedAt() {
        return generatedAt;
    }
    
    public void setGeneratedAt(Date generatedAt) {
        this.generatedAt = generatedAt;
    }
}
