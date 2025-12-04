import React from 'react';
import '../style.css';

const PriorityBadge = ({ priority }) => {
  const getPriorityClass = (priority) => {
    const priorityMap = {
      'LOW': 'priority-low',
      'MEDIUM': 'priority-medium',
      'HIGH': 'priority-high',
      'CRITICAL': 'priority-critical'
    };
    return priorityMap[priority] || 'priority-low';
  };

  const formatPriority = (priority) => {
    if (!priority) return 'Low';
    return priority.charAt(0).toUpperCase() + priority.slice(1).toLowerCase();
  };

  return (
    <span className={`badge ${getPriorityClass(priority)}`}>
      {formatPriority(priority)}
    </span>
  );
};

export default PriorityBadge;
