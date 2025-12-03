package com.example.demo.controller;

import com.example.demo.dto.DashboardDTO;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for dashboard operations.
 * Provides user dashboard with tasks, projects, sprints, and activities.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * Get dashboard data for a user.
     * 
     * @param userId User ID
     * @return Dashboard data including tasks, projects, sprints, and activities
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<DashboardDTO> getUserDashboard(@PathVariable Long userId) {
        DashboardDTO dashboard = dashboardService.getUserDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }
}
