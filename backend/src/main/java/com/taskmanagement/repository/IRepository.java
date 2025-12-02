package com.taskmanagement.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface providing basic CRUD operations
 * @param <T> the entity type
 */
public interface IRepository<T> {
    
    /**
     * Save an entity to the database
     * @param entity the entity to save
     * @return the saved entity
     */
    T save(T entity);
    
    /**
     * Find an entity by its ID
     * @param id the entity ID
     * @return Optional containing the entity if found, empty otherwise
     */
    Optional<T> findById(String id);
    
    /**
     * Find all entities
     * @return list of all entities
     */
    List<T> findAll();
    
    /**
     * Delete an entity by its ID
     * @param id the entity ID to delete
     */
    void delete(String id);
    
    /**
     * Update an existing entity
     * @param entity the entity to update
     * @return the updated entity
     */
    T update(T entity);
    
    /**
     * Check if an entity exists by ID
     * @param id the entity ID
     * @return true if entity exists, false otherwise
     */
    boolean existsById(String id);
}
