package com.example.demo.service;

import com.example.demo.dto.SprintDTO;
import com.example.demo.dto.SprintReport;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.Project;
import com.example.demo.model.Sprint;
import com.example.demo.model.SprintStatus;
import com.example.demo.model.User;
import com.example.demo.model.UserRole;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SprintServiceTest {
    
    @Mock
    private SprintRepository sprintRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private ReportService reportService;
    
    @InjectMocks
    private SprintService sprintService;
    
    private Project testProject;
    private User testUser;
    private Date startDate;
    private Date endDate;
    
    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "test@example.com", "password", UserRole.PROJECT_MANAGER);
        testUser.setId(1L);
        
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setManager(testUser);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        startDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 14);
        endDate = cal.getTime();
    }
    
    @Test
    void createSprint_WithValidData_ShouldCreateSprint() {
        // Arrange
        SprintDTO sprintDTO = new SprintDTO("Sprint 1", 1L, startDate, endDate);
        Sprint expectedSprint = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.PLANNED);
        expectedSprint.setId(1L);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(sprintRepository.findByProjectId(1L)).thenReturn(new ArrayList<>());
        when(sprintRepository.save(any(Sprint.class))).thenReturn(expectedSprint);
        
        // Act
        Sprint result = sprintService.createSprint(sprintDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Sprint 1", result.getName());
        assertEquals(SprintStatus.PLANNED, result.getStatus());
        verify(sprintRepository).save(any(Sprint.class));
    }
    
    @Test
    void createSprint_WithNullData_ShouldThrowValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> sprintService.createSprint(null));
    }
    
    @Test
    void createSprint_WithInvalidDates_ShouldThrowValidationException() {
        // Arrange - end date before start date
        SprintDTO sprintDTO = new SprintDTO("Sprint 1", 1L, endDate, startDate);
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> sprintService.createSprint(sprintDTO));
    }
    
    @Test
    void createSprint_WithOverlappingDates_ShouldThrowValidationException() {
        // Arrange
        Sprint existingSprint = new Sprint("Existing Sprint", testProject, startDate, endDate, SprintStatus.ACTIVE);
        existingSprint.setId(1L);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        Date overlappingStart = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date overlappingEnd = cal.getTime();
        
        SprintDTO sprintDTO = new SprintDTO("Sprint 2", 1L, overlappingStart, overlappingEnd);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(sprintRepository.findByProjectId(1L)).thenReturn(List.of(existingSprint));
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> sprintService.createSprint(sprintDTO));
    }
    
    @Test
    void updateSprint_WithValidData_ShouldUpdateSprint() {
        // Arrange
        Sprint existingSprint = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.PLANNED);
        existingSprint.setId(1L);
        
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date newStartDate = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 14);
        Date newEndDate = cal.getTime();
        
        SprintDTO updateDTO = new SprintDTO("Updated Sprint", 1L, newStartDate, newEndDate);
        
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(existingSprint));
        when(sprintRepository.findByProjectId(1L)).thenReturn(List.of(existingSprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(existingSprint);
        
        // Act
        Sprint result = sprintService.updateSprint(1L, updateDTO);
        
        // Assert
        assertNotNull(result);
        verify(sprintRepository).save(any(Sprint.class));
    }
    
    @Test
    void getSprintById_WithValidId_ShouldReturnSprint() {
        // Arrange
        Sprint sprint = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.PLANNED);
        sprint.setId(1L);
        
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        
        // Act
        Sprint result = sprintService.getSprintById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sprint 1", result.getName());
    }
    
    @Test
    void getSprintById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(sprintRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sprintService.getSprintById(999L));
    }
    
    @Test
    void getSprintsByProject_WithValidProjectId_ShouldReturnSprints() {
        // Arrange
        Sprint sprint1 = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.PLANNED);
        Sprint sprint2 = new Sprint("Sprint 2", testProject, startDate, endDate, SprintStatus.ACTIVE);
        List<Sprint> sprints = List.of(sprint1, sprint2);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(sprintRepository.findByProjectId(1L)).thenReturn(sprints);
        
        // Act
        List<Sprint> result = sprintService.getSprintsByProject(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
    
    @Test
    void completeSprint_WithValidId_ShouldCompleteSprintAndGenerateReport() {
        // Arrange
        Sprint sprint = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.ACTIVE);
        sprint.setId(1L);
        
        SprintReport expectedReport = new SprintReport();
        expectedReport.setSprintId(1L);
        expectedReport.setSprintName("Sprint 1");
        
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(sprint);
        when(reportService.generateSprintReport(1L)).thenReturn(expectedReport);
        
        // Act
        SprintReport result = sprintService.completeSprint(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getSprintId());
        assertEquals(SprintStatus.COMPLETED, sprint.getStatus());
        verify(sprintRepository).save(sprint);
        verify(reportService).generateSprintReport(1L);
    }
    
    @Test
    void completeSprint_WithAlreadyCompletedSprint_ShouldThrowValidationException() {
        // Arrange
        Sprint sprint = new Sprint("Sprint 1", testProject, startDate, endDate, SprintStatus.COMPLETED);
        sprint.setId(1L);
        
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(sprint));
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> sprintService.completeSprint(1L));
    }
}
