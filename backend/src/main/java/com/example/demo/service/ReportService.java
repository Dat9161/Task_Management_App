package com.example.demo.service;

import com.example.demo.dto.ProgressMetrics;
import com.example.demo.dto.SprintReport;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Sprint;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for generating reports and tracking progress.
 * Handles sprint reports, progress metrics, and velocity calculations.
 */
@Service
@Transactional
public class ReportService {
    
    private final SprintRepository sprintRepository;
    
    @Autowired
    public ReportService(SprintRepository sprintRepository) {
        this.sprintRepository = sprintRepository;
    }
    
    /**
     * Generate a comprehensive sprint report.
     * Includes sprint summary, completed tasks, and performance metrics.
     * 
     * @param sprintId the sprint ID
     * @return the generated SprintReport
     * @throws ResourceNotFoundException if sprint is not found
     */
    @Transactional(readOnly = true)
    public SprintReport generateSprintReport(Long sprintId) {
        if (sprintId == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));
        
        List<Task> allTasks = sprint.getTasks();
        List<Task> completedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .collect(Collectors.toList());
        
        // Calculate metrics
        double taskCompletionRate = allTasks.isEmpty() ? 0.0 : 
                (completedTasks.size() * 100.0) / allTasks.size();
        
        double velocity = completedTasks.size();
        
        // Generate progress metrics
        ProgressMetrics metrics = getSprintProgress(sprintId);
        
        // Generate burndown chart data (simplified - just completion percentage over time)
        List<Double> burndownChartData = new ArrayList<>();
        burndownChartData.add(taskCompletionRate);
        
        // Create sprint summary
        String sprintSummary = String.format(
                "Sprint '%s' completed with %d out of %d tasks finished (%.2f%% completion rate). " +
                "Team velocity: %.0f tasks completed.",
                sprint.getName(),
                completedTasks.size(),
                allTasks.size(),
                taskCompletionRate,
                velocity
        );
        
        return new SprintReport(
                sprint.getId(),
                sprint.getName(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprintSummary,
                completedTasks,
                taskCompletionRate,
                velocity,
                metrics,
                burndownChartData
        );
    }
    
    /**
     * Get progress metrics for a sprint.
     * Includes task distribution by status.
     * 
     * @param sprintId the sprint ID
     * @return the ProgressMetrics for the sprint
     * @throws ResourceNotFoundException if sprint is not found
     */
    @Transactional(readOnly = true)
    public ProgressMetrics getSprintProgress(Long sprintId) {
        if (sprintId == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));
        
        List<Task> tasks = sprint.getTasks();
        
        long todoCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgressCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long blockedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();
        
        double completionPercentage = tasks.isEmpty() ? 0.0 : (doneCount * 100.0) / tasks.size();
        
        // Create status distribution map
        java.util.Map<String, Integer> statusDistribution = new java.util.HashMap<>();
        statusDistribution.put("TODO", (int) todoCount);
        statusDistribution.put("IN_PROGRESS", (int) inProgressCount);
        statusDistribution.put("DONE", (int) doneCount);
        statusDistribution.put("BLOCKED", (int) blockedCount);
        
        ProgressMetrics metrics = new ProgressMetrics();
        metrics.setTotalTasks(tasks.size());
        metrics.setCompletedTasks((int) doneCount);
        metrics.setStatusDistribution(statusDistribution);
        metrics.setCompletionPercentage(completionPercentage);
        metrics.setVelocity(doneCount);
        
        return metrics;
    }
}
