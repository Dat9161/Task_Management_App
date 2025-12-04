import React, { useState, useEffect } from 'react';

const Alert = ({ type = 'info', message, onClose, autoClose = true, duration = 5000 }) => {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    if (autoClose) {
      const timer = setTimeout(() => {
        handleClose();
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [autoClose, duration]);

  const handleClose = () => {
    setVisible(false);
    if (onClose) {
      setTimeout(onClose, 300); // Wait for animation
    }
  };

  if (!visible) return null;

  const icons = {
    success: '✓',
    error: '✕',
    warning: '⚠',
    info: 'ℹ'
  };

  return (
    <div className={`alert alert-${type}`}>
      <span className="alert-icon">{icons[type]}</span>
      <div className="alert-content">{message}</div>
      <button className="alert-close" onClick={handleClose}>
        ×
      </button>
    </div>
  );
};

export default Alert;
