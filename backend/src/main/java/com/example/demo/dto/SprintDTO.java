package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import java.util.Date;

public class SprintDTO {
    
    @NotBlank(message = "Sprint name is required")
    @Size(min = 1, max = 200, message = "Sprint name must be between 1 and 200 characters")
    private String name;
    
    @NotNull(message = "Project ID is required")
    private Long projectId;
    
    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date cannot be in the past")
    private Date startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private Date endDate;
    
    // Constructors
    public SprintDTO() {
    }
    
    public SprintDTO(String name, Long projectId, Date startDate, Date endDate) {
        this.name = name;
        this.projectId = projectId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getProjectId() {
        return projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
    
    // Custom validation method to check if end date is after start date
    public boolean isEndDateAfterStartDate() {
        if (startDate != null && endDate != null) {
            return endDate.after(startDate);
        }
        return true;
    }
}
