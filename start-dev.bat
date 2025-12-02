@echo off
echo Starting Task Management Development Environment...

REM Start Docker services
echo Starting MySQL database...
docker-compose up -d mysql

REM Wait for MySQL to be ready
echo Waiting for MySQL to be ready...
timeout /t 10 /nobreak

REM Start backend in new window
echo Starting Spring Boot backend...
start "Backend" cmd /k "cd backend && mvn spring-boot:run"

REM Wait for backend to start
timeout /t 15 /nobreak

REM Start frontend app in new window
echo Starting React frontend app...
start "Frontend" cmd /k "cd frontend && npm run dev"

echo Development environment started!
echo Backend: http://localhost:8080
echo Frontend: http://localhost:3000
pause
