import React from 'react';

const DeadlineWidget = ({ upcomingDeadlines, overdueTasks }) => {
  const formatDate = (dateString) => {
    if (!dateString) return 'No date';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric', 
      year: 'numeric' 
    });
  };

  const getDaysUntil = (dateString) => {
    if (!dateString) return null;
    const date = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    date.setHours(0, 0, 0, 0);
    const diffTime = date - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  const getPriorityColor = (priority) => {
    const colors = {
      'CRITICAL': '#dc3545',
      'HIGH': '#fd7e14',
      'MEDIUM': '#ffc107',
      'LOW': '#28a745'
    };
    return colors[priority] || '#6c757d';
  };

  return (
    <div className="deadline-widget">
      {/* Overdue Tasks */}
      {overdueTasks && overdueTasks.length > 0 && (
        <div className="deadline-section overdue-section">
          <h3 className="deadline-section-title overdue-title">
            ⚠️ Overdue Tasks ({overdueTasks.length})
          </h3>
          <div className="deadline-list">
            {overdueTasks.map((task) => (
              <div key={task.id} className="deadline-item overdue-item">
                <div className="deadline-item-header">
                  <span className="deadline-task-title">{task.title}</span>
                  <span 
                    className="deadline-priority-badge"
                    style={{ backgroundColor: getPriorityColor(task.priority) }}
                  >
                    {task.priority}
                  </span>
                </div>
                <div className="deadline-item-date">
                  Due: {formatDate(task.dueDate)} ({Math.abs(getDaysUntil(task.dueDate))} days ago)
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Upcoming Deadlines */}
      {upcomingDeadlines && upcomingDeadlines.length > 0 && (
        <div className="deadline-section upcoming-section">
          <h3 className="deadline-section-title upcoming-title">
            📅 Upcoming Deadlines ({upcomingDeadlines.length})
          </h3>
          <div className="deadline-list">
            {upcomingDeadlines.map((task) => {
              const daysUntil = getDaysUntil(task.dueDate);
              return (
                <div key={task.id} className="deadline-item upcoming-item">
                  <div className="deadline-item-header">
                    <span className="deadline-task-title">{task.title}</span>
                    <span 
                      className="deadline-priority-badge"
                      style={{ backgroundColor: getPriorityColor(task.priority) }}
                    >
                      {task.priority}
                    </span>
                  </div>
                  <div className="deadline-item-date">
                    Due: {formatDate(task.dueDate)}
                    {daysUntil !== null && (
                      <span className="deadline-days-until">
                        {daysUntil === 0 ? ' (Today)' : 
                         daysUntil === 1 ? ' (Tomorrow)' : 
                         ` (in ${daysUntil} days)`}
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* No deadlines message */}
      {(!overdueTasks || overdueTasks.length === 0) && 
       (!upcomingDeadlines || upcomingDeadlines.length === 0) && (
        <div className="no-deadlines">
          <p>✅ No upcoming or overdue tasks!</p>
        </div>
      )}
    </div>
  );
};

export default DeadlineWidget;
