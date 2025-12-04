import React from 'react';
import '../style.css';

const NotificationItem = ({ notification, onMarkAsRead }) => {
  const getNotificationIcon = (type) => {
    switch (type) {
      case 'TASK_ASSIGNED':
        return '📋';
      case 'DEADLINE_REMINDER':
        return '⏰';
      case 'STATUS_CHANGED':
        return '🔄';
      default:
        return '📢';
    }
  };

  const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    const now = new Date();
    const diffMs = now - date;
    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMs / 3600000);
    const diffDays = Math.floor(diffMs / 86400000);

    if (diffMins < 1) return 'Just now';
    if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`;
    if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
    if (diffDays < 7) return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    
    return date.toLocaleDateString();
  };

  return (
    <div className={`notification-item ${!notification.read ? 'unread' : ''}`}>
      <div className="notification-icon">
        {getNotificationIcon(notification.type)}
      </div>
      <div className="notification-content">
        <div className="notification-message">
          {notification.message}
        </div>
        <div className="notification-time">
          {formatTimestamp(notification.createdAt)}
        </div>
      </div>
      {!notification.read && (
        <button 
          className="btn-mark-read"
          onClick={() => onMarkAsRead(notification.id)}
          title="Mark as read"
        >
          ✓
        </button>
      )}
    </div>
  );
};

export default NotificationItem;
