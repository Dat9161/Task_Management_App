package com.example.demo.service;

import com.example.demo.dto.DashboardDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.*;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SprintRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing Dashboard operations.
 * Provides aggregated data for user dashboard display.
 */
@Service
@Transactional(readOnly = true)
public class DashboardService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public DashboardService(TaskRepository taskRepository,
                           ProjectRepository projectRepository,
                           SprintRepository sprintRepository,
                           UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.sprintRepository = sprintRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Get comprehensive dashboard data for a user.
     * Includes:
     * - Tasks grouped by status
     * - Upcoming deadlines and overdue tasks
     * - Active projects and current sprints
     * - Recent activity
     * 
     * @param userId the user ID
     * @return DashboardDTO containing all dashboard data
     * @throws ResourceNotFoundException if user is not found
     * @throws ValidationException if userId is null
     */
    public DashboardDTO getUserDashboard(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        // Get all tasks assigned to the user
        List<Task> assignedTasks = taskRepository.findByAssigneeId(userId);
        
        // Group tasks by status
        Map<String, List<Task>> tasksByStatus = groupTasksByStatus(assignedTasks);
        
        // Identify upcoming deadlines and overdue tasks
        Date now = new Date();
        List<Task> upcomingDeadlines = identifyUpcomingDeadlines(assignedTasks, now);
        List<Task> overdueTasks = identifyOverdueTasks(assignedTasks, now);
        
        // Get active projects (projects where user is manager or member)
        List<Project> activeProjects = getActiveProjects(userId);
        
        // Get current sprints from active projects
        List<Sprint> currentSprints = getCurrentSprints(activeProjects);
        
        // Get recent activity
        List<String> recentActivity = getRecentActivity(assignedTasks, activeProjects);
        
        return new DashboardDTO(
            tasksByStatus,
            upcomingDeadlines,
            overdueTasks,
            activeProjects,
            currentSprints,
            recentActivity
        );
    }
    
    /**
     * Group tasks by their status.
     * 
     * @param tasks list of tasks to group
     * @return map of status name to list of tasks
     */
    private Map<String, List<Task>> groupTasksByStatus(List<Task> tasks) {
        Map<String, List<Task>> grouped = new HashMap<>();
        
        // Initialize all status groups
        for (TaskStatus status : TaskStatus.values()) {
            grouped.put(status.name(), new ArrayList<>());
        }
        
        // Group tasks by status
        for (Task task : tasks) {
            TaskStatus status = task.getStatus();
            grouped.get(status.name()).add(task);
        }
        
        return grouped;
    }
    
    /**
     * Identify tasks with upcoming deadlines (due within 7 days).
     * 
     * @param tasks list of tasks to check
     * @param now current date
     * @return list of tasks with upcoming deadlines, sorted by due date
     */
    private List<Task> identifyUpcomingDeadlines(List<Task> tasks, Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date sevenDaysFromNow = calendar.getTime();
        
        return tasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getDueDate().after(now) && task.getDueDate().before(sevenDaysFromNow))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Identify overdue tasks (due date in the past and not done).
     * 
     * @param tasks list of tasks to check
     * @param now current date
     * @return list of overdue tasks, sorted by due date
     */
    private List<Task> identifyOverdueTasks(List<Task> tasks, Date now) {
        return tasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .filter(task -> task.getDueDate().before(now))
                .sorted(Comparator.comparing(Task::getDueDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Get all active projects for a user (as manager or member).
     * 
     * @param userId the user ID
     * @return list of active projects
     */
    private List<Project> getActiveProjects(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        
        // Get projects where user is manager
        List<Project> managedProjects = projectRepository.findByManagerId(userId);
        
        // Get projects where user is a member
        List<Project> memberProjects = projectRepository.findByMembersContaining(user);
        
        // Combine both lists, avoiding duplicates
        Set<Project> allProjects = new HashSet<>(managedProjects);
        allProjects.addAll(memberProjects);
        
        return new ArrayList<>(allProjects);
    }
    
    /**
     * Get current active sprints from a list of projects.
     * 
     * @param projects list of projects
     * @return list of active sprints
     */
    private List<Sprint> getCurrentSprints(List<Project> projects) {
        List<Sprint> currentSprints = new ArrayList<>();
        
        for (Project project : projects) {
            List<Sprint> activeSprints = sprintRepository.findByProjectIdAndStatus(
                project.getId(), 
                SprintStatus.ACTIVE
            );
            currentSprints.addAll(activeSprints);
        }
        
        return currentSprints;
    }
    
    /**
     * Get recent activity for the user.
     * Includes recent task creations, status changes, and assignments.
     * 
     * @param assignedTasks list of tasks assigned to the user
     * @param activeProjects list of active projects
     * @return list of recent activity messages
     */
    private List<String> getRecentActivity(List<Task> assignedTasks, List<Project> activeProjects) {
        List<String> activities = new ArrayList<>();
        
        // Sort tasks by updated timestamp (most recent first)
        List<Task> recentTasks = assignedTasks.stream()
                .filter(task -> task.getUpdatedAt() != null)
                .sorted(Comparator.comparing(Task::getUpdatedAt).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        // Generate activity messages for recent tasks
        for (Task task : recentTasks) {
            String activity = generateActivityMessage(task);
            if (activity != null) {
                activities.add(activity);
            }
        }
        
        // If no recent task activities, add project information
        if (activities.isEmpty() && !activeProjects.isEmpty()) {
            activities.add("You are a member of " + activeProjects.size() + " active project(s)");
        }
        
        return activities;
    }
    
    /**
     * Generate an activity message for a task.
     * 
     * @param task the task
     * @return activity message string
     */
    private String generateActivityMessage(Task task) {
        if (task.getUpdatedAt() == null) {
            return null;
        }
        
        String timeAgo = getTimeAgo(task.getUpdatedAt());
        
        // Check if task was recently created
        if (task.getCreatedAt() != null && 
            Math.abs(task.getUpdatedAt().getTime() - task.getCreatedAt().getTime()) < 1000) {
            return String.format("Task '%s' was created %s", task.getTitle(), timeAgo);
        }
        
        // Otherwise, it was updated
        return String.format("Task '%s' was updated to %s %s", 
            task.getTitle(), 
            task.getStatus().name(), 
            timeAgo);
    }
    
    /**
     * Convert a date to a human-readable "time ago" string.
     * 
     * @param date the date
     * @return time ago string (e.g., "2 hours ago", "3 days ago")
     */
    private String getTimeAgo(Date date) {
        long diffInMillis = new Date().getTime() - date.getTime();
        long diffInSeconds = diffInMillis / 1000;
        long diffInMinutes = diffInSeconds / 60;
        long diffInHours = diffInMinutes / 60;
        long diffInDays = diffInHours / 24;
        
        if (diffInDays > 0) {
            return diffInDays + " day" + (diffInDays > 1 ? "s" : "") + " ago";
        } else if (diffInHours > 0) {
            return diffInHours + " hour" + (diffInHours > 1 ? "s" : "") + " ago";
        } else if (diffInMinutes > 0) {
            return diffInMinutes + " minute" + (diffInMinutes > 1 ? "s" : "") + " ago";
        } else {
            return "just now";
        }
    }
}
