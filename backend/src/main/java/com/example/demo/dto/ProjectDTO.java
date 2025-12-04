package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ProjectDTO {
    
    @NotBlank(message = "Project name is required")
    @Size(min = 1, max = 200, message = "Project name must be between 1 and 200 characters")
    private String name;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    // Manager ID will be set automatically from authentication context
    private Long managerId;
    
    private List<Long> memberIds;
    
    @Size(min = 6, message = "Project password must be at least 6 characters")
    private String projectPassword;
    
    // Constructors
    public ProjectDTO() {
    }
    
    public ProjectDTO(String name, String description, Long managerId, List<Long> memberIds) {
        this.name = name;
        this.description = description;
        this.managerId = managerId;
        this.memberIds = memberIds;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getManagerId() {
        return managerId;
    }
    
    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
    
    public List<Long> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
    
    public String getProjectPassword() {
        return projectPassword;
    }
    
    public void setProjectPassword(String projectPassword) {
        this.projectPassword = projectPassword;
    }
}
