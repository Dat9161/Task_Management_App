package com.example.demo.controller;

import com.example.demo.dto.SprintDTO;
import com.example.demo.dto.SprintReport;
import com.example.demo.model.Sprint;
import com.example.demo.model.Task;
import com.example.demo.service.SprintService;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for sprint management operations.
 * Handles CRUD operations for sprints and sprint completion.
 */
@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SprintController {
    
    @Autowired
    private SprintService sprintService;
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Create a new sprint.
     * 
     * @param sprintDTO Sprint data
     * @return Created sprint
     */
    @PostMapping
    public ResponseEntity<Sprint> createSprint(@Valid @RequestBody SprintDTO sprintDTO) {
        Sprint sprint = sprintService.createSprint(sprintDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(sprint);
    }
    
    /**
     * Get sprint by ID.
     * 
     * @param id Sprint ID
     * @return Sprint details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable Long id) {
        Sprint sprint = sprintService.getSprintById(id);
        return ResponseEntity.ok(sprint);
    }
    
    /**
     * Update sprint information.
     * 
     * @param id Sprint ID
     * @param sprintDTO Updated sprint data
     * @return Updated sprint
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sprint> updateSprint(
            @PathVariable Long id,
            @Valid @RequestBody SprintDTO sprintDTO) {
        Sprint sprint = sprintService.updateSprint(id, sprintDTO);
        return ResponseEntity.ok(sprint);
    }
    
    /**
     * Get all sprints for a project.
     * 
     * @param projectId Project ID
     * @return List of sprints
     */
    @GetMapping("/projects/{projectId}/sprints")
    public ResponseEntity<List<Sprint>> getSprintsByProject(@PathVariable Long projectId) {
        List<Sprint> sprints = sprintService.getSprintsByProject(projectId);
        return ResponseEntity.ok(sprints);
    }
    
    /**
     * Complete a sprint and generate report.
     * 
     * @param id Sprint ID
     * @return Sprint report
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<SprintReport> completeSprint(@PathVariable Long id) {
        SprintReport report = sprintService.completeSprint(id);
        return ResponseEntity.ok(report);
    }
    
    /**
     * Get all tasks for a sprint.
     * 
     * @param id Sprint ID
     * @return List of tasks
     */
    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> getSprintTasks(@PathVariable Long id) {
        List<Task> tasks = taskService.getTasksBySprint(id);
        return ResponseEntity.ok(tasks);
    }
}
