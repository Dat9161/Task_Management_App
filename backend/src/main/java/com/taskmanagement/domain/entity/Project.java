package com.taskmanagement.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project entity representing a project in the system
 * Contains sprints and tasks, managed by a creator with multiple members
 */
@Entity
@Table(name = "projects")
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false, name = "creator_id")
    private String creatorId;
    
    @ElementCollection
    @CollectionTable(name = "project_members", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "user_id")
    private List<String> memberIds = new ArrayList<>();
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Project() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.memberIds = new ArrayList<>();
    }
    
    public Project(String name, String description, String creatorId) {
        this();
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        // Creator is automatically a member
        this.memberIds.add(creatorId);
    }
    
    // Business methods
    
    /**
     * Add a member to the project
     * @param userId the user ID to add
     */
    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Remove a member from the project
     * @param userId the user ID to remove
     */
    public void removeMember(String userId) {
        if (memberIds.remove(userId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * Check if a user is a member of the project
     * @param userId the user ID to check
     * @return true if user is a member
     */
    public boolean hasMember(String userId) {
        return memberIds.contains(userId);
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
    
    public List<String> getMemberIds() {
        return memberIds;
    }
    
    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
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
}
