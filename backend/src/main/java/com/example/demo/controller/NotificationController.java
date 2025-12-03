package com.example.demo.controller;

import com.example.demo.model.Notification;
import com.example.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for notification operations.
 * Handles retrieving and managing user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get all notifications for a user.
     * 
     * @param userId User ID
     * @return List of notifications
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Get unread notifications for a user.
     * 
     * @param userId User ID
     * @return List of unread notifications
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Mark a notification as read.
     * 
     * @param id Notification ID
     * @return Success message
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
}
