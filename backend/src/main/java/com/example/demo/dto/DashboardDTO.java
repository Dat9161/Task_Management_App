package com.example.demo.dto;

import com.example.demo.model.Project;
import com.example.demo.model.Sprint;
import com.example.demo.model.Task;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Dashboard data.
 * Contains all information needed to display a user's dashboard.
 */
public class DashboardDTO {
    
    private Map<String, List<Task>> tasksByStatus;
    private List<Task> upcomingDeadlines;
    private List<Task> overdueTasks;
    private List<Project> activeProjects;
    private List<Sprint> currentSprints;
    private List<String> recentActivity;
    
    public DashboardDTO() {
    }
    
    public DashboardDTO(Map<String, List<Task>> tasksByStatus, 
                       List<Task> upcomingDeadlines,
                       List<Task> overdueTasks,
                       List<Project> activeProjects,
                       List<Sprint> currentSprints,
                       List<String> recentActivity) {
        this.tasksByStatus = tasksByStatus;
        this.upcomingDeadlines = upcomingDeadlines;
        this.overdueTasks = overdueTasks;
        this.activeProjects = activeProjects;
        this.currentSprints = currentSprints;
        this.recentActivity = recentActivity;
    }
    
    public Map<String, List<Task>> getTasksByStatus() {
        return tasksByStatus;
    }
    
    public void setTasksByStatus(Map<String, List<Task>> tasksByStatus) {
        this.tasksByStatus = tasksByStatus;
    }
    
    public List<Task> getUpcomingDeadlines() {
        return upcomingDeadlines;
    }
    
    public void setUpcomingDeadlines(List<Task> upcomingDeadlines) {
        this.upcomingDeadlines = upcomingDeadlines;
    }
    
    public List<Task> getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(List<Task> overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public List<Project> getActiveProjects() {
        return activeProjects;
    }
    
    public void setActiveProjects(List<Project> activeProjects) {
        this.activeProjects = activeProjects;
    }
    
    public List<Sprint> getCurrentSprints() {
        return currentSprints;
    }
    
    public void setCurrentSprints(List<Sprint> currentSprints) {
        this.currentSprints = currentSprints;
    }
    
    public List<String> getRecentActivity() {
        return recentActivity;
    }
    
    public void setRecentActivity(List<String> recentActivity) {
        this.recentActivity = recentActivity;
    }
}
