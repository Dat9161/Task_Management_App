package com.taskmanagement.repository;

import com.taskmanagement.domain.entity.Notification;
import com.taskmanagement.domain.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity
 * Provides custom queries for notification management
 */
@Repository
public interface INotificationRepository extends JpaRepository<Notification, String> {
    
    /**
     * Find all notifications for a specific user
     * Results are ordered by creation date (most recent first)
     * @param userId the user ID
     * @return list of notifications ordered by creation date descending
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    
    /**
     * Find unread notifications for a user
     * Results are ordered by creation date (most recent first)
     * @param userId the user ID
     * @return list of unread notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.read = false " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotifications(@Param("userId") String userId);
    
    /**
     * Find read notifications for a user
     * @param userId the user ID
     * @return list of read notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.read = true " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findReadNotifications(@Param("userId") String userId);
    
    /**
     * Find notifications by user and type
     * @param userId the user ID
     * @param type the notification type
     * @return list of notifications of the specified type
     */
    List<Notification> findByUserIdAndType(String userId, NotificationType type);
    
    /**
     * Find notifications created after a specific date
     * @param userId the user ID
     * @param afterDate the date to filter from
     * @return list of notifications created after the date
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.createdAt > :afterDate " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndCreatedAtAfter(@Param("userId") String userId,
                                                      @Param("afterDate") LocalDateTime afterDate);
    
    /**
     * Find recent notifications (within the last N days)
     * @param userId the user ID
     * @param sinceDate the date to filter from
     * @return list of recent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
           "AND n.createdAt >= :sinceDate " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") String userId,
                                               @Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Count unread notifications for a user
     * @param userId the user ID
     * @return count of unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId AND n.read = false")
    long countUnreadNotifications(@Param("userId") String userId);
    
    /**
     * Count notifications by type for a user
     * @param userId the user ID
     * @param type the notification type
     * @return count of notifications of the specified type
     */
    long countByUserIdAndType(String userId, NotificationType type);
    
    /**
     * Delete old read notifications (for cleanup)
     * @param beforeDate delete notifications created before this date
     * @param read only delete if read is true
     */
    @Query("DELETE FROM Notification n WHERE n.createdAt < :beforeDate AND n.read = :read")
    void deleteOldNotifications(@Param("beforeDate") LocalDateTime beforeDate, 
                                @Param("read") boolean read);
    
    /**
     * Mark all notifications as read for a user
     * @param userId the user ID
     */
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsRead(@Param("userId") String userId);
}
