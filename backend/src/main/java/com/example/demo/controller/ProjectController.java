package com.example.demo.controller;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.model.Project;
import com.example.demo.security.UserDetailsImpl;
import com.example.demo.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for project management operations.
 * Handles CRUD operations for projects and member management.
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProjectController {
    
    @Autowired
    private ProjectService projectService;
    
    /**
     * Create a new project.
     * The authenticated user will be set as the project manager.
     * 
     * @param projectDTO Project data
     * @param authentication Current authentication
     * @return Created project
     */
    @PostMapping
    public ResponseEntity<Project> createProject(
            @Valid @RequestBody ProjectDTO projectDTO,
            Authentication authentication) {
        // Get current user ID from authentication
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        projectDTO.setManagerId(userDetails.getId());
        
        Project project = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }
    
    /**
     * Get project by ID.
     * 
     * @param id Project ID
     * @return Project details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    
    /**
     * Update project information.
     * 
     * @param id Project ID
     * @param projectDTO Updated project data
     * @return Updated project
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        Project project = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(project);
    }
    
    /**
     * Delete a project.
     * 
     * @param id Project ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all projects for a user.
     * 
     * @param userId User ID
     * @return List of projects
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Project>> getProjectsByUser(@PathVariable Long userId) {
        List<Project> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(projects);
    }
    
    /**
     * Add a member to a project.
     * 
     * @param id Project ID
     * @param userId User ID to add
     * @return Success message
     */
    @PostMapping("/{id}/members/{userId}")
    public ResponseEntity<String> addMemberToProject(
            @PathVariable Long id,
            @PathVariable Long userId) {
        projectService.addMemberToProject(id, userId);
        return ResponseEntity.ok("Member added successfully");
    }
    
    /**
     * Remove a member from a project.
     * 
     * @param id Project ID
     * @param userId User ID to remove
     * @return No content
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMemberFromProject(
            @PathVariable Long id,
            @PathVariable Long userId) {
        projectService.removeMemberFromProject(id, userId);
        return ResponseEntity.noContent().build();
    }
}
