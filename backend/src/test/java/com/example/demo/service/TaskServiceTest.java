package com.example.demo.service;

import com.example.demo.dto.TaskDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.ValidationException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private SprintRepository sprintRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private TaskService taskService;
    
    private Project testProject;
    private User testUser;
    private Sprint testSprint;
    private Task testTask;
    
    @BeforeEach
    void setUp() {
        // Manually inject NotificationService since it uses setter injection
        taskService.setNotificationService(notificationService);
        
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.MEMBER);
        
        // Setup test project
        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setManager(testUser);
        testProject.setMembers(Arrays.asList(testUser));
        
        // Setup test sprint
        testSprint = new Sprint();
        testSprint.setId(1L);
        testSprint.setName("Sprint 1");
        testSprint.setProject(testProject);
        testSprint.setStatus(SprintStatus.ACTIVE);
        
        // Setup test task
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setStatus(TaskStatus.TODO);
        testTask.setPriority(TaskPriority.HIGH);
        testTask.setProject(testProject);
        testTask.setSprint(testSprint);
        testTask.setAssignee(testUser);
    }
    
    @Test
    void createTask_WithValidData_ShouldCreateTaskWithTodoStatus() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("New Task");
        taskDTO.setDescription("New Description");
        taskDTO.setPriority(TaskPriority.MEDIUM);
        taskDTO.setProjectId(1L);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(2L);
            return task;
        });
        
        // Act
        Task result = taskService.createTask(taskDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals("New Task", result.getTitle());
        assertEquals(TaskPriority.MEDIUM, result.getPriority());
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void createTask_WithNullTitle_ShouldThrowValidationException() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle(null);
        taskDTO.setPriority(TaskPriority.HIGH);
        taskDTO.setProjectId(1L);
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> taskService.createTask(taskDTO));
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void updateTask_WithValidData_ShouldUpdateTask() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");
        taskDTO.setPriority(TaskPriority.LOW);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Task result = taskService.updateTask(1L, taskDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskPriority.LOW, result.getPriority());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    void deleteTask_WithValidId_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).delete(testTask);
        
        // Act
        taskService.deleteTask(1L);
        
        // Assert
        verify(taskRepository).delete(testTask);
    }
    
    @Test
    void getTaskById_WithValidId_ShouldReturnTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // Act
        Task result = taskService.getTaskById(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getTitle());
    }
    
    @Test
    void getTaskById_WithInvalidId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(999L));
    }
    
    @Test
    void updateTaskStatus_WithValidTransition_ShouldUpdateStatus() {
        // Arrange
        testTask.setStatus(TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Task result = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
        
        // Assert
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository).save(testTask);
    }
    
    @Test
    void updateTaskStatus_WithInvalidTransition_ShouldThrowValidationException() {
        // Arrange
        testTask.setStatus(TaskStatus.TODO);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        
        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> taskService.updateTaskStatus(1L, TaskStatus.DONE));
    }
    
    @Test
    void assignTask_WithValidMember_ShouldAssignAndNotify() {
        // Arrange
        testTask.setAssignee(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(notificationService).sendTaskAssignmentNotification(any(Task.class));
        
        // Act
        Task result = taskService.assignTask(1L, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getAssignee());
        verify(taskRepository).save(testTask);
        verify(notificationService).sendTaskAssignmentNotification(any(Task.class));
    }
    
    @Test
    void assignTask_WithNonMember_ShouldThrowValidationException() {
        // Arrange
        User nonMember = new User();
        nonMember.setId(2L);
        nonMember.setUsername("nonmember");
        
        testTask.setAssignee(null);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(nonMember));
        
        // Act & Assert
        assertThrows(ValidationException.class, () -> taskService.assignTask(1L, 2L));
        verify(taskRepository, never()).save(any(Task.class));
    }
    
    @Test
    void searchTasks_WithKeyword_ShouldReturnMatchingTasks() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(taskRepository.searchByKeyword("Test")).thenReturn(expectedTasks);
        
        // Act
        List<Task> result = taskService.searchTasks("Test");
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
    }
    
    @Test
    void filterTasks_WithStatus_ShouldReturnFilteredTasks() {
        // Arrange
        List<Task> allTasks = Arrays.asList(testTask);
        when(taskRepository.findAll()).thenReturn(allTasks);
        
        // Act
        List<Task> result = taskService.filterTasks(TaskStatus.TODO, null, null, null);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TaskStatus.TODO, result.get(0).getStatus());
    }
    
    @Test
    void getTasksByProject_WithValidProjectId_ShouldReturnTasks() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectId(1L)).thenReturn(expectedTasks);
        
        // Act
        List<Task> result = taskService.getTasksByProject(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject, result.get(0).getProject());
    }
    
    @Test
    void getTasksByAssignee_WithValidUserId_ShouldReturnTasks() {
        // Arrange
        List<Task> expectedTasks = Arrays.asList(testTask);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.findByAssigneeId(1L)).thenReturn(expectedTasks);
        
        // Act
        List<Task> result = taskService.getTasksByAssignee(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0).getAssignee());
    }
}
