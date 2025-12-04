import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import '../style.css';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    if (user && user.id) {
      fetchUnreadCount();
      // Poll for new notifications every 30 seconds
      const interval = setInterval(fetchUnreadCount, 30000);
      return () => clearInterval(interval);
    }
  }, [user]);

  const fetchUnreadCount = async () => {
    try {
      const response = await api.get(`/notifications/unread/${user.id}`);
      setUnreadCount(response.data.length);
    } catch (err) {
      console.error('Error fetching unread notification count:', err);
    }
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const handleNotificationClick = () => {
    navigate('/notifications');
  };

  if (!user) {
    return null;
  }

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to="/dashboard" className="navbar-logo">
            📋 Task Manager
          </Link>
        </div>

        <div className="navbar-menu">
          <Link to="/dashboard" className="navbar-link">
            Dashboard
          </Link>
          <Link to="/projects" className="navbar-link">
            Projects
          </Link>
          <Link to="/tasks" className="navbar-link">
            Tasks
          </Link>
          <Link to="/sprints" className="navbar-link">
            Sprints
          </Link>
          <Link to="/reports" className="navbar-link">
            Reports
          </Link>
        </div>

        <div className="navbar-actions">
          <div 
            className="notification-badge" 
            onClick={handleNotificationClick}
            title="Notifications"
          >
            <span className="notification-badge-icon">🔔</span>
            {unreadCount > 0 && (
              <span className="notification-badge-count">{unreadCount}</span>
            )}
          </div>

          <div className="navbar-user">
            <span className="navbar-username">{user.username}</span>
            <button className="btn-logout" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
