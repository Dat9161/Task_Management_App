package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Project entity
 * Provides custom queries for project access control and management
 */
@Repository
public interface IProjectRepository extends JpaRepository<Project, String> {
    
    /**
     * Find all projects created by a specific user
     * @param creatorId the creator's user ID
     * @return list of projects created by the user
     */
    List<Project> findByCreatorId(String creatorId);
    
    /**
     * Find all projects where a user is a member
     * Uses a custom query to search in the memberIds collection
     * @param userId the user ID to search for
     * @return list of projects where the user is a member
     */
    @Query("SELECT p FROM Project p WHERE :userId MEMBER OF p.memberIds")
    List<Project> findByMemberId(@Param("userId") String userId);
    
    /**
     * Find all projects accessible by a user (either as creator or member)
     * @param userId the user ID
     * @return list of projects accessible by the user
     */
    @Query("SELECT p FROM Project p WHERE p.creatorId = :userId OR :userId MEMBER OF p.memberIds")
    List<Project> findByUserAccess(@Param("userId") String userId);
    
    /**
     * Check if a user has access to a project (either as creator or member)
     * @param projectId the project ID
     * @param userId the user ID
     * @return true if user has access to the project
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Project p " +
           "WHERE p.id = :projectId AND (p.creatorId = :userId OR :userId MEMBER OF p.memberIds)")
    boolean hasUserAccess(@Param("projectId") String projectId, @Param("userId") String userId);
}
