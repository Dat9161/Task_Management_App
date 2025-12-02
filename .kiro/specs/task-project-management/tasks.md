# Implementation Plan - Task & Project Management Tool

- [x] 1. Thiết lập môi trường và cấu trúc dự án





  - Tạo Spring Boot project với Maven
  - Cấu hình MySQL database connection
  - Thiết lập Docker và Docker Compose
  - Tạo React Native project
  - Cấu hình API base URL và Axios
  - _Requirements: 13.1, 13.4_

- [x] 2. Implement Domain Layer - Entities và Enums





  - Tạo các enum classes (Role, TaskStatus, Priority, SprintStatus, NotificationType)
  - Implement User entity với JPA annotations
  - Implement Project entity với relationships
  - Implement Sprint entity với date validation
  - Implement Task entity với status tracking
  - Implement Todo entity
  - Implement Notification entity
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 6.1, 9.3_

- [ ]* 2.1 Write property test for password encryption invariant
  - **Property 3: Password encryption invariant**
  - **Validates: Requirements 1.5**

- [x] 3. Implement Repository Layer





  - Tạo IRepository generic interface
  - Implement IUserRepository với custom queries
  - Implement IProjectRepository với access control queries
  - Implement ISprintRepository với date filtering
  - Implement ITaskRepository với search và filter methods
  - Implement ITodoRepository
  - Implement INotificationRepository
  - _Requirements: 1.1, 2.1, 3.1, 4.1, 6.1, 9.3, 10.1_

- [ ]* 3.1 Write property test for repository CRUD operations
  - Test that save then findById returns same entity
  - Test that delete then findById returns null
  - _Requirements: 2.1, 4.1_

- [ ] 4. Implement Authentication Service và Security
  - Implement password hashing với BCrypt
  - Implement JWT token generation và validation
  - Implement AuthenticationService (register, login, logout)
  - Configure Spring Security với JWT filter
  - Implement password reset functionality
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [ ]* 4.1 Write property test for user registration
  - **Property 1: User registration creates valid accounts**
  - **Validates: Requirements 1.1, 1.5**

- [ ]* 4.2 Write property test for authentication round-trip
  - **Property 2: Authentication round-trip**
  - **Validates: Requirements 1.2, 1.3**

- [ ]* 4.3 Write property test for password reset
  - **Property 4: Password reset generates valid tokens**
  - **Validates: Requirements 1.4**

- [ ] 5. Implement Project Management Service
  - Implement ProjectService với CRUD operations
  - Implement project creation và creator association
  - Implement access control logic (member management)
  - Implement project update với timestamp tracking
  - Implement cascade deletion cho projects
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ]* 5.1 Write property test for project creation
  - **Property 5: Project creation and association**
  - **Validates: Requirements 2.1**

- [ ]* 5.2 Write property test for project access control
  - **Property 6: Project access control**
  - **Validates: Requirements 2.2**

- [ ]* 5.3 Write property test for project deletion cascades
  - **Property 8: Project deletion cascades**
  - **Validates: Requirements 2.4**

- [ ]* 5.4 Write property test for member addition
  - **Property 9: Member addition grants access**
  - **Validates: Requirements 2.5**

- [ ] 6. Implement Sprint Management Service
  - Implement SprintService với CRUD operations
  - Implement date validation logic
  - Implement sprint ordering by start date
  - Implement sprint deletion với task preservation
  - Implement auto-completion scheduler (optional)
  - _Requirements: 3.1, 3.2, 3.3, 3.5_

- [ ]* 6.1 Write property test for sprint date validation
  - **Property 10: Sprint date validation**
  - **Validates: Requirements 3.1**

- [ ]* 6.2 Write property test for sprint ordering
  - **Property 11: Sprint ordering invariant**
  - **Validates: Requirements 3.2**

- [ ]* 6.3 Write property test for sprint deletion preserves tasks
  - **Property 13: Sprint deletion preserves tasks**
  - **Validates: Requirements 3.5**

- [ ] 7. Implement Task Management Service
  - Implement TaskService với CRUD operations
  - Implement task creation và backlog management
  - Implement task assignment với notification
  - Implement task-sprint association
  - Implement task update với timestamp tracking
  - Implement task deletion
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ]* 7.1 Write property test for task creation
  - **Property 14: Task creation adds to backlog**
  - **Validates: Requirements 4.1**

- [ ]* 7.2 Write property test for task assignment
  - **Property 15: Task assignment creates notification**
  - **Validates: Requirements 4.2**

- [ ]* 7.3 Write property test for task-sprint association
  - **Property 16: Task-sprint association**
  - **Validates: Requirements 4.3**

- [ ] 8. Implement Task Status Management
  - Implement status transition logic trong Task entity
  - Implement timestamp recording cho status changes
  - Implement blocking reason validation
  - Implement remaining hours calculation
  - Implement authorization check cho status changes
  - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5_

- [ ]* 8.1 Write property test for status transition to In Progress
  - **Property 19: Status transition to In Progress records start time**
  - **Validates: Requirements 5.1**

- [ ]* 8.2 Write property test for status transition to Done
  - **Property 20: Status transition to Done records completion time**
  - **Validates: Requirements 5.2**

- [ ]* 8.3 Write property test for blocked status validation
  - **Property 21: Blocked status requires reason**
  - **Validates: Requirements 5.3**

- [ ]* 8.4 Write property test for remaining hours calculation
  - **Property 22: Remaining hours calculation**
  - **Validates: Requirements 5.4**

- [ ]* 8.5 Write property test for status change authorization
  - **Property 23: Status change authorization**
  - **Validates: Requirements 5.5**

- [ ] 9. Implement Todo Management Service
  - Implement TodoService với CRUD operations
  - Implement todo creation và user association
  - Implement todo ordering by due date
  - Implement todo completion với timestamp
  - Implement overdue todo identification
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ]* 9.1 Write property test for todo ordering
  - **Property 25: Todo ordering invariant**
  - **Validates: Requirements 6.2**

- [ ]* 9.2 Write property test for overdue identification
  - **Property 28: Overdue todo identification**
  - **Validates: Requirements 6.5**

- [ ] 10. Implement Sprint Progress Tracking
  - Implement task status aggregation logic
  - Implement completion percentage calculation
  - Implement hours aggregation (estimated vs actual)
  - Implement burndown chart data generation
  - Implement blocked task identification
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ]* 10.1 Write property test for task status aggregation
  - **Property 29: Task status aggregation**
  - **Validates: Requirements 7.1**

- [ ]* 10.2 Write property test for completion percentage
  - **Property 30: Completion percentage calculation**
  - **Validates: Requirements 7.2**

- [ ]* 10.3 Write property test for hours aggregation
  - **Property 31: Hours aggregation**
  - **Validates: Requirements 7.3**

- [ ] 11. Implement Report Service
  - Implement SprintReport generation logic
  - Implement completed tasks aggregation
  - Implement incomplete tasks aggregation
  - Implement velocity calculation
  - Implement member contribution calculation
  - Implement PDF export functionality
  - _Requirements: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ]* 11.1 Write property test for velocity calculation
  - **Property 36: Velocity calculation**
  - **Validates: Requirements 8.3**

- [ ]* 11.2 Write property test for member contributions
  - **Property 37: Member contribution aggregation**
  - **Validates: Requirements 8.4**

- [ ] 12. Implement Notification Service
  - Implement NotificationService với Observer pattern
  - Implement task assignment notification
  - Implement status change notification
  - Implement notification retrieval và ordering
  - Implement mark as read functionality
  - _Requirements: 9.3, 9.4, 9.5_

- [ ]* 12.1 Write property test for assignment notification
  - **Property 41: Assignment notification creation**
  - **Validates: Requirements 9.3**

- [ ]* 12.2 Write property test for status change notification
  - **Property 42: Status change notification**
  - **Validates: Requirements 9.4**

- [ ]* 12.3 Write property test for notification ordering
  - **Property 43: Notification ordering**
  - **Validates: Requirements 9.5**

- [ ] 13. Implement Dashboard Service
  - Implement dashboard data aggregation
  - Implement task filtering by user và status
  - Implement upcoming deadline calculation
  - Integrate với TaskService và TodoService
  - _Requirements: 9.1, 9.2_

- [ ]* 13.1 Write property test for dashboard task filtering
  - **Property 39: Dashboard task filtering**
  - **Validates: Requirements 9.1**

- [ ]* 13.2 Write property test for deadline identification
  - **Property 40: Upcoming deadline identification**
  - **Validates: Requirements 9.2**

- [ ] 14. Implement Search và Filter Service
  - Implement search across multiple fields
  - Implement status filter
  - Implement priority filter
  - Implement assignee filter
  - Implement compound filter logic (AND)
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5_

- [ ]* 14.1 Write property test for search functionality
  - **Property 44: Search across multiple fields**
  - **Validates: Requirements 10.1**

- [ ]* 14.2 Write property test for multiple filters
  - **Property 48: Multiple filter conjunction**
  - **Validates: Requirements 10.5**

- [ ] 15. Implement Authorization và Access Control
  - Implement role-based access control
  - Implement project access authorization
  - Implement task modification authorization
  - Implement security event logging
  - Configure method-level security annotations
  - _Requirements: 12.1, 12.2, 12.3, 12.4_

- [ ]* 15.1 Write property test for RBAC
  - **Property 49: Role-based access control**
  - **Validates: Requirements 12.1**

- [ ]* 15.2 Write property test for project authorization
  - **Property 50: Project access authorization**
  - **Validates: Requirements 12.2**

- [ ]* 15.3 Write property test for task authorization
  - **Property 51: Task modification authorization**
  - **Validates: Requirements 12.3**

- [ ] 16. Implement REST API Controllers
  - Implement AuthenticationController với login/register endpoints
  - Implement ProjectController với CRUD endpoints
  - Implement SprintController với CRUD endpoints
  - Implement TaskController với CRUD và search endpoints
  - Implement TodoController với CRUD endpoints
  - Implement ReportController với report generation endpoint
  - Implement DashboardController
  - Add request/response DTOs
  - Add input validation
  - Add error handling với @ControllerAdvice
  - _Requirements: All functional requirements_

- [ ]* 16.1 Write integration tests for API endpoints
  - Test authentication flow
  - Test CRUD operations
  - Test error responses
  - _Requirements: 1.1, 2.1, 3.1, 4.1_

- [ ] 17. Checkpoint - Backend Testing
  - Ensure all tests pass, ask the user if questions arise.

- [ ] 18. Setup React Native Project Structure
  - Initialize React Native project
  - Setup navigation (React Navigation)
  - Configure Redux store và slices
  - Setup Axios với base URL và interceptors
  - Configure AsyncStorage cho token management
  - Setup environment variables
  - _Requirements: 13.1_

- [ ] 19. Implement Authentication Screens (React Native)
  - Create LoginScreen với form validation
  - Create RegisterScreen với form validation
  - Implement authentication Redux slice
  - Implement token storage và retrieval
  - Implement auto-login on app start
  - Add loading states và error handling
  - _Requirements: 1.1, 1.2, 1.3_

- [ ] 20. Implement Dashboard Screen (React Native)
  - Create DashboardScreen layout
  - Display tasks grouped by status
  - Display upcoming deadlines
  - Display notifications panel
  - Implement pull-to-refresh
  - Add navigation to task details
  - _Requirements: 9.1, 9.2, 9.5_

- [ ] 21. Implement Project Management Screens (React Native)
  - Create ProjectListScreen với search
  - Create ProjectDetailScreen
  - Create CreateProjectForm
  - Implement project Redux slice
  - Add member management UI
  - Add loading states và error handling
  - _Requirements: 2.1, 2.2, 2.3, 2.5_

- [ ] 22. Implement Sprint Management Screens (React Native)
  - Create SprintListScreen
  - Create SprintDetailScreen với progress visualization
  - Create CreateSprintForm với date pickers
  - Implement sprint Redux slice
  - Display burndown chart
  - Add task list trong sprint
  - _Requirements: 3.1, 3.2, 3.3, 7.1, 7.2, 7.4_

- [ ] 23. Implement Task Management Screens (React Native)
  - Create TaskListScreen với filters
  - Create TaskDetailScreen
  - Create CreateTaskForm
  - Implement task Redux slice
  - Add status update UI (drag-and-drop hoặc buttons)
  - Add task assignment UI
  - Add search và filter UI
  - _Requirements: 4.1, 4.2, 5.1, 5.2, 10.1, 10.2_

- [ ] 24. Implement Todo Management Screens (React Native)
  - Create TodoListScreen
  - Create CreateTodoForm
  - Implement todo Redux slice
  - Add completion checkbox
  - Highlight overdue todos
  - Add swipe-to-delete functionality
  - _Requirements: 6.1, 6.2, 6.3, 6.5_

- [ ] 25. Implement Report Screen (React Native)
  - Create SprintReportScreen
  - Display completed và incomplete tasks
  - Display velocity metrics
  - Display member contributions
  - Add PDF export functionality (optional)
  - _Requirements: 8.1, 8.2, 8.3, 8.4_

- [ ] 26. Implement Notification System (React Native)
  - Create NotificationPanel component
  - Display unread notifications
  - Implement mark as read functionality
  - Add notification badge on dashboard
  - Implement real-time updates (polling hoặc WebSocket - optional)
  - _Requirements: 9.3, 9.4, 9.5_

- [ ] 27. Implement UI Components và Styling
  - Create reusable Button component
  - Create reusable Input component
  - Create reusable Card component
  - Create TaskCard component
  - Create SprintCard component
  - Define color scheme và theme
  - Add icons với React Native Vector Icons
  - Ensure responsive design
  - _Requirements: All UI requirements_

- [ ] 28. Implement Error Handling và Validation (React Native)
  - Add form validation với Yup
  - Display error messages
  - Handle network errors
  - Handle authentication errors (401, 403)
  - Add retry logic cho failed requests
  - Add offline detection
  - _Requirements: 1.3, All validation requirements_

- [ ] 29. Add Loading States và User Feedback
  - Add loading spinners
  - Add skeleton screens
  - Add success messages (toast/snackbar)
  - Add confirmation dialogs cho delete operations
  - Add empty states
  - _Requirements: User experience_

- [ ] 30. Optimize Performance
  - Implement pagination cho lists
  - Add memoization với React.memo
  - Optimize Redux selectors
  - Add image optimization
  - Implement lazy loading
  - _Requirements: 11.1, 11.2_

- [ ] 31. Final Testing và Bug Fixes
  - Test all user flows
  - Test on iOS và Android devices
  - Fix any bugs discovered
  - Test offline behavior
  - Test error scenarios
  - _Requirements: All requirements_

- [ ] 32. Documentation và Deployment Preparation
  - Write API documentation với Swagger
  - Write README với setup instructions
  - Create user manual (optional)
  - Prepare Docker deployment
  - Create database migration scripts
  - _Requirements: Project documentation_

- [ ] 33. Final Checkpoint
  - Ensure all tests pass, ask the user if questions arise.
