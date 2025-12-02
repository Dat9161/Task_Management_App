# Requirements Document - Task & Project Management Tool

## Introduction

Ứng dụng Task & Project Management Tool là một hệ thống quản lý công việc và dự án được thiết kế để hỗ trợ các nhóm làm việc theo phương pháp Agile/Scrum. Hệ thống cho phép quản lý to-do cá nhân, phân công task cho nhóm, theo dõi tiến độ công việc, và tạo báo cáo theo sprint. Đây là một giải pháp phần mềm hướng đối tượng được xây dựng để giải quyết bài toán thực tế trong quản lý dự án phần mềm.

## Glossary

- **System**: Task & Project Management Tool - hệ thống quản lý task và dự án
- **User**: Người dùng của hệ thống (có thể là member hoặc project manager)
- **Task**: Công việc cụ thể cần hoàn thành với thông tin như tên, mô tả, độ ưu tiên, trạng thái
- **Project**: Dự án chứa nhiều task và được quản lý theo sprint
- **Sprint**: Chu kỳ làm việc ngắn (thường 1-4 tuần) trong phương pháp Agile
- **Member**: Thành viên trong nhóm, có thể được phân công task
- **Project Manager**: Người quản lý dự án, có quyền tạo project, phân công task
- **Status**: Trạng thái của task (TODO, IN_PROGRESS, DONE, BLOCKED)
- **Priority**: Độ ưu tiên của task (LOW, MEDIUM, HIGH, CRITICAL)
- **Report**: Báo cáo tiến độ công việc theo sprint

## Requirements

### Requirement 1: Quản lý người dùng (User Management)

**User Story:** As a user, I want to register and login to the system, so that I can access my tasks and projects securely.

#### Acceptance Criteria

1. WHEN a user provides valid registration information (username, email, password) THEN the System SHALL create a new user account with unique identifier
2. WHEN a user provides valid login credentials THEN the System SHALL authenticate the user and grant access to the application
3. WHEN a user provides invalid credentials THEN the System SHALL reject the login attempt and display an error message
4. WHEN a user logs out THEN the System SHALL terminate the user session and require re-authentication for subsequent access
5. THE System SHALL store user passwords in encrypted format to ensure security

### Requirement 2: Quản lý dự án (Project Management)

**User Story:** As a project manager, I want to create and manage projects, so that I can organize work into structured initiatives.

#### Acceptance Criteria

1. WHEN a project manager creates a project with name and description THEN the System SHALL create a new project with unique identifier and set the creator as project manager
2. WHEN a project manager updates project information THEN the System SHALL save the changes and maintain project history
3. WHEN a project manager deletes a project THEN the System SHALL remove the project and all associated tasks from the database
4. WHEN a user views project list THEN the System SHALL display all projects the user has access to with basic information
5. WHEN a project manager adds a member to a project THEN the System SHALL grant that member access to view and work on project tasks

### Requirement 3: Quản lý Sprint (Sprint Management)

**User Story:** As a project manager, I want to create and manage sprints within projects, so that I can organize work into time-boxed iterations.

#### Acceptance Criteria

1. WHEN a project manager creates a sprint with name, start date, and end date THEN the System SHALL create a new sprint associated with the project
2. WHEN a project manager assigns tasks to a sprint THEN the System SHALL associate those tasks with the sprint
3. WHEN a sprint end date is reached THEN the System SHALL mark the sprint as completed
4. WHEN viewing a sprint THEN the System SHALL display all tasks assigned to that sprint with their current status
5. THE System SHALL prevent overlapping sprint dates within the same project

### Requirement 4: Quản lý Task (Task Management)

**User Story:** As a user, I want to create and manage tasks, so that I can track work items that need to be completed.

#### Acceptance Criteria

1. WHEN a user creates a task with title, description, priority, and due date THEN the System SHALL create a new task with status TODO
2. WHEN a user updates task information THEN the System SHALL save the changes and update the last modified timestamp
3. WHEN a user changes task status THEN the System SHALL update the status and record the status change history
4. WHEN a user deletes a task THEN the System SHALL remove the task from the database
5. WHEN viewing tasks THEN the System SHALL display tasks with all relevant information including assignee, priority, status, and due date

### Requirement 5: Phân công Task (Task Assignment)

**User Story:** As a project manager, I want to assign tasks to team members, so that work is distributed and responsibilities are clear.

#### Acceptance Criteria

1. WHEN a project manager assigns a task to a member THEN the System SHALL set that member as the task assignee and send notification
2. WHEN a task is assigned to a member THEN the System SHALL verify that the member belongs to the project
3. WHEN a project manager reassigns a task to a different member THEN the System SHALL update the assignee and notify both old and new assignees
4. WHEN viewing assigned tasks THEN the System SHALL display all tasks assigned to the current user
5. WHEN a member is removed from a project THEN the System SHALL unassign all tasks assigned to that member

### Requirement 6: Theo dõi tiến độ (Progress Tracking)

**User Story:** As a project manager, I want to track project and sprint progress, so that I can monitor team performance and identify bottlenecks.

#### Acceptance Criteria

1. WHEN viewing project dashboard THEN the System SHALL display the percentage of completed tasks versus total tasks
2. WHEN viewing sprint progress THEN the System SHALL display task distribution by status (TODO, IN_PROGRESS, DONE, BLOCKED)
3. WHEN a task status changes THEN the System SHALL update the progress metrics in real-time
4. WHEN viewing task details THEN the System SHALL display the task completion percentage based on subtasks if applicable
5. THE System SHALL calculate and display velocity metrics based on completed tasks per sprint

### Requirement 7: Báo cáo Sprint (Sprint Reporting)

**User Story:** As a project manager, I want to generate sprint reports, so that I can review team performance and present results to stakeholders.

#### Acceptance Criteria

1. WHEN a project manager requests a sprint report THEN the System SHALL generate a report containing sprint summary, completed tasks, and team performance metrics
2. WHEN generating a report THEN the System SHALL include task completion rate, velocity, and burndown chart data
3. WHEN a sprint is completed THEN the System SHALL automatically generate a final sprint report
4. WHEN viewing historical reports THEN the System SHALL display all past sprint reports for the project
5. THE System SHALL allow exporting sprint reports in PDF or CSV format

### Requirement 8: Tìm kiếm và Lọc (Search and Filter)

**User Story:** As a user, I want to search and filter tasks, so that I can quickly find specific work items.

#### Acceptance Criteria

1. WHEN a user enters search keywords THEN the System SHALL return all tasks matching the keywords in title or description
2. WHEN a user applies filters by status THEN the System SHALL display only tasks with the selected status
3. WHEN a user applies filters by priority THEN the System SHALL display only tasks with the selected priority level
4. WHEN a user applies filters by assignee THEN the System SHALL display only tasks assigned to the selected member
5. WHEN a user applies multiple filters THEN the System SHALL display tasks matching all filter criteria

### Requirement 9: Thông báo (Notifications)

**User Story:** As a user, I want to receive notifications about task updates, so that I stay informed about changes affecting my work.

#### Acceptance Criteria

1. WHEN a task is assigned to a user THEN the System SHALL send a notification to that user
2. WHEN a task deadline is approaching (within 24 hours) THEN the System SHALL send a reminder notification to the assignee
3. WHEN a task status is changed by another user THEN the System SHALL notify the task assignee
4. WHEN viewing notifications THEN the System SHALL display all unread notifications with timestamp and description
5. WHEN a user marks a notification as read THEN the System SHALL update the notification status

### Requirement 10: Dashboard và Tổng quan (Dashboard and Overview)

**User Story:** As a user, I want to view a dashboard with overview of my tasks and projects, so that I can quickly understand my workload and priorities.

#### Acceptance Criteria

1. WHEN a user accesses the dashboard THEN the System SHALL display a summary of assigned tasks grouped by status
2. WHEN viewing the dashboard THEN the System SHALL display upcoming deadlines and overdue tasks prominently
3. WHEN viewing the dashboard THEN the System SHALL display active projects and current sprint information
4. WHEN viewing the dashboard THEN the System SHALL display recent activity and updates
5. THE System SHALL allow users to customize dashboard widgets and layout preferences

