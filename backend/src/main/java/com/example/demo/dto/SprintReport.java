package com.example.demo.dto;

import com.example.demo.model.Task;
import java.util.Date;
import java.util.List;

public class SprintReport {
    
    private Long sprintId;
    private String sprintName;
    private Date startDate;
    private Date endDate;
    private String sprintSummary;
    private List<Task> completedTasks;
    private double taskCompletionRate;
    private double velocity;
    private ProgressMetrics metrics;
    private List<Double> burndownChartData;
    
    // Constructors
    public SprintReport() {
    }
    
    public SprintReport(Long sprintId, String sprintName, Date startDate, Date endDate,
                       String sprintSummary, List<Task> completedTasks, double taskCompletionRate,
                       double velocity, ProgressMetrics metrics, List<Double> burndownChartData) {
        this.sprintId = sprintId;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sprintSummary = sprintSummary;
        this.completedTasks = completedTasks;
        this.taskCompletionRate = taskCompletionRate;
        this.velocity = velocity;
        this.metrics = metrics;
        this.burndownChartData = burndownChartData;
    }
    
    // Getters and Setters
    public Long getSprintId() {
        return sprintId;
    }
    
    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
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
    
    public List<Task> getCompletedTasks() {
        return completedTasks;
    }
    
    public void setCompletedTasks(List<Task> completedTasks) {
        this.completedTasks = completedTasks;
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
    
    public ProgressMetrics getMetrics() {
        return metrics;
    }
    
    public void setMetrics(ProgressMetrics metrics) {
        this.metrics = metrics;
    }
    
    public List<Double> getBurndownChartData() {
        return burndownChartData;
    }
    
    public void setBurndownChartData(List<Double> burndownChartData) {
        this.burndownChartData = burndownChartData;
    }
}
