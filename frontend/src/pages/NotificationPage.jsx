import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import NotificationItem from '../components/NotificationItem';
import '../style.css';

const NotificationPage = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('all'); // 'all' or 'unread'

  useEffect(() => {
    fetchNotifications();
  }, [user, filter]);

  const fetchNotifications = async () => {
    if (!user || !user.id) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const endpoint = filter === 'unread' 
        ? `/notifications/unread/${user.id}`
        : `/notifications/user/${user.id}`;
      
      const response = await api.get(endpoint);
      setNotifications(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching notifications:', err);
      setError('Failed to load notifications. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (notificationId) => {
    try {
      await api.put(`/notifications/${notificationId}/read`);
      // Update the notification in the local state
      setNotifications(notifications.map(notif => 
        notif.id === notificationId ? { ...notif, read: true } : notif
      ));
    } catch (err) {
      console.error('Error marking notification as read:', err);
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      // Mark all unread notifications as read
      const unreadNotifications = notifications.filter(n => !n.read);
      await Promise.all(
        unreadNotifications.map(notif => 
          api.put(`/notifications/${notif.id}/read`)
        )
      );
      // Refresh notifications
      fetchNotifications();
    } catch (err) {
      console.error('Error marking all notifications as read:', err);
    }
  };



  if (loading) {
    return (
      <div className="page-container">
        <h1>Notifications</h1>
        <p>Loading notifications...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1>Notifications</h1>
        <div className="error-message">{error}</div>
      </div>
    );
  }

  const unreadCount = notifications.filter(n => !n.read).length;

  return (
    <div className="page-container">
      <div className="page-header">
        <h1>Notifications</h1>
        <div className="button-group">
          {unreadCount > 0 && (
            <button className="btn-primary" onClick={handleMarkAllAsRead}>
              Mark All as Read
            </button>
          )}
        </div>
      </div>

      <div className="notification-filters">
        <button 
          className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => setFilter('all')}
        >
          All ({notifications.length})
        </button>
        <button 
          className={`filter-btn ${filter === 'unread' ? 'active' : ''}`}
          onClick={() => setFilter('unread')}
        >
          Unread ({unreadCount})
        </button>
      </div>

      {notifications.length === 0 ? (
        <div className="empty-state">
          <p>
            {filter === 'unread' 
              ? 'No unread notifications' 
              : 'No notifications yet'}
          </p>
        </div>
      ) : (
        <div className="notifications-list">
          {notifications.map((notification) => (
            <NotificationItem
              key={notification.id}
              notification={notification}
              onMarkAsRead={handleMarkAsRead}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default NotificationPage;
