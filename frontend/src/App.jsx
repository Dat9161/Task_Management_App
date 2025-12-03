import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import PublicRoute from './components/PublicRoute';

// Import pages
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProjectListPage from './pages/ProjectListPage';
import ProjectDetailPage from './pages/ProjectDetailPage';
import TaskListPage from './pages/TaskListPage';
import TaskDetailPage from './pages/TaskDetailPage';
import SprintListPage from './pages/SprintListPage';
import SprintDetailPage from './pages/SprintDetailPage';
import NotificationPage from './pages/NotificationPage';
import ReportPage from './pages/ReportPage';

import './App.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <div className="App">
          <Routes>
            {/* Public Routes */}
            <Route 
              path="/login" 
              element={
                <PublicRoute>
                  <LoginPage />
                </PublicRoute>
              } 
            />
            <Route 
              path="/register" 
              element={
                <PublicRoute>
                  <RegisterPage />
                </PublicRoute>
              } 
            />

            {/* Protected Routes */}
            <Route 
              path="/dashboard" 
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/projects" 
              element={
                <ProtectedRoute>
                  <ProjectListPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/projects/:id" 
              element={
                <ProtectedRoute>
                  <ProjectDetailPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/tasks" 
              element={
                <ProtectedRoute>
                  <TaskListPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/tasks/:id" 
              element={
                <ProtectedRoute>
                  <TaskDetailPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/sprints" 
              element={
                <ProtectedRoute>
                  <SprintListPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/sprints/:id" 
              element={
                <ProtectedRoute>
                  <SprintDetailPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/notifications" 
              element={
                <ProtectedRoute>
                  <NotificationPage />
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/reports" 
              element={
                <ProtectedRoute>
                  <ReportPage />
                </ProtectedRoute>
              } 
            />

            {/* Default redirect */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            
            {/* 404 - Not Found */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </div>
      </AuthProvider>
    </Router>
  );
}

export default App;
