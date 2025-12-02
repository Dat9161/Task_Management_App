#!/bin/bash

echo "Starting Task Management Development Environment..."

# Start Docker services
echo "Starting MySQL database..."
docker-compose up -d mysql

# Wait for MySQL to be ready
echo "Waiting for MySQL to be ready..."
sleep 10

# Start backend
echo "Starting Spring Boot backend..."
cd backend
mvn spring-boot:run &
BACKEND_PID=$!

# Wait for backend to start
sleep 15

# Start frontend app
echo "Starting React frontend app..."
cd ../frontend
npm run dev &
FRONTEND_PID=$!

echo "Development environment started!"
echo "Backend PID: $BACKEND_PID"
echo "Frontend PID: $FRONTEND_PID"
echo ""
echo "Backend: http://localhost:8080"
echo "Frontend: http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for user interrupt
wait
