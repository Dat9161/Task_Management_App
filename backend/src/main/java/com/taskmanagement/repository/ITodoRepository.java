package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Todo entity
 * Provides custom queries for personal todo management
 */
@Repository
public interface ITodoRepository extends JpaRepository<Todo, String> {
    
    /**
     * Find all todos belonging to a specific user
     * Results are ordered by due date (nulls last) and creation date
     * @param userId the user ID
     * @return list of todos ordered by due date
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId " +
           "ORDER BY CASE WHEN t.dueDate IS NULL THEN 1 ELSE 0 END, t.dueDate ASC, t.createdAt DESC")
    List<Todo> findByUserIdOrderByDueDateAsc(@Param("userId") String userId);
    
    /**
     * Find all completed todos for a user
     * @param userId the user ID
     * @return list of completed todos
     */
    List<Todo> findByUserIdAndCompleted(String userId, boolean completed);
    
    /**
     * Find all incomplete todos for a user
     * @param userId the user ID
     * @return list of incomplete todos
     */
    default List<Todo> findIncompleteByUserId(String userId) {
        return findByUserIdAndCompleted(userId, false);
    }
    
    /**
     * Find all completed todos for a user
     * @param userId the user ID
     * @return list of completed todos
     */
    default List<Todo> findCompletedByUserId(String userId) {
        return findByUserIdAndCompleted(userId, true);
    }
    
    /**
     * Find overdue todos for a user
     * A todo is overdue if it's not completed and the due date is in the past
     * @param userId the user ID
     * @param currentDate the current date to compare against
     * @return list of overdue todos
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId " +
           "AND t.completed = false " +
           "AND t.dueDate IS NOT NULL " +
           "AND t.dueDate < :currentDate " +
           "ORDER BY t.dueDate ASC")
    List<Todo> findOverdueTodos(@Param("userId") String userId, 
                                @Param("currentDate") LocalDate currentDate);
    
    /**
     * Find todos due within a date range
     * @param userId the user ID
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of todos due within the range
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId " +
           "AND t.dueDate IS NOT NULL " +
           "AND t.dueDate >= :startDate " +
           "AND t.dueDate <= :endDate " +
           "ORDER BY t.dueDate ASC")
    List<Todo> findByUserIdAndDueDateBetween(@Param("userId") String userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
    
    /**
     * Find upcoming todos (due within the next N days)
     * @param userId the user ID
     * @param currentDate the current date
     * @param futureDate the future date (current + N days)
     * @return list of upcoming todos
     */
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId " +
           "AND t.completed = false " +
           "AND t.dueDate IS NOT NULL " +
           "AND t.dueDate >= :currentDate " +
           "AND t.dueDate <= :futureDate " +
           "ORDER BY t.dueDate ASC")
    List<Todo> findUpcomingTodos(@Param("userId") String userId,
                                 @Param("currentDate") LocalDate currentDate,
                                 @Param("futureDate") LocalDate futureDate);
    
    /**
     * Count incomplete todos for a user
     * @param userId the user ID
     * @return count of incomplete todos
     */
    long countByUserIdAndCompleted(String userId, boolean completed);
    
    /**
     * Count overdue todos for a user
     * @param userId the user ID
     * @param currentDate the current date
     * @return count of overdue todos
     */
    @Query("SELECT COUNT(t) FROM Todo t WHERE t.userId = :userId " +
           "AND t.completed = false " +
           "AND t.dueDate IS NOT NULL " +
           "AND t.dueDate < :currentDate")
    long countOverdueTodos(@Param("userId") String userId, 
                          @Param("currentDate") LocalDate currentDate);
}
