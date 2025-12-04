import React from 'react';
import '../style.css';

const StatusBadge = ({ status }) => {
  const getStatusClass = (status) => {
    const statusMap = {
      'TODO': 'status-todo',
      'IN_PROGRESS': 'status-in-progress',
      'DONE': 'status-done',
      'BLOCKED': 'status-blocked',
      'PLANNED': 'status-planned',
      'ACTIVE': 'status-active',
      'COMPLETED': 'status-completed'
    };
    return statusMap[status] || 'status-default';
  };

  const formatStatus = (status) => {
    if (!status) return 'Unknown';
    return status.replace(/_/g, ' ');
  };

  return (
    <span className={`badge ${getStatusClass(status)}`}>
      {formatStatus(status)}
    </span>
  );
};

export default StatusBadge;
