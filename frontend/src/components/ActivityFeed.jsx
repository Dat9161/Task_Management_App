import React from 'react';

const ActivityFeed = ({ recentActivity }) => {
  const getActivityIcon = (activity) => {
    if (!activity) return '📝';
    
    const activityLower = activity.toLowerCase();
    
    if (activityLower.includes('created')) return '✨';
    if (activityLower.includes('assigned')) return '👤';
    if (activityLower.includes('completed') || activityLower.includes('done')) return '✅';
    if (activityLower.includes('updated') || activityLower.includes('changed')) return '🔄';
    if (activityLower.includes('deleted')) return '🗑️';
    if (activityLower.includes('started')) return '🚀';
    if (activityLower.includes('blocked')) return '🚫';
    
    return '📝';
  };

  const getActivityTime = (activity) => {
    // Try to extract timestamp from activity string if present
    // For now, we'll just show the activity as-is since the backend
    // returns activity strings without timestamps
    return null;
  };

  return (
    <div className="activity-feed">
      {recentActivity && recentActivity.length > 0 ? (
        <div className="activity-list">
          {recentActivity.map((activity, index) => (
            <div key={index} className="activity-item">
              <div className="activity-icon">
                {getActivityIcon(activity)}
              </div>
              <div className="activity-content">
                <div className="activity-text">{activity}</div>
                {getActivityTime(activity) && (
                  <div className="activity-time">{getActivityTime(activity)}</div>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="no-activity">
          <p>No recent activity</p>
        </div>
      )}
    </div>
  );
};

export default ActivityFeed;
