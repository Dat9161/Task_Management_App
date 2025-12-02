# Task & Project Management Tool

A comprehensive task and project management application built with Spring Boot (backend) and React (frontend web app).

## Project Structure

```
.
├── backend/                 # Spring Boot backend application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/taskmanagement/
│   │   │   │   ├── domain/          # Domain entities
│   │   │   │   ├── repository/      # Data access layer
│   │   │   │   ├── service/         # Business logic layer
│   │   │   │   ├── controller/      # REST API controllers
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── exception/       # Custom exceptions
│   │   │   │   └── config/          # Configuration classes
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                    # Test classes
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # React web application
│   ├── src/
│   │   ├── config/          # API configuration
│   │   ├── store/           # Redux store and slices
│   │   ├── pages/           # Page components
│   │   └── components/      # Reusable components
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
└── docker-compose.yml       # Docker orchestration

```

## Prerequisites

### Backend
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Docker and Docker Compose (optional)

### Frontend
- Node.js 18+
- npm or yarn

## Setup Instructions

### Backend Setup

#### Option 1: Using Docker Compose (Recommended)

1. Start the services:
```bash
docker-compose up -d
```

This will start both MySQL and the Spring Boot backend.

#### Option 2: Manual Setup

1. Install and start MySQL:
```bash
# Create database
mysql -u root -p
CREATE DATABASE task_management;
```

**Note**: If using Docker, MySQL runs on port **3307** (not 3306) to avoid conflicts with other MySQL containers.

2. Update database credentials in `backend/src/main/resources/application.properties` if needed

3. Build and run the backend:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`

### Frontend Setup

1. Install dependencies:
```bash
cd frontend
npm install
```

2. Create environment file:
```bash
cp .env.example .env
```

3. Update API base URL in `.env` if needed (default: `http://localhost:8080/api`)

4. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

## Architecture

The application follows a layered architecture pattern:

### Backend Layers
1. **Presentation Layer**: REST API controllers
2. **Application Layer**: Business logic services
3. **Domain Layer**: Entities and business rules
4. **Infrastructure Layer**: Repositories and data access

### Frontend Architecture
- **Redux Toolkit**: State management
- **React Router**: Client-side routing
- **Axios**: HTTP client for API calls
- **Material-UI**: UI component library
- **Vite**: Build tool and dev server

## Technology Stack

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- MySQL 8.0
- JWT for authentication
- JQwik for property-based testing
- JUnit 5 for unit testing

### Frontend
- React 18
- Redux Toolkit
- React Router v6
- Material-UI (MUI)
- Axios
- Vite
- Formik & Yup for form validation
- Recharts for data visualization

## Development

### Backend Development
```bash
cd backend
mvn spring-boot:run
```

### Frontend Development
```bash
cd frontend
npm run dev
```

## Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## API Documentation

Once the backend is running, API documentation will be available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html` (to be configured)

## Environment Variables

### Backend
Configure in `application.properties`:
- `spring.datasource.url`: Database connection URL
- `spring.datasource.username`: Database username
- `spring.datasource.password`: Database password
- `jwt.secret`: JWT secret key
- `jwt.expiration`: JWT token expiration time

### Frontend
Configure in `frontend/.env`:
- `VITE_API_BASE_URL`: Backend API base URL

## License

This project is licensed under the MIT License.
