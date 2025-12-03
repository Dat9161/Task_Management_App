package com.example.demo.controller;

import com.example.demo.dto.ProgressMetrics;
import com.example.demo.dto.SprintReport;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for reporting operations.
 * Handles sprint reports, progress metrics, and report exports.
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    /**
     * Get sprint report.
     * 
     * @param sprintId Sprint ID
     * @return Sprint report
     */
    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<SprintReport> getSprintReport(@PathVariable Long sprintId) {
        SprintReport report = reportService.generateSprintReport(sprintId);
        return ResponseEntity.ok(report);
    }
    
    /**
     * Get project progress metrics.
     * 
     * @param projectId Project ID
     * @return Progress metrics
     */
    @GetMapping("/project/{projectId}/progress")
    public ResponseEntity<ProgressMetrics> getProjectProgress(@PathVariable Long projectId) {
        ProgressMetrics metrics = reportService.getProjectProgress(projectId);
        return ResponseEntity.ok(metrics);
    }
    
    /**
     * Export sprint report as PDF.
     * 
     * @param sprintId Sprint ID
     * @return PDF file
     */
    @GetMapping("/sprint/{sprintId}/export")
    public ResponseEntity<byte[]> exportSprintReport(@PathVariable Long sprintId) {
        // First generate the sprint report
        SprintReport sprintReport = reportService.generateSprintReport(sprintId);
        
        // Then export it to PDF
        byte[] pdfContent = reportService.exportReportToPDF(sprintReport);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "sprint-report-" + sprintId + ".pdf");
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
