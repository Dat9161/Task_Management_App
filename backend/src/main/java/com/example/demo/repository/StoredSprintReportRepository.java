package com.example.demo.repository;

import com.example.demo.model.StoredSprintReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoredSprintReportRepository extends JpaRepository<StoredSprintReport, Long> {
    
    /**
     * Find all stored reports for a specific sprint.
     * 
     * @param sprintId the sprint ID
     * @return list of stored reports for the sprint
     */
    List<StoredSprintReport> findBySprintId(Long sprintId);
    
    /**
     * Find all stored reports for sprints in a specific project.
     * 
     * @param projectId the project ID
     * @return list of stored reports for the project
     */
    @Query("SELECT r FROM StoredSprintReport r WHERE r.sprint.project.id = :projectId ORDER BY r.generatedAt DESC")
    List<StoredSprintReport> findByProjectId(@Param("projectId") Long projectId);
}
