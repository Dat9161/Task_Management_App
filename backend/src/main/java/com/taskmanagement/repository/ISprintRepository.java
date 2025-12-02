package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.Sprint;
import com.taskmanagement.domain.enums.SprintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Sprint entity
 * Provides custom queries for sprint management with date filtering
 */
@Repository
public interface ISprintRepository extends JpaRepository<Sprint, String> {
    
    /**
     * Find all sprints belonging to a specific project
     * Results are ordered by start date (ascending)
     * @param projectId the project ID
     * @return list of sprints ordered by start date
     */
    List<Sprint> findByProjectIdOrderByStartDateAsc(String projectId);
    
    /**
     * Find all sprints with a specific status
     * @param status the sprint status to filter by
     * @return list of sprints with the specified status
     */
    List<Sprint> findByStatus(SprintStatus status);
    
    /**
     * Find all active sprints
     * @return list of active sprints
     */
    default List<Sprint> findActiveSprints() {
        return findByStatus(SprintStatus.ACTIVE);
    }
    
    /**
     * Find all sprints that have expired (end date is in the past)
     * but are not yet marked as completed
     * @param currentDate the current date to compare against
     * @return list of expired sprints
     */
    @Query("SELECT s FROM Sprint s WHERE s.endDate < :currentDate AND s.status != 'COMPLETED'")
    List<Sprint> findExpiredSprints(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Find sprints by project and status
     * @param projectId the project ID
     * @param status the sprint status
     * @return list of sprints matching the criteria
     */
    List<Sprint> findByProjectIdAndStatus(String projectId, SprintStatus status);
    
    /**
     * Find sprints within a date range
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of sprints within the date range
     */
    @Query("SELECT s FROM Sprint s WHERE s.startDate >= :startDate AND s.endDate <= :endDate")
    List<Sprint> findByDateRange(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);
    
    /**
     * Find sprints for a project within a date range
     * @param projectId the project ID
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @return list of sprints matching the criteria
     */
    @Query("SELECT s FROM Sprint s WHERE s.projectId = :projectId " +
           "AND s.startDate >= :startDate AND s.endDate <= :endDate " +
           "ORDER BY s.startDate ASC")
    List<Sprint> findByProjectIdAndDateRange(@Param("projectId") String projectId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}
