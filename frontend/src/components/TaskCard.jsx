import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../style.css';

const TaskCard = ({ task }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/tasks/${task.id}`);
  };

  const getStatusBadgeClass = (status) => {
    const statusMap = {
      'TODO': 'status-todo',
      'IN_PROGRESS': 'status-in-progress',
      'DONE': 'status-done',
      'BLOCKED': 'status-blocked'
    };
    return statusMap[status] || 'status-default';
  };

  const getPriorityBadgeClass = (priority) => {
    const priorityMap = {
      'LOW': 'priority-low',
      'MEDIUM': 'priority-medium',
      'HIGH': 'priority-high',
      'CRITICAL': 'priority-critical'
    };
    return priorityMap[priority] || 'priority-default';
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'No due date';
    const date = new Date(dateString);
    const today = new Date();
    const isOverdue = date < today && task.status !== 'DONE';
    
    return (
      <span className={isOverdue ? 'date-overdue' : 'date-normal'}>
        📅 {date.toLocaleDateString()}
        {isOverdue && ' (Overdue)'}
      </span>
    );
  };

  return (
    <div className="task-card" onClick={handleClick}>
      <div className="task-card-header">
        <h3 className="task-title">{task.title}</h3>
        <div className="task-badges">
          <span className={`badge ${getStatusBadgeClass(task.status)}`}>
            {task.status?.replace('_', ' ')}
          </span>
          <span className={`badge ${getPriorityBadgeClass(task.priority)}`}>
            {task.priority}
          </span>
        </div>
      </div>
      
      <div className="task-card-body">
        {task.description && (
          <p className="task-description">
            {task.description.length > 100 
              ? `${task.description.substring(0, 100)}...` 
              : task.description}
          </p>
        )}
        
        <div className="task-card-footer">
          <div className="task-meta">
            {task.assignee ? (
              <span className="task-assignee">
                👤 {task.assignee.username}
              </span>
            ) : (
              <span className="task-assignee unassigned">
                👤 Unassigned
              </span>
            )}
            <span className="task-due-date">
              {formatDate(task.dueDate)}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TaskCard;
