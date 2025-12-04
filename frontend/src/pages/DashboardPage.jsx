import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import TaskSummaryCard from '../components/TaskSummaryCard';
import DeadlineWidget from '../components/DeadlineWidget';
import ProjectListWidget from '../components/ProjectListWidget';
import ActivityFeed from '../components/ActivityFeed';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import '../style.css';

const DashboardPage = () => {
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      if (!user || !user.id) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await api.get(`/dashboard/user/${user.id}`);
        setDashboardData(response.data);
        setError(null);
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError('Failed to load dashboard data. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, [user]);

  const handleRetry = () => {
    setLoading(true);
    setError(null);
    if (user && user.id) {
      api.get(`/dashboard/user/${user.id}`)
        .then(response => {
          setDashboardData(response.data);
          setError(null);
        })
        .catch(err => {
          console.error('Error fetching dashboard data:', err);
          setError('Failed to load dashboard data. Please try again later.');
        })
        .finally(() => setLoading(false));
    }
  };

  if (loading) {
    return <Loading message="Loading your dashboard..." />;
  }

  if (error) {
    return (
      <ErrorMessage 
        title="Dashboard Error"
        message={error}
        onRetry={handleRetry}
      />
    );
  }

  if (!dashboardData) {
    return (
      <div className="dashboard-container">
        <h1>Dashboard</h1>
        <p>No dashboard data available.</p>
      </div>
    );
  }

  return (
    <div className="dashboard-container fade-in">
      <h1>Dashboard</h1>
      <p>Welcome back, {user?.username}!</p>
      
      <div className="dashboard-grid">
        {/* Task Summary Section */}
        <div className="dashboard-section">
          <h2>Task Summary</h2>
          <TaskSummaryCard tasksByStatus={dashboardData.tasksByStatus} />
        </div>

        {/* Deadlines Section */}
        <div className="dashboard-section">
          <h2>Deadlines</h2>
          <DeadlineWidget 
            upcomingDeadlines={dashboardData.upcomingDeadlines}
            overdueTasks={dashboardData.overdueTasks}
          />
        </div>

        {/* Active Projects Section */}
        <div className="dashboard-section">
          <h2>Active Projects & Sprints</h2>
          <ProjectListWidget 
            activeProjects={dashboardData.activeProjects}
            currentSprints={dashboardData.currentSprints}
          />
        </div>

        {/* Recent Activity Section */}
        <div className="dashboard-section">
          <h2>Recent Activity</h2>
          <ActivityFeed recentActivity={dashboardData.recentActivity} />
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
