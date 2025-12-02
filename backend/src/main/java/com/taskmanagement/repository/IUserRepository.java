package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.User;
import com.taskmanagement.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides custom queries for user management and authentication
 */
@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    
    /**
     * Find a user by email address
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find a user by username
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find all users with a specific role
     * @param role the role to filter by
     * @return list of users with the specified role
     */
    List<User> findByRole(Role role);
    
    /**
     * Check if a user exists with the given email
     * @param email the email to check
     * @return true if user exists with this email
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * @param username the username to check
     * @return true if user exists with this username
     */
    boolean existsByUsername(String username);
}
