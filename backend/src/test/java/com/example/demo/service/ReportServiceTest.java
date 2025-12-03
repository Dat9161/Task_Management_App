package com.example.demo.service;

import com.example.demo.dto.ProgressMetrics;
import com.example.demo.dto.SprintReport;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.*;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SprintRepository;
import com.example.demo.repository.StoredSprintReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    
    @Mock
    private SprintRepository sprintRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private StoredSprintReportRepository storedSprintReportRepository;
    
    @InjectMocks
    private ReportService reportService;
    
    private Sprint testSprint;
    private Project testProject;
    private List<Task> testTasks;
    
    @BeforeEach
    void setUp() {
        // Create test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        
        // Create test sprint
        testSprint = new Sprint();
        testSprint.setId(1L);
        testSprint.setName("Sprint 1");
        testSprint.setProject(testProject);
        testSprint.setStartDate(new Date());
        testSprint.setEndDate(new Date());
        testSprint.setStatus(SprintStatus.COMPLETED);
        
        // Create test tasks
        testTasks = new ArrayList<>();
        
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.DONE);
        task1.setPriority(TaskPriority.HIGH);
        testTasks.add(task1);
        
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.DONE);
        task2.setPriority(TaskPriority.MEDIUM);
        testTasks.add(task2);
        
        Task task3 = new Task();
        task3.setId(3L);
        task3.setTitle("Task 3");
        task3.setStatus(TaskStatus.IN_PROGRESS);
        task3.setPriority(TaskPriority.LOW);
        testTasks.add(task3);
        
        Task task4 = new Task();
        task4.setId(4L);
        task4.setTitle("Task 4");
        task4.setStatus(TaskStatus.TODO);
        task4.setPriority(TaskPriority.CRITICAL);
        testTasks.add(task4);
        
        testSprint.setTasks(testTasks);
        testProject.setTasks(testTasks);
        testProject.setSprints(Arrays.asList(testSprint));
    }
    
    @Test
    void generateSprintReport_WithValidSprintId_ShouldReturnReport() {
        // Arrange
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        when(storedSprintReportRepository.save(any())).thenReturn(null);
        
        // Act
        SprintReport report = reportService.generateSprintReport(1L);
        
        // Assert
        assertNotNull(report);
        assertEquals("Sprint 1", report.getSprintName());
        assertEquals(2, report.getCompletedTasks().size());
        assertEquals(50.0, report.getTaskCompletionRate(), 0.01);
        assertEquals(2.0, report.getVelocity(), 0.01);
        verify(storedSprintReportRepository, times(1)).save(any());
    }
    
    @Test
    void generateSprintReport_WithNullSprintId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> reportService.generateSprintReport(null));
    }
    
    @Test
    void generateSprintReport_WithNonExistentSprint_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(sprintRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reportService.generateSprintReport(999L));
    }
    
    @Test
    void getSprintProgress_WithValidSprintId_ShouldReturnMetrics() {
        // Arrange
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        
        // Act
        ProgressMetrics metrics = reportService.getSprintProgress(1L);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(4, metrics.getTotalTasks());
        assertEquals(2, metrics.getCompletedTasks());
        assertEquals(50.0, metrics.getCompletionPercentage(), 0.01);
        assertEquals(2.0, metrics.getVelocity(), 0.01);
        
        Map<String, Integer> distribution = metrics.getStatusDistribution();
        assertEquals(1, distribution.get("TODO"));
        assertEquals(1, distribution.get("IN_PROGRESS"));
        assertEquals(2, distribution.get("DONE"));
        assertEquals(0, distribution.get("BLOCKED"));
    }
    
    @Test
    void getSprintProgress_WithNullSprintId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> reportService.getSprintProgress(null));
    }
    
    @Test
    void getProjectProgress_WithValidProjectId_ShouldReturnMetrics() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        
        // Act
        ProgressMetrics metrics = reportService.getProjectProgress(1L);
        
        // Assert
        assertNotNull(metrics);
        assertEquals(4, metrics.getTotalTasks());
        assertEquals(2, metrics.getCompletedTasks());
        assertEquals(50.0, metrics.getCompletionPercentage(), 0.01);
        assertEquals(2.0, metrics.getVelocity(), 0.01);
    }
    
    @Test
    void getProjectProgress_WithNullProjectId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> reportService.getProjectProgress(null));
    }
    
    @Test
    void getProjectProgress_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reportService.getProjectProgress(999L));
    }
    
    @Test
    void exportReportToPDF_WithValidReport_ShouldReturnPDFBytes() {
        // Arrange
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        when(storedSprintReportRepository.save(any())).thenReturn(null);
        SprintReport report = reportService.generateSprintReport(1L);
        
        // Act
        byte[] pdfBytes = reportService.exportReportToPDF(report);
        
        // Assert
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
        // Verify PDF header
        assertTrue(pdfBytes[0] == 0x25 && pdfBytes[1] == 0x50 && pdfBytes[2] == 0x44 && pdfBytes[3] == 0x46); // %PDF
    }
    
    @Test
    void exportReportToPDF_WithNullReport_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> reportService.exportReportToPDF(null));
    }
    
    @Test
    void getHistoricalReports_WithValidProjectId_ShouldReturnReports() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        
        StoredSprintReport storedReport1 = new StoredSprintReport();
        storedReport1.setId(1L);
        storedReport1.setSprintName("Sprint 1");
        
        StoredSprintReport storedReport2 = new StoredSprintReport();
        storedReport2.setId(2L);
        storedReport2.setSprintName("Sprint 2");
        
        List<StoredSprintReport> storedReports = Arrays.asList(storedReport1, storedReport2);
        when(storedSprintReportRepository.findByProjectId(1L)).thenReturn(storedReports);
        
        // Act
        List<StoredSprintReport> reports = reportService.getHistoricalReports(1L);
        
        // Assert
        assertNotNull(reports);
        assertEquals(2, reports.size());
        assertEquals("Sprint 1", reports.get(0).getSprintName());
        assertEquals("Sprint 2", reports.get(1).getSprintName());
    }
    
    @Test
    void getHistoricalReports_WithNullProjectId_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> reportService.getHistoricalReports(null));
    }
    
    @Test
    void getHistoricalReports_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reportService.getHistoricalReports(999L));
    }
}
