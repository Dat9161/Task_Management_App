package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.Task;
import com.taskmanagement.domain.enums.Priority;
import com.taskmanagement.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Task entity
 * Provides custom queries for task search, filtering, and management
 */
@Repository
public interface ITaskRepository extends JpaRepository<Task, String> {
    
    /**
     * Find all tasks belonging to a specific project
     * @param projectId the project ID
     * @return list of tasks in the project
     */
    List<Task> findByProjectId(String projectId);
    
    /**
     * Find all tasks in a specific sprint
     * @param sprintId the sprint ID
     * @return list of tasks in the sprint
     */
    List<Task> findBySprintId(String sprintId);
    
    /**
     * Find all tasks in the project backlog (not assigned to any sprint)
     * @param projectId the project ID
     * @return list of backlog tasks
     */
    List<Task> findByProjectIdAndSprintIdIsNull(String projectId);
    
    /**
     * Find all tasks assigned to a specific user
     * @param assigneeId the assignee's user ID
     * @return list of tasks assigned to the user
     */
    List<Task> findByAssigneeId(String assigneeId);
    
    /**
     * Find all tasks with a specific status
     * @param status the task status to filter by
     * @return list of tasks with the specified status
     */
    List<Task> findByStatus(TaskStatus status);
    
    /**
     * Find all tasks with a specific priority
     * @param priority the priority to filter by
     * @return list of tasks with the specified priority
     */
    List<Task> findByPriority(Priority priority);
    
    /**
     * Find tasks by assignee and status
     * @param assigneeId the assignee's user ID
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<Task> findByAssigneeIdAndStatus(String assigneeId, TaskStatus status);
    
    /**
     * Find tasks by project and status
     * @param projectId the project ID
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<Task> findByProjectIdAndStatus(String projectId, TaskStatus status);
    
    /**
     * Find tasks by sprint and status
     * @param sprintId the sprint ID
     * @param status the task status
     * @return list of tasks matching the criteria
     */
    List<Task> findBySprintIdAndStatus(String sprintId, TaskStatus status);
    
    /**
     * Search tasks by title or description containing a keyword
     * Case-insensitive search
     * @param keyword the search keyword
     * @return list of tasks matching the search
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * Search tasks within a project by keyword
     * @param projectId the project ID
     * @param keyword the search keyword
     * @return list of tasks matching the search
     */
    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId " +
           "AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> searchByProjectAndKeyword(@Param("projectId") String projectId, 
                                         @Param("keyword") String keyword);
    
    /**
     * Find tasks with multiple filter criteria
     * All parameters are optional (can be null)
     * @param projectId the project ID (optional)
     * @param sprintId the sprint ID (optional)
     * @param assigneeId the assignee ID (optional)
     * @param status the task status (optional)
     * @param priority the priority (optional)
     * @return list of tasks matching all non-null criteria
     */
    @Query("SELECT t FROM Task t WHERE " +
           "(:projectId IS NULL OR t.projectId = :projectId) AND " +
           "(:sprintId IS NULL OR t.sprintId = :sprintId) AND " +
           "(:assigneeId IS NULL OR t.assigneeId = :assigneeId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority)")
    List<Task> findByMultipleFilters(@Param("projectId") String projectId,
                                      @Param("sprintId") String sprintId,
                                      @Param("assigneeId") String assigneeId,
                                      @Param("status") TaskStatus status,
                                      @Param("priority") Priority priority);
    
    /**
     * Find all blocked tasks in a sprint
     * @param sprintId the sprint ID
     * @return list of blocked tasks
     */
    default List<Task> findBlockedTasksBySprintId(String sprintId) {
        return findBySprintIdAndStatus(sprintId, TaskStatus.BLOCKED);
    }
    
    /**
     * Count tasks by status in a sprint
     * @param sprintId the sprint ID
     * @param status the task status
     * @return count of tasks with the specified status
     */
    long countBySprintIdAndStatus(String sprintId, TaskStatus status);
    
    /**
     * Count total tasks in a sprint
     * @param sprintId the sprint ID
     * @return total count of tasks
     */
    long countBySprintId(String sprintId);
}
