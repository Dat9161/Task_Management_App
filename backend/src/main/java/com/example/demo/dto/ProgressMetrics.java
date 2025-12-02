package com.example.demo.dto;

import java.util.Map;

public class ProgressMetrics {
    
    private double completionPercentage;
    private Map<String, Integer> statusDistribution;
    private int totalTasks;
    private int completedTasks;
    private double velocity;
    
    // Constructors
    public ProgressMetrics() {
    }
    
    public ProgressMetrics(double completionPercentage, Map<String, Integer> statusDistribution,
                          int totalTasks, int completedTasks, double velocity) {
        this.completionPercentage = completionPercentage;
        this.statusDistribution = statusDistribution;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.velocity = velocity;
    }
    
    // Getters and Setters
    public double getCompletionPercentage() {
        return completionPercentage;
    }
    
    public void setCompletionPercentage(double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }
    
    public Map<String, Integer> getStatusDistribution() {
        return statusDistribution;
    }
    
    public void setStatusDistribution(Map<String, Integer> statusDistribution) {
        this.statusDistribution = statusDistribution;
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
    
    public double getVelocity() {
        return velocity;
    }
    
    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }
}
