# Requirements Document

## Introduction

Hệ thống Task & Project Management Tool là một ứng dụng quản lý dự án và công việc được thiết kế cho các nhóm phát triển phần mềm sử dụng phương pháp Agile/Scrum. Hệ thống cho phép quản lý to-do cá nhân, task nhóm, theo dõi tiến độ, phân công công việc và tạo báo cáo theo sprint. Ứng dụng này giải quyết vấn đề phối hợp công việc trong nhóm, theo dõi tiến độ dự án và đảm bảo tính minh bạch trong quá trình phát triển phần mềm.

## Glossary

- **System**: Task & Project Management Tool - hệ thống quản lý dự án và công việc
- **User**: Người dùng của hệ thống, có thể là thành viên nhóm hoặc quản lý dự án
- **Project**: Dự án - một tập hợp các sprint và task liên quan đến một mục tiêu chung
- **Sprint**: Chu kỳ phát triển ngắn (thường 1-4 tuần) trong phương pháp Scrum
- **Task**: Công việc cụ thể cần hoàn thành trong một sprint
- **Todo**: Công việc cá nhân của người dùng
- **Team Member**: Thành viên nhóm - người thực hiện các task
- **Project Manager**: Quản lý dự án - người tạo và quản lý project, sprint và phân công task
- **Status**: Trạng thái của task (To Do, In Progress, Done, Blocked)
- **Priority**: Mức độ ưu tiên của task (Low, Medium, High, Critical)
- **Report**: Báo cáo tổng hợp về tiến độ và hiệu suất

## Requirements

### Requirement 1: Quản lý người dùng và xác thực

**User Story:** Là một người dùng, tôi muốn đăng ký và đăng nhập vào hệ thống, để có thể truy cập và quản lý công việc của mình.

#### Acceptance Criteria

1. WHEN a User provides valid registration information (username, email, password), THE System SHALL create a new user account and store the credentials securely
2. WHEN a User provides valid login credentials, THE System SHALL authenticate the User and grant access to the application
3. WHEN a User provides invalid login credentials, THE System SHALL reject the authentication attempt and display an error message
4. WHEN a User requests password reset, THE System SHALL send a password reset link to the registered email address
5. THE System SHALL encrypt all user passwords before storing them in the database

### Requirement 2: Quản lý dự án (Project)

**User Story:** Là một Project Manager, tôi muốn tạo và quản lý các dự án, để tổ chức công việc theo từng dự án riêng biệt.

#### Acceptance Criteria

1. WHEN a Project Manager creates a new Project with name and description, THE System SHALL store the Project and associate it with the creator
2. WHEN a Project Manager views the project list, THE System SHALL display all Projects that the User has access to
3. WHEN a Project Manager updates Project information, THE System SHALL save the changes and update the modification timestamp
4. WHEN a Project Manager deletes a Project, THE System SHALL remove the Project and all associated Sprints and Tasks
5. WHEN a Project Manager adds a Team Member to a Project, THE System SHALL grant that User access to view and work on the Project

### Requirement 3: Quản lý Sprint

**User Story:** Là một Project Manager, tôi muốn tạo và quản lý các sprint trong dự án, để tổ chức công việc theo chu kỳ phát triển ngắn.

#### Acceptance Criteria

1. WHEN a Project Manager creates a Sprint with name, start date, and end date within a Project, THE System SHALL validate the dates and create the Sprint
2. WHEN a Project Manager views a Project, THE System SHALL display all Sprints belonging to that Project ordered by start date
3. WHEN a Project Manager updates Sprint information, THE System SHALL validate the new dates and save the changes
4. WHEN a Sprint end date is reached, THE System SHALL automatically mark the Sprint as completed
5. WHEN a Project Manager deletes a Sprint, THE System SHALL move all associated Tasks back to the Project backlog

### Requirement 4: Quản lý Task

**User Story:** Là một Project Manager, tôi muốn tạo và phân công các task, để phân chia công việc cho các thành viên trong nhóm.

#### Acceptance Criteria

1. WHEN a Project Manager creates a Task with title, description, Priority, and estimated hours, THE System SHALL create the Task and add it to the Project backlog
2. WHEN a Project Manager assigns a Task to a Team Member, THE System SHALL update the Task assignee and notify the Team Member
3. WHEN a Project Manager adds a Task to a Sprint, THE System SHALL associate the Task with that Sprint
4. WHEN a Project Manager updates Task information, THE System SHALL save the changes and update the modification timestamp
5. WHEN a Project Manager deletes a Task, THE System SHALL remove the Task from the System

### Requirement 5: Cập nhật trạng thái Task

**User Story:** Là một Team Member, tôi muốn cập nhật trạng thái công việc của mình, để phản ánh tiến độ thực tế.

#### Acceptance Criteria

1. WHEN a Team Member changes a Task Status from "To Do" to "In Progress", THE System SHALL update the Status and record the start time
2. WHEN a Team Member changes a Task Status to "Done", THE System SHALL update the Status and record the completion time
3. WHEN a Team Member changes a Task Status to "Blocked", THE System SHALL update the Status and require a blocking reason
4. WHEN a Team Member updates actual hours spent on a Task, THE System SHALL record the hours and calculate the remaining hours
5. THE System SHALL prevent a Team Member from changing the Status of a Task not assigned to them

### Requirement 6: Quản lý Todo cá nhân

**User Story:** Là một User, tôi muốn tạo và quản lý danh sách todo cá nhân, để theo dõi các công việc riêng của mình.

#### Acceptance Criteria

1. WHEN a User creates a Todo with title and optional due date, THE System SHALL create the Todo and associate it with the User
2. WHEN a User views their todo list, THE System SHALL display all Todos ordered by due date and priority
3. WHEN a User marks a Todo as completed, THE System SHALL update the Todo status and record the completion time
4. WHEN a User deletes a Todo, THE System SHALL remove the Todo from the System
5. THE System SHALL display overdue Todos with a visual indicator

### Requirement 7: Theo dõi tiến độ Sprint

**User Story:** Là một Project Manager, tôi muốn xem tiến độ của sprint hiện tại, để đánh giá khả năng hoàn thành mục tiêu sprint.

#### Acceptance Criteria

1. WHEN a Project Manager views a Sprint, THE System SHALL display the total number of Tasks in each Status category
2. WHEN a Project Manager views a Sprint, THE System SHALL calculate and display the percentage of completed Tasks
3. WHEN a Project Manager views a Sprint, THE System SHALL display the total estimated hours versus actual hours spent
4. WHEN a Project Manager views a Sprint, THE System SHALL display a burndown chart showing remaining work over time
5. WHEN a Sprint has blocked Tasks, THE System SHALL highlight them with a warning indicator

### Requirement 8: Tạo báo cáo Sprint

**User Story:** Là một Project Manager, tôi muốn tạo báo cáo tổng kết sprint, để đánh giá hiệu suất và chia sẻ với stakeholders.

#### Acceptance Criteria

1. WHEN a Project Manager generates a Sprint Report, THE System SHALL include the list of completed Tasks with actual hours
2. WHEN a Project Manager generates a Sprint Report, THE System SHALL include the list of incomplete Tasks with reasons
3. WHEN a Project Manager generates a Sprint Report, THE System SHALL calculate team velocity (completed story points or hours)
4. WHEN a Project Manager generates a Sprint Report, THE System SHALL include individual Team Member contributions
5. WHEN a Project Manager generates a Sprint Report, THE System SHALL allow exporting the Report to PDF format

### Requirement 9: Dashboard và thông báo

**User Story:** Là một User, tôi muốn xem tổng quan về công việc của mình trên dashboard, để nắm bắt nhanh các thông tin quan trọng.

#### Acceptance Criteria

1. WHEN a User accesses the dashboard, THE System SHALL display all Tasks assigned to the User grouped by Status
2. WHEN a User accesses the dashboard, THE System SHALL display upcoming deadlines for Tasks and Todos
3. WHEN a Task is assigned to a User, THE System SHALL create a notification for that User
4. WHEN a Task Status is changed, THE System SHALL notify the Project Manager and assigned Team Member
5. WHEN a User accesses the notification panel, THE System SHALL display all unread notifications ordered by time

### Requirement 10: Tìm kiếm và lọc

**User Story:** Là một User, tôi muốn tìm kiếm và lọc các task và project, để nhanh chóng tìm thấy thông tin cần thiết.

#### Acceptance Criteria

1. WHEN a User enters a search query, THE System SHALL search across Project names, Task titles, and descriptions
2. WHEN a User applies a Status filter, THE System SHALL display only Tasks matching the selected Status
3. WHEN a User applies a Priority filter, THE System SHALL display only Tasks matching the selected Priority
4. WHEN a User applies an assignee filter, THE System SHALL display only Tasks assigned to the selected Team Member
5. WHEN a User applies multiple filters, THE System SHALL display Tasks matching all filter criteria

### Requirement 11: Yêu cầu phi chức năng - Hiệu suất

**User Story:** Là một User, tôi muốn hệ thống phản hồi nhanh chóng, để có trải nghiệm sử dụng mượt mà.

#### Acceptance Criteria

1. WHEN a User performs any action, THE System SHALL respond within 2 seconds under normal load conditions
2. WHEN the System loads a dashboard with up to 100 Tasks, THE System SHALL complete rendering within 3 seconds
3. WHEN multiple Users access the System concurrently, THE System SHALL maintain response time within acceptable limits
4. WHEN the System performs database queries, THE System SHALL use appropriate indexes to optimize query performance
5. THE System SHALL cache frequently accessed data to reduce database load

### Requirement 12: Yêu cầu phi chức năng - Bảo mật và phân quyền

**User Story:** Là một Project Manager, tôi muốn kiểm soát quyền truy cập vào dự án, để đảm bảo bảo mật thông tin.

#### Acceptance Criteria

1. THE System SHALL implement role-based access control with roles: Admin, Project Manager, and Team Member
2. WHEN a User attempts to access a Project, THE System SHALL verify the User has appropriate permissions
3. WHEN a User attempts to modify a Task, THE System SHALL verify the User has permission to edit that Task
4. THE System SHALL log all security-related events including login attempts and permission changes
5. THE System SHALL implement session timeout after 30 minutes of inactivity

### Requirement 13: Yêu cầu phi chức năng - Khả năng mở rộng

**User Story:** Là một system architect, tôi muốn hệ thống có kiến trúc dễ mở rộng, để có thể thêm tính năng mới trong tương lai.

#### Acceptance Criteria

1. THE System SHALL implement a layered architecture separating presentation, business logic, and data access layers
2. THE System SHALL use design patterns (Repository, Service, Factory) to promote code reusability
3. THE System SHALL implement dependency injection to reduce coupling between components
4. THE System SHALL provide clear interfaces between modules to enable independent development
5. THE System SHALL follow SOLID principles in class design and implementation
