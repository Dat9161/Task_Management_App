# Design Document - Task & Project Management Tool

## Overview

Task & Project Management Tool là một ứng dụng full-stack được xây dựng theo kiến trúc client-server với backend REST API sử dụng Java Spring Boot và frontend web application sử dụng React. Hệ thống áp dụng các design patterns và nguyên lý OOP để đảm bảo tính mở rộng, bảo trì và kiểm thử.

### Technology Stack

**Backend:**
- Java 17+
- Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security)
- MySQL 8.0
- Maven

**Frontend:**
- React 18+
- React Router (Navigation)
- Axios (HTTP client)
- Material-UI hoặc Ant Design (UI components)

**Development Tools:**
- IntelliJ IDEA (Backend development)
- VS Code (Frontend development)
- Postman (API testing)
- Docker (Containerization)
- GitHub (Version control)

## Architecture

### System Architecture

Hệ thống áp dụng kiến trúc **3-tier layered architecture**:

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│     (React Web Application)         │
└──────────────┬──────────────────────┘
               │ HTTP/REST API
┌──────────────▼──────────────────────┐
│     Application Layer               │
│     (Spring Boot Backend)           │
│  ┌────────────────────────────────┐ │
│  │  Controllers (REST Endpoints)  │ │
│  └────────────┬───────────────────┘ │
│  ┌────────────▼───────────────────┐ │
│  │  Services (Business Logic)     │ │
│  └────────────┬───────────────────┘ │
│  ┌────────────▼───────────────────┐ │
│  │  Repositories (Data Access)    │ │
│  └────────────┬───────────────────┘ │
└───────────────┼─────────────────────┘
                │ JPA/Hibernate
┌───────────────▼─────────────────────┐
│     Data Layer                      │
│     (MySQL Database)                │
└─────────────────────────────────────┘
```

### Design Patterns Applied

1. **MVC (Model-View-Controller)**: Tách biệt logic nghiệp vụ, dữ liệu và giao diện
2. **Repository Pattern**: Trừu tượng hóa data access layer
3. **Service Layer Pattern**: Đóng gói business logic
4. **DTO (Data Transfer Object)**: Truyền dữ liệu giữa layers
5. **Dependency Injection**: Spring IoC container quản lý dependencies
6. **Builder Pattern**: Xây dựng objects phức tạp (cho entities)
7. **Strategy Pattern**: Xử lý các loại notifications khác nhau

## Components and Interfaces

### Backend Components

#### 1. Domain Models (Entities)

**User Entity**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password; // Encrypted
    
    @Enumerated(EnumType.STRING)
    private UserRole role; // MEMBER, PROJECT_MANAGER
    
    @OneToMany(mappedBy = "assignee")
    private List<Task> assignedTasks;
    
    @ManyToMany(mappedBy = "members")
    private List<Project> projects;
}
```

**Project Entity**
```java
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    
    @ManyToMany
    @JoinTable(
        name = "project_members",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> members;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Sprint> sprints;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Task> tasks;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
```

**Sprint Entity**
```java
@Entity
@Table(name = "sprints")
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    @Temporal(TemporalType.DATE)
    private Date startDate;
    
    @Temporal(TemporalType.DATE)
    private Date endDate;
    
    @Enumerated(EnumType.STRING)
    private SprintStatus status; // PLANNED, ACTIVE, COMPLETED
    
    @OneToMany(mappedBy = "sprint")
    private List<Task> tasks;
}
```

**Task Entity**
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status; // TODO, IN_PROGRESS, DONE, BLOCKED
    
    @Enumerated(EnumType.STRING)
    private TaskPriority priority; // LOW, MEDIUM, HIGH, CRITICAL
    
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    
    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;
    
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;
    
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
```

**Notification Entity**
```java
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(nullable = false)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type; // TASK_ASSIGNED, DEADLINE_REMINDER, STATUS_CHANGED
    
    private boolean isRead;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
}
```

#### 2. Repository Interfaces

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByManagerId(Long managerId);
    List<Project> findByMembersContaining(User user);
}

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    List<Sprint> findByProjectId(Long projectId);
    List<Sprint> findByProjectIdAndStatus(Long projectId, SprintStatus status);
}

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findBySprintId(Long sprintId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByAssigneeIdAndStatus(Long assigneeId, TaskStatus status);
}

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
```

#### 3. Service Layer

**UserService**
- registerUser(UserDTO): User
- authenticateUser(String username, String password): AuthToken
- getUserById(Long id): User
- updateUser(Long id, UserDTO): User

**ProjectService**
- createProject(ProjectDTO): Project
- updateProject(Long id, ProjectDTO): Project
- deleteProject(Long id): void
- getProjectById(Long id): Project
- getProjectsByUser(Long userId): List<Project>
- addMemberToProject(Long projectId, Long userId): void
- removeMemberFromProject(Long projectId, Long userId): void

**SprintService**
- createSprint(SprintDTO): Sprint
- updateSprint(Long id, SprintDTO): Sprint
- getSprintById(Long id): Sprint
- getSprintsByProject(Long projectId): List<Sprint>
- completeSprint(Long id): SprintReport

**TaskService**
- createTask(TaskDTO): Task
- updateTask(Long id, TaskDTO): Task
- deleteTask(Long id): void
- getTaskById(Long id): Task
- getTasksByProject(Long projectId): List<Task>
- getTasksByAssignee(Long assigneeId): List<Task>
- assignTask(Long taskId, Long userId): Task
- updateTaskStatus(Long taskId, TaskStatus status): Task
- searchTasks(SearchCriteria): List<Task>

**NotificationService**
- createNotification(NotificationDTO): Notification
- getUnreadNotifications(Long userId): List<Notification>
- markAsRead(Long notificationId): void
- sendTaskAssignmentNotification(Task task): void
- sendDeadlineReminder(Task task): void

**ReportService**
- generateSprintReport(Long sprintId): SprintReport
- getProjectProgress(Long projectId): ProgressMetrics
- getSprintProgress(Long sprintId): ProgressMetrics
- exportReportToPDF(SprintReport): byte[]

#### 4. REST Controllers

**AuthController**
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/logout

**ProjectController**
- POST /api/projects
- GET /api/projects/{id}
- PUT /api/projects/{id}
- DELETE /api/projects/{id}
- GET /api/projects/user/{userId}
- POST /api/projects/{id}/members/{userId}
- DELETE /api/projects/{id}/members/{userId}

**SprintController**
- POST /api/sprints
- GET /api/sprints/{id}
- PUT /api/sprints/{id}
- GET /api/projects/{projectId}/sprints
- POST /api/sprints/{id}/complete

**TaskController**
- POST /api/tasks
- GET /api/tasks/{id}
- PUT /api/tasks/{id}
- DELETE /api/tasks/{id}
- GET /api/projects/{projectId}/tasks
- GET /api/users/{userId}/tasks
- PUT /api/tasks/{id}/assign/{userId}
- PUT /api/tasks/{id}/status
- GET /api/tasks/search

**NotificationController**
- GET /api/notifications/user/{userId}
- GET /api/notifications/unread/{userId}
- PUT /api/notifications/{id}/read

**ReportController**
- GET /api/reports/sprint/{sprintId}
- GET /api/reports/project/{projectId}/progress
- GET /api/reports/sprint/{sprintId}/export

### Frontend Components (React Web)

#### Page Components
- LoginPage
- RegisterPage
- DashboardPage
- ProjectListPage
- ProjectDetailPage
- SprintListPage
- SprintDetailPage
- TaskListPage
- TaskDetailPage
- CreateTaskPage
- NotificationPage
- ReportPage

#### Reusable Components
- TaskCard
- ProjectCard
- SprintCard
- ProgressBar
- StatusBadge
- PriorityBadge
- NotificationItem
- FilterPanel
- SearchBar
- Navbar
- Sidebar
- Modal (for create/edit forms)

## Data Models

### Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────┐
│    User     │────────<│   Project    │
│             │ manages │              │
└──────┬──────┘         └───────┬──────┘
       │                        │
       │ assigned to            │ contains
       │                        │
       ▼                        ▼
┌─────────────┐         ┌──────────────┐
│    Task     │────────<│    Sprint    │
│             │ part of │              │
└─────────────┘         └──────────────┘

┌─────────────┐
│Notification │
│             │
└──────┬──────┘
       │ sent to
       ▼
┌─────────────┐
│    User     │
└─────────────┘
```

### Database Schema

**users**
- id (PK, BIGINT, AUTO_INCREMENT)
- username (VARCHAR(50), UNIQUE, NOT NULL)
- email (VARCHAR(100), UNIQUE, NOT NULL)
- password (VARCHAR(255), NOT NULL)
- role (ENUM: MEMBER, PROJECT_MANAGER)
- created_at (TIMESTAMP)

**projects**
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(200), NOT NULL)
- description (TEXT)
- manager_id (FK → users.id)
- created_at (TIMESTAMP)

**project_members** (Join Table)
- project_id (FK → projects.id)
- user_id (FK → users.id)
- PRIMARY KEY (project_id, user_id)

**sprints**
- id (PK, BIGINT, AUTO_INCREMENT)
- name (VARCHAR(200), NOT NULL)
- project_id (FK → projects.id)
- start_date (DATE)
- end_date (DATE)
- status (ENUM: PLANNED, ACTIVE, COMPLETED)
- created_at (TIMESTAMP)

**tasks**
- id (PK, BIGINT, AUTO_INCREMENT)
- title (VARCHAR(200), NOT NULL)
- description (TEXT)
- status (ENUM: TODO, IN_PROGRESS, DONE, BLOCKED)
- priority (ENUM: LOW, MEDIUM, HIGH, CRITICAL)
- project_id (FK → projects.id)
- sprint_id (FK → sprints.id, NULLABLE)
- assignee_id (FK → users.id, NULLABLE)
- due_date (DATE)
- created_at (TIMESTAMP)
- updated_at (TIMESTAMP)

**notifications**
- id (PK, BIGINT, AUTO_INCREMENT)
- user_id (FK → users.id)
- message (VARCHAR(500), NOT NULL)
- type (ENUM: TASK_ASSIGNED, DEADLINE_REMINDER, STATUS_CHANGED)
- is_read (BOOLEAN, DEFAULT FALSE)
- created_at (TIMESTAMP)

### DTOs (Data Transfer Objects)

**UserDTO**
```java
public class UserDTO {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
```

**ProjectDTO**
```java
public class ProjectDTO {
    private String name;
    private String description;
    private Long managerId;
    private List<Long> memberIds;
}
```

**TaskDTO**
```java
public class TaskDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private Long sprintId;
    private Long assigneeId;
    private Date dueDate;
}
```

**SprintDTO**
```java
public class SprintDTO {
    private String name;
    private Long projectId;
    private Date startDate;
    private Date endDate;
}
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*


### Property Reflection

Sau khi phân tích 50 acceptance criteria, tôi đã xác định được 45 properties có thể kiểm thử. Tuy nhiên, một số properties có thể được gộp lại để tránh trùng lặp:

**Redundancies identified:**
- Properties về filtering (8.2, 8.3, 8.4) có thể được gộp vào property tổng quát về filtering (8.5)
- Properties về notification creation (9.1, 9.3) có thể được gộp thành một property về notification triggering
- Properties về task retrieval (4.5, 5.4) có thể được gộp vào property về data completeness

**Consolidated properties:** Sau khi loại bỏ redundancy, chúng ta có khoảng 38 unique properties cần implement.

### User Management Properties

**Property 1: User registration creates unique accounts**
*For any* valid registration data (unique username, valid email, password), creating a user account should result in a new user with a unique identifier and all provided information correctly stored.
**Validates: Requirements 1.1**

**Property 2: Valid credentials authenticate successfully**
*For any* user account in the system, providing correct username and password should result in successful authentication and access token generation.
**Validates: Requirements 1.2**

**Property 3: Invalid credentials are rejected**
*For any* invalid credential combination (wrong password, non-existent username, or empty fields), authentication attempts should be rejected with appropriate error messages.
**Validates: Requirements 1.3**

**Property 4: Logout invalidates session**
*For any* authenticated user session, logging out should invalidate the session token and prevent access to protected resources without re-authentication.
**Validates: Requirements 1.4**

**Property 5: Passwords are encrypted**
*For any* user account, the stored password in the database should never match the plain text password (must be hashed/encrypted).
**Validates: Requirements 1.5**

### Project Management Properties

**Property 6: Project creation assigns manager**
*For any* valid project data, creating a project should result in a new project with unique identifier and the creator set as project manager.
**Validates: Requirements 2.1**

**Property 7: Project updates are persisted**
*For any* existing project, updating its information should result in the changes being saved and retrievable in subsequent queries.
**Validates: Requirements 2.2**

**Property 8: Project deletion cascades to tasks**
*For any* project with associated tasks, deleting the project should also remove all tasks belonging to that project from the database.
**Validates: Requirements 2.3**

**Property 9: Project list filtered by access**
*For any* user, retrieving their project list should return exactly the projects where they are either the manager or a member, and no others.
**Validates: Requirements 2.4**

**Property 10: Adding members grants access**
*For any* project and user, adding the user as a project member should allow that user to view and access the project and its tasks.
**Validates: Requirements 2.5**

### Sprint Management Properties

**Property 11: Sprint creation associates with project**
*For any* valid sprint data with project reference, creating a sprint should result in a new sprint correctly associated with the specified project.
**Validates: Requirements 3.1**

**Property 12: Task assignment to sprint creates association**
*For any* task and sprint within the same project, assigning the task to the sprint should create the association and the task should appear in sprint queries.
**Validates: Requirements 3.2**

**Property 13: Sprint retrieval includes all tasks**
*For any* sprint, retrieving sprint details should return all tasks assigned to that sprint with their current status information.
**Validates: Requirements 3.4**

**Property 14: Sprint dates do not overlap**
*For any* two sprints within the same project, their date ranges (start to end) should not overlap, and attempting to create overlapping sprints should be rejected.
**Validates: Requirements 3.5**

### Task Management Properties

**Property 15: New tasks have TODO status**
*For any* valid task data, creating a task should result in a new task with initial status set to TODO regardless of other provided fields.
**Validates: Requirements 4.1**

**Property 16: Task updates modify timestamp**
*For any* existing task, updating any task information should save the changes and update the updatedAt timestamp to be later than the previous value.
**Validates: Requirements 4.2**

**Property 17: Status changes are persisted**
*For any* task and valid status value, changing the task status should result in the new status being saved and retrievable in subsequent queries.
**Validates: Requirements 4.3**

**Property 18: Task deletion removes from database**
*For any* existing task, deleting it should result in the task no longer being retrievable from the database.
**Validates: Requirements 4.4**

**Property 19: Task retrieval includes complete data**
*For any* task, retrieving it should return all fields including title, description, assignee, priority, status, due date, and timestamps.
**Validates: Requirements 4.5**

### Task Assignment Properties

**Property 20: Task assignment sets assignee and notifies**
*For any* task and project member, assigning the task should set the member as assignee and create a notification for that member.
**Validates: Requirements 5.1**

**Property 21: Assignment validates project membership**
*For any* task assignment attempt, if the assignee is not a member of the task's project, the assignment should be rejected with an error.
**Validates: Requirements 5.2**

**Property 22: Reassignment updates and notifies both parties**
*For any* task with an existing assignee, reassigning to a different member should update the assignee and create notifications for both the old and new assignees.
**Validates: Requirements 5.3**

**Property 23: User task list filtered by assignee**
*For any* user, retrieving their assigned tasks should return exactly the tasks where they are the assignee, and no others.
**Validates: Requirements 5.4**

**Property 24: Member removal unassigns tasks**
*For any* project member with assigned tasks, removing them from the project should clear the assignee field on all their tasks in that project.
**Validates: Requirements 5.5**

### Progress Tracking Properties

**Property 25: Completion percentage calculated correctly**
*For any* project, the completion percentage should equal (number of DONE tasks / total tasks) × 100, rounded appropriately.
**Validates: Requirements 6.1**

**Property 26: Status distribution calculated correctly**
*For any* sprint, the task distribution by status should accurately count tasks in each status (TODO, IN_PROGRESS, DONE, BLOCKED).
**Validates: Requirements 6.2**

**Property 27: Progress metrics reflect current state**
*For any* task status change, immediately querying progress metrics should reflect the updated status in calculations.
**Validates: Requirements 6.3**

**Property 28: Velocity calculated from completed tasks**
*For any* sprint, the velocity metric should equal the count of tasks with status DONE in that sprint.
**Validates: Requirements 6.5**

### Sprint Reporting Properties

**Property 29: Sprint report contains required sections**
*For any* sprint, generating a report should produce a report object containing sprint summary, list of completed tasks, and team performance metrics.
**Validates: Requirements 7.1**

**Property 30: Report includes all metrics**
*For any* generated sprint report, it should include task completion rate, velocity, and burndown chart data fields.
**Validates: Requirements 7.2**

**Property 31: Sprint completion triggers report generation**
*For any* sprint, when its status changes to COMPLETED, a final sprint report should be automatically generated and stored.
**Validates: Requirements 7.3**

**Property 32: Historical reports are retrievable**
*For any* project with completed sprints, retrieving historical reports should return reports for all completed sprints in that project.
**Validates: Requirements 7.4**

**Property 33: Reports are exportable**
*For any* sprint report, exporting it should produce valid PDF or CSV file content that can be saved.
**Validates: Requirements 7.5**

### Search and Filter Properties

**Property 34: Keyword search matches title and description**
*For any* search query string, the results should include all tasks where the query appears in either the title or description (case-insensitive).
**Validates: Requirements 8.1**

**Property 35: Multiple filters use AND logic**
*For any* combination of filters (status, priority, assignee), the results should include only tasks that match ALL applied filter criteria simultaneously.
**Validates: Requirements 8.2, 8.3, 8.4, 8.5**

### Notification Properties

**Property 36: Task events trigger notifications**
*For any* task assignment or status change by another user, a notification should be created for the task assignee with appropriate message and type.
**Validates: Requirements 9.1, 9.3**

**Property 37: Unread notifications are retrievable**
*For any* user, retrieving unread notifications should return all notifications where isRead is false, with complete timestamp and message information.
**Validates: Requirements 9.4**

**Property 38: Marking notification as read updates status**
*For any* notification, marking it as read should change its isRead field from false to true and persist the change.
**Validates: Requirements 9.5**

### Dashboard Properties

**Property 39: Dashboard groups tasks by status**
*For any* user, the dashboard should display their assigned tasks correctly grouped by status (TODO, IN_PROGRESS, DONE, BLOCKED).
**Validates: Requirements 10.1**

**Property 40: Dashboard identifies deadline tasks**
*For any* user, the dashboard should correctly identify and display tasks with due dates in the future (upcoming) and in the past (overdue).
**Validates: Requirements 10.2**

**Property 41: Dashboard shows active projects and sprints**
*For any* user, the dashboard should display all projects they are members of and the currently active sprints in those projects.
**Validates: Requirements 10.3**

**Property 42: Dashboard displays recent activity**
*For any* user, the dashboard should show recent activities (task creations, status changes, assignments) related to their projects, ordered by timestamp.
**Validates: Requirements 10.4**


## Error Handling

### Exception Hierarchy

```java
// Base exception
public class TaskManagementException extends RuntimeException {
    private String errorCode;
    private HttpStatus httpStatus;
}

// Specific exceptions
public class ResourceNotFoundException extends TaskManagementException {
    // For entities not found (404)
}

public class UnauthorizedException extends TaskManagementException {
    // For authentication failures (401)
}

public class ForbiddenException extends TaskManagementException {
    // For authorization failures (403)
}

public class ValidationException extends TaskManagementException {
    // For invalid input data (400)
}

public class ConflictException extends TaskManagementException {
    // For business rule violations (409)
}
```

### Error Response Format

```json
{
    "timestamp": "2024-12-02T10:30:00Z",
    "status": 404,
    "error": "Not Found",
    "message": "Task with id 123 not found",
    "path": "/api/tasks/123"
}
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("An unexpected error occurred"));
    }
}
```

### Validation Rules

**User Validation:**
- Username: 3-50 characters, alphanumeric and underscore only
- Email: Valid email format
- Password: Minimum 8 characters, at least one uppercase, one lowercase, one digit

**Project Validation:**
- Name: 1-200 characters, required
- Description: Maximum 1000 characters, optional

**Sprint Validation:**
- Name: 1-200 characters, required
- Start date: Cannot be in the past
- End date: Must be after start date
- Date range: Cannot overlap with existing sprints in the same project

**Task Validation:**
- Title: 1-200 characters, required
- Description: Maximum 2000 characters, optional
- Due date: Cannot be in the past
- Assignee: Must be a member of the task's project
- Sprint: Must belong to the same project as the task

## Testing Strategy

### Unit Testing

**Framework:** JUnit 5 + Mockito

**Coverage Areas:**
- Service layer business logic
- Repository queries
- Validation logic
- DTO conversions
- Exception handling

**Example Unit Tests:**
```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private TaskService taskService;
    
    @Test
    void createTask_WithValidData_ShouldCreateTaskWithTodoStatus() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("Test Task", "Description", 
            TaskPriority.HIGH, null, 1L, null, null);
        
        // Act
        Task result = taskService.createTask(taskDTO);
        
        // Assert
        assertEquals(TaskStatus.TODO, result.getStatus());
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    void assignTask_WithNonProjectMember_ShouldThrowException() {
        // Arrange
        Long taskId = 1L;
        Long userId = 2L;
        when(projectService.isMember(any(), any())).thenReturn(false);
        
        // Act & Assert
        assertThrows(ValidationException.class, 
            () -> taskService.assignTask(taskId, userId));
    }
}
```

### Property-Based Testing

**Framework:** jqwik (Java property-based testing library)

**Configuration:** Each property test should run minimum 100 iterations

**Test Annotation Format:**
```java
/**
 * Feature: task-project-management, Property X: [property description]
 * Validates: Requirements X.Y
 */
@Property
void propertyTestName(@ForAll ...) {
    // Test implementation
}
```

**Example Property Tests:**
```java
class TaskPropertyTests {
    
    /**
     * Feature: task-project-management, Property 15: New tasks have TODO status
     * Validates: Requirements 4.1
     */
    @Property(tries = 100)
    void newTasksAlwaysHaveTodoStatus(
        @ForAll @StringLength(min = 1, max = 200) String title,
        @ForAll @StringLength(max = 2000) String description,
        @ForAll TaskPriority priority) {
        
        // Arrange
        TaskDTO taskDTO = new TaskDTO(title, description, priority, 
            null, projectId, null, null);
        
        // Act
        Task task = taskService.createTask(taskDTO);
        
        // Assert
        assertEquals(TaskStatus.TODO, task.getStatus());
    }
    
    /**
     * Feature: task-project-management, Property 8: Project deletion cascades to tasks
     * Validates: Requirements 2.3
     */
    @Property(tries = 100)
    void deletingProjectDeletesAllTasks(
        @ForAll @IntRange(min = 0, max = 50) int taskCount) {
        
        // Arrange
        Project project = createTestProject();
        List<Task> tasks = createRandomTasks(project, taskCount);
        
        // Act
        projectService.deleteProject(project.getId());
        
        // Assert
        for (Task task : tasks) {
            assertFalse(taskRepository.existsById(task.getId()));
        }
    }
    
    /**
     * Feature: task-project-management, Property 25: Completion percentage calculated correctly
     * Validates: Requirements 6.1
     */
    @Property(tries = 100)
    void completionPercentageIsAccurate(
        @ForAll @IntRange(min = 1, max = 100) int totalTasks,
        @ForAll @IntRange(min = 0, max = 100) int completedTasks) {
        
        Assume.that(completedTasks <= totalTasks);
        
        // Arrange
        Project project = createProjectWithTasks(totalTasks, completedTasks);
        
        // Act
        double percentage = reportService.getProjectProgress(project.getId())
            .getCompletionPercentage();
        
        // Assert
        double expected = (completedTasks * 100.0) / totalTasks;
        assertEquals(expected, percentage, 0.01);
    }
}
```

### Integration Testing

**Framework:** Spring Boot Test + TestContainers (for MySQL)

**Coverage Areas:**
- REST API endpoints
- Database operations
- Authentication and authorization
- End-to-end workflows

**Example Integration Test:**
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
class TaskControllerIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void createTask_WithValidData_ReturnsCreatedTask() {
        // Arrange
        TaskDTO taskDTO = new TaskDTO("Integration Test Task", 
            "Description", TaskPriority.HIGH, null, 1L, null, null);
        
        // Act
        ResponseEntity<Task> response = restTemplate.postForEntity(
            "/api/tasks", taskDTO, Task.class);
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TaskStatus.TODO, response.getBody().getStatus());
    }
}
```

### Frontend Testing (React Web)

**Framework:** Jest + React Testing Library

**Coverage Areas:**
- Component rendering
- User interactions
- Navigation flows
- API integration
- Form validation

**Example Component Test:**
```javascript
describe('TaskCard', () => {
  it('displays task information correctly', () => {
    const task = {
      id: 1,
      title: 'Test Task',
      status: 'TODO',
      priority: 'HIGH',
      dueDate: '2024-12-10'
    };
    
    const { getByText } = render(<TaskCard task={task} />);
    
    expect(getByText('Test Task')).toBeInTheDocument();
    expect(getByText('TODO')).toBeInTheDocument();
    expect(getByText('HIGH')).toBeInTheDocument();
  });
  
  it('handles click events', () => {
    const handleClick = jest.fn();
    const task = { id: 1, title: 'Test Task' };
    
    const { getByText } = render(
      <TaskCard task={task} onClick={handleClick} />
    );
    
    fireEvent.click(getByText('Test Task'));
    expect(handleClick).toHaveBeenCalledWith(task);
  });
});
```

### Test Data Generators

For property-based testing, we need generators for domain objects:

```java
@Provide
Arbitrary<User> users() {
    return Combinators.combine(
        Arbitraries.strings().alpha().ofLength(10),
        Arbitraries.emails(),
        Arbitraries.strings().ofLength(12)
    ).as((username, email, password) -> 
        new User(username, email, password, UserRole.MEMBER));
}

@Provide
Arbitrary<Task> tasks() {
    return Combinators.combine(
        Arbitraries.strings().ofMinLength(1).ofMaxLength(200),
        Arbitraries.strings().ofMaxLength(2000),
        Arbitraries.of(TaskPriority.class),
        Arbitraries.of(TaskStatus.class)
    ).as((title, desc, priority, status) -> 
        new Task(title, desc, priority, status));
}
```

## Security Considerations

### Authentication
- JWT (JSON Web Token) based authentication
- Token expiration: 24 hours
- Refresh token mechanism for extended sessions

### Authorization
- Role-based access control (RBAC)
- Project-level permissions (manager vs member)
- Resource ownership validation

### Password Security
- BCrypt hashing with salt
- Minimum password strength requirements
- Password change functionality

### API Security
- HTTPS only in production
- CORS configuration for React web app
- Rate limiting on authentication endpoints
- Input validation and sanitization
- SQL injection prevention (JPA/Hibernate)
- CSRF protection

### Data Privacy
- User data encryption at rest
- Secure session management
- Audit logging for sensitive operations

## Deployment Architecture

### Development Environment
```
┌─────────────────┐
│  React Web App  │
│  (Vite/CRA)     │
│  localhost:3000 │
└────────┬────────┘
         │
┌────────▼────────┐
│  Spring Boot    │
│  localhost:8080 │
└────────┬────────┘
         │
┌────────▼────────┐
│  MySQL          │
│  localhost:3306 │
└─────────────────┘
```

### Docker Compose Setup
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_DATABASE: taskmanagement
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
  
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/taskmanagement
```

## Performance Considerations

### Database Optimization
- Indexes on foreign keys and frequently queried fields
- Pagination for list endpoints (default page size: 20)
- Lazy loading for entity relationships
- Connection pooling (HikariCP)

### Caching Strategy
- Spring Cache for frequently accessed data
- Cache user sessions
- Cache project member lists
- TTL: 5 minutes for most cached data

### API Response Optimization
- DTO projections to avoid over-fetching
- Batch operations for bulk updates
- Async processing for notifications and reports

## Monitoring and Logging

### Logging Strategy
- SLF4J + Logback
- Log levels: ERROR, WARN, INFO, DEBUG
- Structured logging with correlation IDs
- Log rotation and retention policies

### Metrics
- API response times
- Database query performance
- Error rates by endpoint
- Active user sessions

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity check
- Disk space monitoring

