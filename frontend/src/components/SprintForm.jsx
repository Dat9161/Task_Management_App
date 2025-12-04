import { useState, useEffect } from 'react';
import api from '../services/api';
import '../style.css';

const SprintForm = ({ sprint, projectId, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    projectId: projectId || '',
    startDate: '',
    endDate: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // If editing existing sprint, populate form
    if (sprint) {
      setFormData({
        name: sprint.name || '',
        projectId: sprint.projectId || projectId || '',
        startDate: sprint.startDate ? sprint.startDate.split('T')[0] : '',
        endDate: sprint.endDate ? sprint.endDate.split('T')[0] : ''
      });
    } else if (projectId) {
      setFormData(prev => ({
        ...prev,
        projectId: projectId
      }));
    }
  }, [sprint, projectId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validateDates = () => {
    if (!formData.startDate || !formData.endDate) {
      return 'Both start date and end date are required';
    }

    const start = new Date(formData.startDate);
    const end = new Date(formData.endDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (start < today && !sprint) {
      return 'Start date cannot be in the past';
    }

    if (end <= start) {
      return 'End date must be after start date';
    }

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Validate dates
    const dateError = validateDates();
    if (dateError) {
      setError(dateError);
      setLoading(false);
      return;
    }

    try {
      if (sprint) {
        // Update existing sprint
        await api.put(`/sprints/${sprint.id}`, formData);
      } else {
        // Create new sprint
        await api.post('/sprints', formData);
      }
      
      if (onSuccess) {
        onSuccess();
      }
    } catch (err) {
      console.error('Error saving sprint:', err);
      setError(err.response?.data?.message || 'Failed to save sprint. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="sprint-form">
      <h2>{sprint ? 'Edit Sprint' : 'Create New Sprint'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Sprint Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            maxLength={200}
            placeholder="Enter sprint name (e.g., Sprint 1, Q4 Sprint)"
          />
        </div>

        <div className="form-group">
          <label htmlFor="startDate">Start Date *</label>
          <input
            type="date"
            id="startDate"
            name="startDate"
            value={formData.startDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="endDate">End Date *</label>
          <input
            type="date"
            id="endDate"
            name="endDate"
            value={formData.endDate}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-actions">
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Saving...' : (sprint ? 'Update Sprint' : 'Create Sprint')}
          </button>
          {onCancel && (
            <button type="button" onClick={onCancel} className="btn-secondary">
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
};

export default SprintForm;
