package com.example.demo.controller;

import com.example.demo.dto.TaskDTO;
import com.example.demo.model.Task;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.TaskStatus;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for task management operations.
 * Handles CRUD operations for tasks, assignment, status updates, and search/filter.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Create a new task.
     * 
     * @param taskDTO Task data
     * @return Created task
     */
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        Task task = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
    
    /**
     * Get task by ID.
     * 
     * @param id Task ID
     * @return Task details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }
    
    /**
     * Update task information.
     * 
     * @param id Task ID
     * @param taskDTO Updated task data
     * @return Updated task
     */
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        Task task = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(task);
    }
    
    /**
     * Delete a task.
     * 
     * @param id Task ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all tasks for a project.
     * 
     * @param projectId Project ID
     * @return List of tasks
     */
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Get all tasks assigned to a user.
     * 
     * @param userId User ID
     * @return List of tasks
     */
    @GetMapping("/users/{userId}/tasks")
    public ResponseEntity<List<Task>> getTasksByAssignee(@PathVariable Long userId) {
        List<Task> tasks = taskService.getTasksByAssignee(userId);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * Assign a task to a user.
     * 
     * @param id Task ID
     * @param userId User ID to assign
     * @return Updated task
     */
    @PutMapping("/{id}/assign/{userId}")
    public ResponseEntity<Task> assignTask(
            @PathVariable Long id,
            @PathVariable Long userId) {
        Task task = taskService.assignTask(id, userId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * Update task status.
     * 
     * @param id Task ID
     * @param status New status
     * @return Updated task
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        Task task = taskService.updateTaskStatus(id, status);
        return ResponseEntity.ok(task);
    }
    
    /**
     * Search and filter tasks.
     * 
     * @param keyword Search keyword (optional)
     * @param status Filter by status (optional)
     * @param priority Filter by priority (optional)
     * @param assigneeId Filter by assignee (optional)
     * @param projectId Filter by project (optional)
     * @return List of matching tasks
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long projectId) {
        List<Task> tasks = taskService.searchAndFilterTasks(keyword, status, priority, assigneeId, projectId);
        return ResponseEntity.ok(tasks);
    }
}
