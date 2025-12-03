import React from 'react';

const TaskSummaryCard = ({ tasksByStatus }) => {
  if (!tasksByStatus) {
    return <p>No tasks available.</p>;
  }

  const statusLabels = {
    'TODO': 'To Do',
    'IN_PROGRESS': 'In Progress',
    'DONE': 'Done',
    'BLOCKED': 'Blocked'
  };

  const statusColors = {
    'TODO': '#6c757d',
    'IN_PROGRESS': '#007bff',
    'DONE': '#28a745',
    'BLOCKED': '#dc3545'
  };

  const getTotalTasks = () => {
    return Object.values(tasksByStatus).reduce((total, tasks) => total + tasks.length, 0);
  };

  return (
    <div className="task-summary-card">
      <div className="task-summary-total">
        <h3>Total Tasks: {getTotalTasks()}</h3>
      </div>
      
      <div className="task-summary-grid">
        {Object.entries(tasksByStatus).map(([status, tasks]) => (
          <div 
            key={status} 
            className="task-summary-item"
            style={{ borderLeftColor: statusColors[status] }}
          >
            <div className="task-summary-label">{statusLabels[status] || status}</div>
            <div className="task-summary-count">{tasks.length}</div>
            {tasks.length > 0 && (
              <div className="task-summary-list">
                {tasks.slice(0, 3).map((task) => (
                  <div key={task.id} className="task-summary-task-item">
                    • {task.title}
                  </div>
                ))}
                {tasks.length > 3 && (
                  <div className="task-summary-more">
                    +{tasks.length - 3} more
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default TaskSummaryCard;
