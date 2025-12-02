package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Task operations.
 * Handles task creation, updates, deletion, assignment, and search/filter functionality.
 */
@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    private NotificationService notificationService;
    
    @Autowired
    public TaskService(TaskRepository taskRepository, 
                      ProjectRepository projectRepository,
                      SprintRepository sprintRepository,
                      UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Set the NotificationService (used to avoid circular dependency).
     * This is called by Spring after construction.
     * 
     * @param notificationService the notification service
     */
    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * Create a new task with initial TODO status.
     * 
     * @param taskDTO the task data transfer object
     * @return the created Task entity
     * @throws ValidationException if task data is invalid
     * @throws ResourceNotFoundException if project, sprint, or assignee is not found
     */
    public Task createTask(TaskDTO taskDTO) {
        // Validate input
        if (taskDTO == null) {
            throw new ValidationException("Task data cannot be null");
        }
        
        if (taskDTO.getTitle() == null || taskDTO.getTitle().trim().isEmpty()) {
            throw new ValidationException("Task title is required");
        }
        
        if (taskDTO.getProjectId() == null) {
            throw new ValidationException("Project ID is required");
        }
        
        if (taskDTO.getPriority() == null) {
            throw new ValidationException("Task priority is required");
        }
        
        // Get project
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", taskDTO.getProjectId()));
        
        // Get sprint if provided
        Sprint sprint = null;
        if (taskDTO.getSprintId() != null) {
            sprint = sprintRepository.findById(taskDTO.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint", taskDTO.getSprintId()));
            
            // Validate sprint belongs to the same project
            if (!sprint.getProject().getId().equals(project.getId())) {
                throw new ValidationException("Sprint does not belong to the specified project");
            }
        }
        
        // Get assignee if provided
        User assignee = null;
        if (taskDTO.getAssigneeId() != null) {
            assignee = userRepository.findById(taskDTO.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", taskDTO.getAssigneeId()));
        }
        
        // Create new task with TODO status
        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(TaskStatus.TODO); // Always set to TODO initially
        task.setPriority(taskDTO.getPriority());
        task.setProject(project);
        task.setSprint(sprint);
        task.setAssignee(assignee);
        task.setDueDate(taskDTO.getDueDate());
        
        return taskRepository.save(task);
    }
    
    /**
     * Update an existing task's information and update timestamp.
     * 
     * @param id the task ID
     * @param taskDTO the updated task data
     * @return the updated Task entity
     * @throws ResourceNotFoundException if task is not found
     * @throws ValidationException if task data is invalid
     */
    public Task updateTask(Long id, TaskDTO taskDTO) {
        if (id == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        if (taskDTO == null) {
            throw new ValidationException("Task data cannot be null");
        }
        
        Task existingTask = getTaskById(id);
        
        // Update title if provided
        if (taskDTO.getTitle() != null && !taskDTO.getTitle().trim().isEmpty()) {
            existingTask.setTitle(taskDTO.getTitle());
        }
        
        // Update description
        if (taskDTO.getDescription() != null) {
            existingTask.setDescription(taskDTO.getDescription());
        }
        
        // Update priority if provided
        if (taskDTO.getPriority() != null) {
            existingTask.setPriority(taskDTO.getPriority());
        }
        
        // Update due date
        if (taskDTO.getDueDate() != null) {
            existingTask.setDueDate(taskDTO.getDueDate());
        }
        
        // Update sprint if provided
        if (taskDTO.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(taskDTO.getSprintId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sprint", taskDTO.getSprintId()));
            
            // Validate sprint belongs to the same project
            if (!sprint.getProject().getId().equals(existingTask.getProject().getId())) {
                throw new ValidationException("Sprint does not belong to the task's project");
            }
            
            existingTask.setSprint(sprint);
        }
        
        // The @PreUpdate annotation will automatically update the updatedAt timestamp
        return taskRepository.save(existingTask);
    }
    
    /**
     * Delete a task.
     * 
     * @param id the task ID
     * @throws ResourceNotFoundException if task is not found
     */
    public void deleteTask(Long id) {
        if (id == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
    
    /**
     * Get a task by its ID.
     * 
     * @param id the task ID
     * @return the Task entity with complete data
     * @throws ResourceNotFoundException if task is not found
     */
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        if (id == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
    }
    
    /**
     * Get all tasks for a specific project.
     * 
     * @param projectId the project ID
     * @return list of tasks in the project
     * @throws ResourceNotFoundException if project is not found
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByProject(Long projectId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        // Verify project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        
        return taskRepository.findByProjectId(projectId);
    }
    
    /**
     * Get all tasks assigned to a specific user.
     * 
     * @param assigneeId the user ID
     * @return list of tasks assigned to the user
     * @throws ResourceNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByAssignee(Long assigneeId) {
        if (assigneeId == null) {
            throw new ValidationException("Assignee ID cannot be null");
        }
        
        // Verify user exists
        userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", assigneeId));
        
        return taskRepository.findByAssigneeId(assigneeId);
    }
    
    /**
     * Update the status of a task.
     * Validates status transitions and persists the change.
     * Sends notification to assignee if status was changed by another user.
     * 
     * @param taskId the task ID
     * @param newStatus the new status
     * @param changedByUserId the ID of the user making the change (optional, for notification)
     * @return the updated Task entity
     * @throws ResourceNotFoundException if task is not found
     * @throws ValidationException if status transition is invalid
     */
    public Task updateTaskStatus(Long taskId, TaskStatus newStatus, Long changedByUserId) {
        if (taskId == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        if (newStatus == null) {
            throw new ValidationException("Task status cannot be null");
        }
        
        Task task = getTaskById(taskId);
        TaskStatus currentStatus = task.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new ValidationException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus)
            );
        }
        
        // Update status
        task.setStatus(newStatus);
        
        // The @PreUpdate annotation will automatically update the updatedAt timestamp
        Task savedTask = taskRepository.save(task);
        
        // Send notification if status changed by another user
        if (notificationService != null && changedByUserId != null) {
            notificationService.sendStatusChangeNotification(savedTask, changedByUserId);
        }
        
        return savedTask;
    }
    
    /**
     * Update the status of a task (without notification).
     * Validates status transitions and persists the change.
     * 
     * @param taskId the task ID
     * @param newStatus the new status
     * @return the updated Task entity
     * @throws ResourceNotFoundException if task is not found
     * @throws ValidationException if status transition is invalid
     */
    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) {
        return updateTaskStatus(taskId, newStatus, null);
    }
    
    /**
     * Validate if a status transition is allowed.
     * 
     * Valid transitions:
     * - TODO -> IN_PROGRESS, BLOCKED
     * - IN_PROGRESS -> DONE, BLOCKED, TODO
     * - DONE -> TODO (reopening)
     * - BLOCKED -> TODO, IN_PROGRESS
     * 
     * @param currentStatus the current status
     * @param newStatus the new status
     * @return true if transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        // Allow staying in the same status
        if (currentStatus == newStatus) {
            return true;
        }
        
        switch (currentStatus) {
            case TODO:
                return newStatus == TaskStatus.IN_PROGRESS || newStatus == TaskStatus.BLOCKED;
            
            case IN_PROGRESS:
                return newStatus == TaskStatus.DONE || 
                       newStatus == TaskStatus.BLOCKED || 
                       newStatus == TaskStatus.TODO;
            
            case DONE:
                return newStatus == TaskStatus.TODO; // Allow reopening
            
            case BLOCKED:
                return newStatus == TaskStatus.TODO || newStatus == TaskStatus.IN_PROGRESS;
            
            default:
                return false;
        }
    }
    
    /**
     * Assign a task to a user.
     * Validates that the user is a member of the task's project.
     * Sends a notification to the assignee.
     * 
     * @param taskId the task ID
     * @param userId the user ID to assign the task to
     * @return the updated Task entity
     * @throws ResourceNotFoundException if task or user is not found
     * @throws ValidationException if user is not a member of the project
     */
    public Task assignTask(Long taskId, Long userId) {
        if (taskId == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        Task task = getTaskById(taskId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Validate that user is a member of the project
        Project project = task.getProject();
        boolean isMember = project.getManager().getId().equals(userId) || 
                          project.getMembers().contains(user);
        
        if (!isMember) {
            throw new ValidationException("User is not a member of the task's project");
        }
        
        // Assign task
        task.setAssignee(user);
        Task savedTask = taskRepository.save(task);
        
        // Send notification using NotificationService
        if (notificationService != null) {
            notificationService.sendTaskAssignmentNotification(savedTask);
        }
        
        return savedTask;
    }
    
    /**
     * Reassign a task to a different user.
     * Validates that the new user is a member of the task's project.
     * Sends notifications to both old and new assignees.
     * 
     * @param taskId the task ID
     * @param newUserId the new user ID to assign the task to
     * @return the updated Task entity
     * @throws ResourceNotFoundException if task or user is not found
     * @throws ValidationException if new user is not a member of the project
     */
    public Task reassignTask(Long taskId, Long newUserId) {
        if (taskId == null) {
            throw new ValidationException("Task ID cannot be null");
        }
        
        if (newUserId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        Task task = getTaskById(taskId);
        User oldAssignee = task.getAssignee();
        
        User newAssignee = userRepository.findById(newUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", newUserId));
        
        // Validate that new user is a member of the project
        Project project = task.getProject();
        boolean isMember = project.getManager().getId().equals(newUserId) || 
                          project.getMembers().contains(newAssignee);
        
        if (!isMember) {
            throw new ValidationException("User is not a member of the task's project");
        }
        
        // Reassign task
        task.setAssignee(newAssignee);
        Task savedTask = taskRepository.save(task);
        
        // Send notification to new assignee using NotificationService
        if (notificationService != null) {
            notificationService.sendTaskAssignmentNotification(savedTask);
            
            // Send notification to old assignee if there was one
            if (oldAssignee != null) {
                String message = String.format("Task '%s' has been reassigned to %s", 
                                              savedTask.getTitle(), newAssignee.getUsername());
                notificationService.createNotification(oldAssignee.getId(), message, 
                                                      com.example.demo.model.NotificationType.STATUS_CHANGED);
            }
        }
        
        return savedTask;
    }
    

    /**
     * Search tasks by keyword in title or description.
     * 
     * @param keyword the search keyword
     * @return list of tasks matching the keyword
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        return taskRepository.searchByKeyword(keyword.trim());
    }
    
    /**
     * Filter tasks by multiple criteria using AND logic.
     * All non-null criteria must be satisfied.
     * 
     * @param status the task status filter (optional)
     * @param priority the task priority filter (optional)
     * @param assigneeId the assignee ID filter (optional)
     * @param projectId the project ID filter (optional)
     * @return list of tasks matching all specified criteria
     */
    @Transactional(readOnly = true)
    public List<Task> filterTasks(TaskStatus status, TaskPriority priority, 
                                  Long assigneeId, Long projectId) {
        // Start with all tasks or project-specific tasks
        List<Task> tasks;
        if (projectId != null) {
            tasks = taskRepository.findByProjectId(projectId);
        } else {
            tasks = taskRepository.findAll();
        }
        
        // Apply filters using AND logic
        if (status != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (priority != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (assigneeId != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getAssignee() != null && 
                                   task.getAssignee().getId().equals(assigneeId))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return tasks;
    }
    
    /**
     * Search and filter tasks by keyword and multiple criteria.
     * Combines keyword search with filtering using AND logic.
     * 
     * @param keyword the search keyword (optional)
     * @param status the task status filter (optional)
     * @param priority the task priority filter (optional)
     * @param assigneeId the assignee ID filter (optional)
     * @param projectId the project ID filter (optional)
     * @return list of tasks matching the keyword and all specified criteria
     */
    @Transactional(readOnly = true)
    public List<Task> searchAndFilterTasks(String keyword, TaskStatus status, 
                                          TaskPriority priority, Long assigneeId, 
                                          Long projectId) {
        List<Task> tasks;
        
        // Start with keyword search if provided, otherwise use filter
        if (keyword != null && !keyword.trim().isEmpty()) {
            tasks = searchTasks(keyword);
            
            // Apply additional filters to search results
            if (projectId != null) {
                Long finalProjectId = projectId;
                tasks = tasks.stream()
                        .filter(task -> task.getProject().getId().equals(finalProjectId))
                        .collect(java.util.stream.Collectors.toList());
            }
        } else {
            // No keyword, just use filters
            tasks = filterTasks(status, priority, assigneeId, projectId);
            return tasks;
        }
        
        // Apply remaining filters
        if (status != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getStatus() == status)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (priority != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getPriority() == priority)
                    .collect(java.util.stream.Collectors.toList());
        }
        
        if (assigneeId != null) {
            tasks = tasks.stream()
                    .filter(task -> task.getAssignee() != null && 
                                   task.getAssignee().getId().equals(assigneeId))
                    .collect(java.util.stream.Collectors.toList());
        }
        
        return tasks;
    }
}
