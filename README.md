# Task & Project Management Tool

A full-stack task and project management application built with Spring Boot and React, following Agile/Scrum methodology.

## Technology Stack

### Backend
- Java 17
- Spring Boot 4.0.0
- Spring Web
- Spring Data JPA
- Spring Security
- MySQL 8.0
- Lombok
- JWT Authentication
- Maven

### Frontend
- React 18+
- Vite
- React Router
- Axios
- Material-UI
- JavaScript/JSX

## Project Structure

```
Task_Management_App/
├── backend/                    # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Java source files
│   │   │   └── resources/     # Configuration files
│   │   └── test/              # Test files
│   └── pom.xml                # Maven dependencies
│
├── frontend/                   # React frontend
│   ├── src/
│   │   ├── components/        # Reusable components
│   │   ├── pages/             # Page components
│   │   ├── services/          # API services
│   │   ├── utils/             # Utility functions
│   │   ├── contexts/          # React contexts
│   │   ├── hooks/             # Custom hooks
│   │   ├── App.jsx            # Main app component
│   │   └── main.jsx           # Entry point
│   ├── package.json           # NPM dependencies
│   └── vite.config.js         # Vite configuration
│
└── .kiro/specs/               # Feature specifications
```

## Prerequisites

- Java 17 or higher
- Node.js 18+ and npm
- MySQL 8.0
- Maven 3.6+

## Setup Instructions

### Database Setup

#### Option 1: Using Docker (Recommended)

Run MySQL in Docker container on port 3307:

```bash
docker run --name mysql-taskmanagement -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=task_management_db -p 3307:3306 -d mysql:8.0
```

To stop the container:
```bash
docker stop mysql-taskmanagement
```

To start the container again:
```bash
docker start mysql-taskmanagement
```

To remove the container:
```bash
docker rm -f mysql-taskmanagement
```

#### Option 2: Local MySQL Installation

1. Install MySQL 8.0
2. Create a database:
   ```sql
   CREATE DATABASE task_management_db;
   ```
3. Update database credentials in `backend/src/main/resources/application.properties` if needed

**Note:** The application is configured to use port 3307 by default. If you're using local MySQL on port 3306, update the port in `application.properties`.

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on `http://localhost:3000`

4. Build for production:
   ```bash
   npm run build
   ```

## Configuration

### Backend Configuration

The backend configuration is in `backend/src/main/resources/application.properties`:

- Server port: 8080
- Database URL: `jdbc:mysql://localhost:3307/task_management_db`
- Database username: root
- Database password: root (change for production!)
- JWT secret and expiration settings
- Hibernate DDL auto-update enabled

### Frontend Configuration

The frontend configuration is in `frontend/vite.config.js`:

- Development server port: 3000
- API proxy configured to forward `/api` requests to `http://localhost:8080`

## API Endpoints

The backend exposes REST API endpoints under `/api`:

- `/api/auth/*` - Authentication endpoints
- `/api/projects/*` - Project management
- `/api/sprints/*` - Sprint management
- `/api/tasks/*` - Task management
- `/api/notifications/*` - Notifications
- `/api/reports/*` - Reporting

## Features

- User authentication and authorization
- Project creation and management
- Sprint planning and tracking
- Task creation, assignment, and status management
- Progress tracking and reporting
- Search and filter functionality
- Notification system
- Dashboard with overview

## Development

### Running Tests

Backend tests:
```bash
cd backend
./mvnw test
```

Frontend tests:
```bash
cd frontend
npm test
```

### Code Style

- Backend: Follow Java coding conventions
- Frontend: Follow React best practices and ESLint rules

## License

This project is for educational purposes.
