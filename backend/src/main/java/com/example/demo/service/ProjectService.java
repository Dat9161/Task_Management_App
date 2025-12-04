package com.example.demo.service;

import com.example.demo.dto.ProjectDTO;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for managing Project operations.
 * Handles project creation, updates, deletion, and member management.
 */
@Service
@Transactional
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    
    @Autowired
    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository,
                         org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Create a new project with the specified manager.
     * The manager ID must be set in the DTO before calling this method.
     * 
     * @param projectDTO the project data transfer object (with managerId set)
     * @return the created Project entity
     * @throws ValidationException if project data is invalid
     * @throws ResourceNotFoundException if manager user is not found
     */
    public Project createProject(ProjectDTO projectDTO) {
        // Validate input
        if (projectDTO == null) {
            throw new ValidationException("Project data cannot be null");
        }
        
        if (projectDTO.getName() == null || projectDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Project name is required");
        }
        
        if (projectDTO.getManagerId() == null) {
            throw new ValidationException("Manager ID must be set");
        }
        
        // Get manager user
        User manager = userRepository.findById(projectDTO.getManagerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", projectDTO.getManagerId()));
        
        // Create new project
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setManager(manager);
        
        // Encrypt and set project password if provided
        if (projectDTO.getProjectPassword() != null && !projectDTO.getProjectPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(projectDTO.getProjectPassword());
            project.setProjectPassword(encryptedPassword);
        }
        
        // Add initial members if provided
        if (projectDTO.getMemberIds() != null && !projectDTO.getMemberIds().isEmpty()) {
            List<User> members = new ArrayList<>();
            for (Long memberId : projectDTO.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", memberId));
                members.add(member);
            }
            project.setMembers(members);
        }
        
        return projectRepository.save(project);
    }
    
    /**
     * Update an existing project's information.
     * Only the project manager can update the project.
     * 
     * @param id the project ID
     * @param projectDTO the updated project data
     * @return the updated Project entity
     * @throws ResourceNotFoundException if project is not found
     * @throws ValidationException if project data is invalid
     */
    public Project updateProject(Long id, ProjectDTO projectDTO) {
        if (id == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        if (projectDTO == null) {
            throw new ValidationException("Project data cannot be null");
        }
        
        Project existingProject = getProjectById(id);
        
        // Update name if provided
        if (projectDTO.getName() != null && !projectDTO.getName().trim().isEmpty()) {
            existingProject.setName(projectDTO.getName());
        }
        
        // Update description (can be null or empty)
        if (projectDTO.getDescription() != null) {
            existingProject.setDescription(projectDTO.getDescription());
        }
        
        // Update project password if provided
        if (projectDTO.getProjectPassword() != null && !projectDTO.getProjectPassword().trim().isEmpty()) {
            String encryptedPassword = passwordEncoder.encode(projectDTO.getProjectPassword());
            existingProject.setProjectPassword(encryptedPassword);
        }
        
        return projectRepository.save(existingProject);
    }
    
    /**
     * Delete a project and all associated tasks and sprints (cascade deletion).
     * Only the project manager can delete the project.
     * 
     * @param id the project ID
     * @throws ResourceNotFoundException if project is not found
     */
    public void deleteProject(Long id) {
        if (id == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        Project project = getProjectById(id);
        
        // Cascade deletion is handled by JPA annotations (CascadeType.ALL, orphanRemoval = true)
        projectRepository.delete(project);
    }
    
    /**
     * Get a project by its ID.
     * 
     * @param id the project ID
     * @return the Project entity
     * @throws ResourceNotFoundException if project is not found
     */
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        if (id == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }
    
    /**
     * Get all projects accessible by a user (as manager or member).
     * 
     * @param userId the user ID
     * @return list of projects the user has access to
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public List<Project> getProjectsByUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Get projects where user is manager
        List<Project> managedProjects = projectRepository.findByManagerId(userId);
        
        // Get projects where user is a member
        List<Project> memberProjects = projectRepository.findByMembersContaining(user);
        
        // Combine both lists, avoiding duplicates
        List<Project> allProjects = new ArrayList<>(managedProjects);
        for (Project project : memberProjects) {
            if (!allProjects.contains(project)) {
                allProjects.add(project);
            }
        }
        
        return allProjects;
    }
    
    /**
     * Add a member to a project.
     * Only the project manager can add members.
     * 
     * @param projectId the project ID
     * @param userId the user ID to add as member
     * @throws ResourceNotFoundException if project or user is not found
     * @throws ValidationException if user is already a member
     */
    public void addMemberToProject(Long projectId, Long userId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        Project project = getProjectById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Check if user is already a member
        if (project.getMembers().contains(user)) {
            throw new ValidationException("User is already a member of this project");
        }
        
        // Add member
        project.getMembers().add(user);
        projectRepository.save(project);
    }
    
    /**
     * Remove a member from a project.
     * Only the project manager can remove members.
     * When a member is removed, all their task assignments in this project are cleared.
     * 
     * @param projectId the project ID
     * @param userId the user ID to remove
     * @throws ResourceNotFoundException if project or user is not found
     * @throws ValidationException if user is not a member or is the project manager
     */
    public void removeMemberFromProject(Long projectId, Long userId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        Project project = getProjectById(projectId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Cannot remove the project manager
        if (project.getManager().getId().equals(userId)) {
            throw new ValidationException("Cannot remove the project manager from the project");
        }
        
        // Check if user is a member
        if (!project.getMembers().contains(user)) {
            throw new ValidationException("User is not a member of this project");
        }
        
        // Remove member
        project.getMembers().remove(user);
        
        // Unassign all tasks assigned to this user in this project
        project.getTasks().forEach(task -> {
            if (task.getAssignee() != null && task.getAssignee().getId().equals(userId)) {
                task.setAssignee(null);
            }
        });
        
        projectRepository.save(project);
    }
    
    /**
     * Check if a user is a member of a project (including manager).
     * 
     * @param projectId the project ID
     * @param userId the user ID
     * @return true if user is a member or manager, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isMember(Long projectId, Long userId) {
        if (projectId == null || userId == null) {
            return false;
        }
        
        try {
            Project project = getProjectById(projectId);
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return false;
            }
            
            // Check if user is the manager
            if (project.getManager().getId().equals(userId)) {
                return true;
            }
            
            // Check if user is in members list
            return project.getMembers().contains(user);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Check if a user is the manager of a project.
     * 
     * @param projectId the project ID
     * @param userId the user ID
     * @return true if user is the project manager, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isManager(Long projectId, Long userId) {
        if (projectId == null || userId == null) {
            return false;
        }
        
        try {
            Project project = getProjectById(projectId);
            return project.getManager().getId().equals(userId);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
