import React from 'react';
import '../style.css';

const ProgressBar = ({ percentage, label, showPercentage = true }) => {
  // Ensure percentage is between 0 and 100
  const validPercentage = Math.min(100, Math.max(0, percentage || 0));

  const getProgressColor = (percentage) => {
    if (percentage < 30) {
      return 'linear-gradient(90deg, #dc3545, #c82333)'; // Red
    } else if (percentage < 70) {
      return 'linear-gradient(90deg, #ffc107, #ff9800)'; // Yellow/Orange
    } else {
      return 'linear-gradient(90deg, #28a745, #218838)'; // Green
    }
  };

  return (
    <div className="progress-section">
      {(label || showPercentage) && (
        <div className="progress-label">
          {label && <span>{label}</span>}
          {showPercentage && <span>{validPercentage.toFixed(0)}%</span>}
        </div>
      )}
      <div className="progress-bar-container">
        <div 
          className="progress-bar-fill" 
          style={{ 
            width: `${validPercentage}%`,
            background: getProgressColor(validPercentage)
          }}
        />
      </div>
    </div>
  );
};

export default ProgressBar;
