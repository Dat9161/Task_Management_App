import React from 'react';
import '../style.css';

const ProgressMetrics = ({ metrics }) => {
  if (!metrics) {
    return (
      <div className="progress-metrics">
        <p>No metrics available</p>
      </div>
    );
  }

  const getStatusBadgeClass = (status) => {
    const statusMap = {
      'TODO': 'status-todo',
      'IN_PROGRESS': 'status-in-progress',
      'DONE': 'status-done',
      'BLOCKED': 'status-blocked'
    };
    return statusMap[status] || 'status-default';
  };

  const getStatusColor = (status) => {
    const colorMap = {
      'TODO': '#6c757d',
      'IN_PROGRESS': '#007bff',
      'DONE': '#28a745',
      'BLOCKED': '#dc3545'
    };
    return colorMap[status] || '#6c757d';
  };

  return (
    <div className="progress-metrics">
      {/* Completion Percentage */}
      <div className="metric-card">
        <h3>Completion Percentage</h3>
        <div className="metric-value-large">
          {metrics.completionPercentage.toFixed(1)}%
        </div>
        <div className="progress-bar-container">
          <div 
            className="progress-bar-fill" 
            style={{ width: `${metrics.completionPercentage}%` }}
          />
        </div>
        <div className="metric-details">
          <span>{metrics.completedTasks} of {metrics.totalTasks} tasks completed</span>
        </div>
      </div>

      {/* Task Distribution by Status */}
      <div className="metric-card">
        <h3>Task Distribution by Status</h3>
        {metrics.statusDistribution && Object.keys(metrics.statusDistribution).length > 0 ? (
          <div className="status-distribution">
            {/* Pie chart representation using CSS */}
            <div className="distribution-chart">
              {Object.entries(metrics.statusDistribution).map(([status, count]) => {
                const percentage = (count / metrics.totalTasks) * 100;
                return (
                  <div key={status} className="chart-segment">
                    <div 
                      className="segment-bar"
                      style={{ 
                        width: `${percentage}%`,
                        backgroundColor: getStatusColor(status)
                      }}
                    />
                  </div>
                );
              })}
            </div>
            
            {/* Legend */}
            <div className="distribution-legend">
              {Object.entries(metrics.statusDistribution).map(([status, count]) => (
                <div key={status} className="legend-item">
                  <span 
                    className="legend-color"
                    style={{ backgroundColor: getStatusColor(status) }}
                  />
                  <span className={`badge ${getStatusBadgeClass(status)}`}>
                    {status.replace('_', ' ')}
                  </span>
                  <span className="legend-count">{count} tasks</span>
                  <span className="legend-percentage">
                    ({((count / metrics.totalTasks) * 100).toFixed(1)}%)
                  </span>
                </div>
              ))}
            </div>
          </div>
        ) : (
          <p>No task distribution data available</p>
        )}
      </div>

      {/* Velocity Metric */}
      <div className="metric-card">
        <h3>Velocity</h3>
        <div className="metric-value-large">
          {metrics.velocity || 0}
        </div>
        <p className="metric-description">Tasks completed in this sprint</p>
        {metrics.completedTasks > 0 && (
          <div className="velocity-details">
            <p className="metric-note">
              Average velocity helps predict future sprint capacity
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProgressMetrics;
