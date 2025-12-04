import React from 'react';

const ErrorMessage = ({ title = 'Error', message, onRetry }) => {
  return (
    <div className="error-container">
      <div className="error-icon">⚠️</div>
      <h2 className="error-title">{title}</h2>
      <p className="error-message">{message}</p>
      {onRetry && (
        <div className="error-actions">
          <button className="btn-primary" onClick={onRetry}>
            Try Again
          </button>
        </div>
      )}
    </div>
  );
};

export default ErrorMessage;
