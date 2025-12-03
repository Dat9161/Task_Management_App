package com.example.demo.service;

import com.example.demo.dto.ProgressMetrics;
import com.example.demo.dto.SprintReport;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.Sprint;
import com.example.demo.model.Task;
import com.example.demo.model.TaskStatus;
import com.example.demo.model.StoredSprintReport;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SprintRepository;
import com.example.demo.repository.StoredSprintReportRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for generating reports and tracking progress.
 * Handles sprint reports, progress metrics, and velocity calculations.
 */
@Service
@Transactional
public class ReportService {
    
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final StoredSprintReportRepository storedSprintReportRepository;
    
    @Autowired
    public ReportService(SprintRepository sprintRepository, ProjectRepository projectRepository,
                        StoredSprintReportRepository storedSprintReportRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.storedSprintReportRepository = storedSprintReportRepository;
    }
    
    /**
     * Generate a comprehensive sprint report.
     * Includes sprint summary, completed tasks, and performance metrics.
     * 
     * @param sprintId the sprint ID
     * @return the generated SprintReport
     * @throws ResourceNotFoundException if sprint is not found
     */
    @Transactional(readOnly = true)
    public SprintReport generateSprintReport(Long sprintId) {
        if (sprintId == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));
        
        List<Task> allTasks = sprint.getTasks();
        List<Task> completedTasks = allTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .collect(Collectors.toList());
        
        // Calculate metrics
        double taskCompletionRate = allTasks.isEmpty() ? 0.0 : 
                (completedTasks.size() * 100.0) / allTasks.size();
        
        double velocity = completedTasks.size();
        
        // Generate progress metrics
        ProgressMetrics metrics = getSprintProgress(sprintId);
        
        // Generate burndown chart data (simplified - just completion percentage over time)
        List<Double> burndownChartData = new ArrayList<>();
        burndownChartData.add(taskCompletionRate);
        
        // Create sprint summary
        String sprintSummary = String.format(
                "Sprint '%s' completed with %d out of %d tasks finished (%.2f%% completion rate). " +
                "Team velocity: %.0f tasks completed.",
                sprint.getName(),
                completedTasks.size(),
                allTasks.size(),
                taskCompletionRate,
                velocity
        );
        
        SprintReport report = new SprintReport(
                sprint.getId(),
                sprint.getName(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprintSummary,
                completedTasks,
                taskCompletionRate,
                velocity,
                metrics,
                burndownChartData
        );
        
        // Store the report in the database for historical access
        storeSprintReport(sprint, report, metrics);
        
        return report;
    }
    
    /**
     * Get progress metrics for a sprint.
     * Includes task distribution by status.
     * 
     * @param sprintId the sprint ID
     * @return the ProgressMetrics for the sprint
     * @throws ResourceNotFoundException if sprint is not found
     */
    @Transactional(readOnly = true)
    public ProgressMetrics getSprintProgress(Long sprintId) {
        if (sprintId == null) {
            throw new ValidationException("Sprint ID cannot be null");
        }
        
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));
        
        List<Task> tasks = sprint.getTasks();
        
        long todoCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgressCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long blockedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();
        
        double completionPercentage = tasks.isEmpty() ? 0.0 : (doneCount * 100.0) / tasks.size();
        
        // Create status distribution map
        java.util.Map<String, Integer> statusDistribution = new java.util.HashMap<>();
        statusDistribution.put("TODO", (int) todoCount);
        statusDistribution.put("IN_PROGRESS", (int) inProgressCount);
        statusDistribution.put("DONE", (int) doneCount);
        statusDistribution.put("BLOCKED", (int) blockedCount);
        
        ProgressMetrics metrics = new ProgressMetrics();
        metrics.setTotalTasks(tasks.size());
        metrics.setCompletedTasks((int) doneCount);
        metrics.setStatusDistribution(statusDistribution);
        metrics.setCompletionPercentage(completionPercentage);
        metrics.setVelocity(doneCount);
        
        return metrics;
    }
    
    /**
     * Get progress metrics for a project.
     * Calculates completion percentage based on all tasks in the project.
     * 
     * @param projectId the project ID
     * @return the ProgressMetrics for the project
     * @throws ResourceNotFoundException if project is not found
     */
    @Transactional(readOnly = true)
    public ProgressMetrics getProjectProgress(Long projectId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        
        List<Task> tasks = project.getTasks();
        
        long todoCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgressCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long doneCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
        long blockedCount = tasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();
        
        double completionPercentage = tasks.isEmpty() ? 0.0 : (doneCount * 100.0) / tasks.size();
        
        // Create status distribution map
        java.util.Map<String, Integer> statusDistribution = new java.util.HashMap<>();
        statusDistribution.put("TODO", (int) todoCount);
        statusDistribution.put("IN_PROGRESS", (int) inProgressCount);
        statusDistribution.put("DONE", (int) doneCount);
        statusDistribution.put("BLOCKED", (int) blockedCount);
        
        // Calculate velocity as average completed tasks per sprint
        double velocity = calculateProjectVelocity(project);
        
        ProgressMetrics metrics = new ProgressMetrics();
        metrics.setTotalTasks(tasks.size());
        metrics.setCompletedTasks((int) doneCount);
        metrics.setStatusDistribution(statusDistribution);
        metrics.setCompletionPercentage(completionPercentage);
        metrics.setVelocity(velocity);
        
        return metrics;
    }
    
    /**
     * Calculate project velocity as average completed tasks per sprint.
     * 
     * @param project the project
     * @return the velocity metric
     */
    private double calculateProjectVelocity(Project project) {
        List<Sprint> sprints = project.getSprints();
        if (sprints == null || sprints.isEmpty()) {
            return 0.0;
        }
        
        long totalCompletedTasks = sprints.stream()
                .flatMap(sprint -> sprint.getTasks().stream())
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        
        return (double) totalCompletedTasks / sprints.size();
    }
    
    /**
     * Export a sprint report to PDF format.
     * 
     * @param sprintReport the sprint report to export
     * @return byte array containing the PDF document
     */
    public byte[] exportReportToPDF(SprintReport sprintReport) {
        if (sprintReport == null) {
            throw new ValidationException("Sprint report cannot be null");
        }
        
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Title
            document.add(new Paragraph("Sprint Report: " + sprintReport.getSprintName())
                    .setFontSize(20)
                    .setBold());
            
            // Sprint dates
            document.add(new Paragraph("Start Date: " + dateFormat.format(sprintReport.getStartDate())));
            document.add(new Paragraph("End Date: " + dateFormat.format(sprintReport.getEndDate())));
            document.add(new Paragraph("\n"));
            
            // Sprint summary
            document.add(new Paragraph("Summary")
                    .setFontSize(16)
                    .setBold());
            document.add(new Paragraph(sprintReport.getSprintSummary()));
            document.add(new Paragraph("\n"));
            
            // Metrics
            document.add(new Paragraph("Metrics")
                    .setFontSize(16)
                    .setBold());
            document.add(new Paragraph("Task Completion Rate: " + 
                    String.format("%.2f%%", sprintReport.getTaskCompletionRate())));
            document.add(new Paragraph("Velocity: " + 
                    String.format("%.0f tasks", sprintReport.getVelocity())));
            document.add(new Paragraph("\n"));
            
            // Status distribution
            ProgressMetrics metrics = sprintReport.getMetrics();
            if (metrics != null && metrics.getStatusDistribution() != null) {
                document.add(new Paragraph("Task Distribution")
                        .setFontSize(16)
                        .setBold());
                
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
                table.setWidth(UnitValue.createPercentValue(50));
                
                table.addCell("Status");
                table.addCell("Count");
                
                metrics.getStatusDistribution().forEach((status, count) -> {
                    table.addCell(status);
                    table.addCell(String.valueOf(count));
                });
                
                document.add(table);
                document.add(new Paragraph("\n"));
            }
            
            // Completed tasks
            document.add(new Paragraph("Completed Tasks")
                    .setFontSize(16)
                    .setBold());
            
            if (sprintReport.getCompletedTasks() != null && !sprintReport.getCompletedTasks().isEmpty()) {
                Table taskTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
                taskTable.setWidth(UnitValue.createPercentValue(100));
                
                taskTable.addCell("Task Title");
                taskTable.addCell("Priority");
                
                for (Task task : sprintReport.getCompletedTasks()) {
                    taskTable.addCell(task.getTitle());
                    taskTable.addCell(task.getPriority().toString());
                }
                
                document.add(taskTable);
            } else {
                document.add(new Paragraph("No tasks completed in this sprint."));
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new ValidationException("Failed to generate PDF report: " + e.getMessage());
        }
    }
    
    /**
     * Store a sprint report in the database for historical access.
     * 
     * @param sprint the sprint
     * @param report the sprint report
     * @param metrics the progress metrics
     */
    private void storeSprintReport(Sprint sprint, SprintReport report, ProgressMetrics metrics) {
        StoredSprintReport storedReport = new StoredSprintReport(
                sprint,
                report.getSprintName(),
                report.getStartDate(),
                report.getEndDate(),
                report.getSprintSummary(),
                report.getTaskCompletionRate(),
                report.getVelocity(),
                metrics.getTotalTasks(),
                metrics.getCompletedTasks()
        );
        
        storedSprintReportRepository.save(storedReport);
    }
    
    /**
     * Get all historical reports for a project.
     * Returns stored reports ordered by generation date (most recent first).
     * 
     * @param projectId the project ID
     * @return list of stored sprint reports
     * @throws ResourceNotFoundException if project is not found
     */
    @Transactional(readOnly = true)
    public List<StoredSprintReport> getHistoricalReports(Long projectId) {
        if (projectId == null) {
            throw new ValidationException("Project ID cannot be null");
        }
        
        // Verify project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
        
        return storedSprintReportRepository.findByProjectId(projectId);
    }
}
