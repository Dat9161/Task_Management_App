# Hướng dẫn cài đặt nhanh

## Yêu cầu hệ thống

- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+
- Docker & Docker Compose (tùy chọn)

## Cài đặt nhanh với Docker

```bash
# Khởi động MySQL và Backend
docker-compose up -d

# Cài đặt và chạy Frontend
cd frontend
npm install
npm run dev
```

## Cài đặt thủ công

### 1. Cài đặt Backend

```bash
# Tạo database
mysql -u root -p
CREATE DATABASE task_management;

# Build và chạy backend
cd backend
mvn clean install
mvn spring-boot:run
```

Backend sẽ chạy tại: `http://localhost:8080`

### 2. Cài đặt Frontend

```bash
cd frontend
npm install

# Tạo file .env
cp .env.example .env

# Chạy development server
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:3000`

## Cấu trúc dự án

```
Task_Management_App/
├── backend/              # Spring Boot API
│   ├── src/main/java/com/taskmanagement/
│   │   ├── domain/      # Entities
│   │   ├── repository/  # Data access
│   │   ├── service/     # Business logic
│   │   ├── controller/  # REST API
│   │   ├── dto/         # Data transfer objects
│   │   ├── exception/   # Custom exceptions
│   │   └── config/      # Configuration
│   └── pom.xml
│
├── frontend/            # React Web App
│   ├── src/
│   │   ├── pages/       # Page components
│   │   ├── components/  # Reusable components
│   │   ├── store/       # Redux state
│   │   ├── services/    # API calls
│   │   ├── config/      # Configuration
│   │   └── utils/       # Utilities
│   └── package.json
│
└── docker-compose.yml   # Docker orchestration
```

## Công nghệ sử dụng

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security + JWT
- MySQL 8.0
- JQwik (Property-based testing)
- JUnit 5

### Frontend
- React 18
- Redux Toolkit
- React Router v6
- Material-UI (MUI)
- Axios
- Vite
- Formik + Yup

## Scripts hữu ích

### Backend
```bash
mvn clean install          # Build project
mvn spring-boot:run        # Run application
mvn test                   # Run tests
```

### Frontend
```bash
npm run dev                # Development server
npm run build              # Production build
npm run preview            # Preview production build
npm run lint               # Lint code
```

## Endpoints API

Backend API sẽ có các endpoints sau (sẽ được implement trong các task tiếp theo):

- `/api/auth/*` - Authentication
- `/api/projects/*` - Project management
- `/api/sprints/*` - Sprint management
- `/api/tasks/*` - Task management
- `/api/todos/*` - Todo management
- `/api/reports/*` - Reports
- `/api/dashboard/*` - Dashboard data

## Troubleshooting

### Backend không kết nối được MySQL
- Kiểm tra MySQL đã chạy: `mysql -u root -p`
- Kiểm tra credentials trong `application.properties`
- Nếu dùng Docker: `docker-compose logs mysql`

### Frontend không gọi được API
- Kiểm tra backend đã chạy tại port 8080
- Kiểm tra CORS configuration
- Kiểm tra `.env` file có đúng API URL

### Port đã được sử dụng
- Backend (8080): Đổi port trong `application.properties`
- Frontend (3000): Đổi port trong `vite.config.js`
- MySQL (3307): Đã đổi từ 3306 sang 3307 để tránh conflict với container khác

**Lưu ý**: MySQL container sử dụng port **3307** (thay vì 3306 mặc định) để tránh xung đột với các container MySQL khác đang chạy.
