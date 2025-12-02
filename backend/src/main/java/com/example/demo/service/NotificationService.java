package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Notification;
import com.example.demo.model.NotificationType;
import com.example.demo.model.Task;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Notification operations.
 * Handles notification creation, retrieval, and status updates.
 */
@Service
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    
    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                              UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Create a new notification for a user.
     * 
     * @param userId the user ID to send the notification to
     * @param message the notification message
     * @param type the notification type
     * @return the created Notification entity
     * @throws ResourceNotFoundException if user is not found
     * @throws ValidationException if input data is invalid
     */
    public Notification createNotification(Long userId, String message, NotificationType type) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        if (message == null || message.trim().isEmpty()) {
            throw new ValidationException("Notification message cannot be empty");
        }
        
        if (type == null) {
            throw new ValidationException("Notification type cannot be null");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        Notification notification = new Notification(user, message, type);
        return notificationRepository.save(notification);
    }
    
    /**
     * Get all unread notifications for a user.
     * 
     * @param userId the user ID
     * @return list of unread notifications with timestamp and description
     * @throws ResourceNotFoundException if user is not found
     * @throws ValidationException if user ID is null
     */
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Get all notifications for a user, ordered by creation date (newest first).
     * 
     * @param userId the user ID
     * @return list of all notifications for the user
     * @throws ResourceNotFoundException if user is not found
     * @throws ValidationException if user ID is null
     */
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * Mark a notification as read.
     * Updates the isRead status from false to true.
     * 
     * @param notificationId the notification ID
     * @throws ResourceNotFoundException if notification is not found
     * @throws ValidationException if notification ID is null
     */
    public void markAsRead(Long notificationId) {
        if (notificationId == null) {
            throw new ValidationException("Notification ID cannot be null");
        }
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    /**
     * Send a notification when a task is assigned to a user.
     * 
     * @param task the task that was assigned
     * @throws ValidationException if task or assignee is null
     */
    public void sendTaskAssignmentNotification(Task task) {
        if (task == null) {
            throw new ValidationException("Task cannot be null");
        }
        
        if (task.getAssignee() == null) {
            throw new ValidationException("Task must have an assignee");
        }
        
        String message = String.format("You have been assigned to task: %s", task.getTitle());
        createNotification(task.getAssignee().getId(), message, NotificationType.TASK_ASSIGNED);
    }
    
    /**
     * Send a notification when a task status is changed by another user.
     * 
     * @param task the task whose status changed
     * @param changedByUserId the ID of the user who changed the status
     * @throws ValidationException if task or assignee is null
     */
    public void sendStatusChangeNotification(Task task, Long changedByUserId) {
        if (task == null) {
            throw new ValidationException("Task cannot be null");
        }
        
        if (task.getAssignee() == null) {
            // No assignee, no notification needed
            return;
        }
        
        // Don't send notification if the assignee changed their own task status
        if (task.getAssignee().getId().equals(changedByUserId)) {
            return;
        }
        
        String message = String.format("Task '%s' status changed to %s", 
                                      task.getTitle(), task.getStatus());
        createNotification(task.getAssignee().getId(), message, NotificationType.STATUS_CHANGED);
    }
    
    /**
     * Send a deadline reminder notification for a task.
     * 
     * @param task the task with an approaching deadline
     * @throws ValidationException if task or assignee is null
     */
    public void sendDeadlineReminder(Task task) {
        if (task == null) {
            throw new ValidationException("Task cannot be null");
        }
        
        if (task.getAssignee() == null) {
            // No assignee, no notification needed
            return;
        }
        
        if (task.getDueDate() == null) {
            // No due date, no reminder needed
            return;
        }
        
        String message = String.format("Reminder: Task '%s' is due soon", task.getTitle());
        createNotification(task.getAssignee().getId(), message, NotificationType.DEADLINE_REMINDER);
    }
}
