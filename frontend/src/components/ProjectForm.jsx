import React, { useState, useEffect } from 'react';
import api from '../services/api';
import '../style.css';

const ProjectForm = ({ project, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    projectPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // If editing existing project, populate form
    if (project) {
      setFormData({
        name: project.name || '',
        description: project.description || '',
        projectPassword: '' // Don't show existing password
      });
    }
  }, [project]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };



  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      if (project) {
        // Update existing project
        await api.put(`/projects/${project.id}`, formData);
      } else {
        // Create new project
        await api.post('/projects', formData);
      }
      
      if (onSuccess) {
        onSuccess();
      }
    } catch (err) {
      console.error('Error saving project:', err);
      setError(err.response?.data?.message || 'Failed to save project. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="project-form">
      <h2>{project ? 'Edit Project' : 'Create New Project'}</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="name">Project Name *</label>
          <input
            type="text"
            id="name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            maxLength={200}
            placeholder="Enter project name"
          />
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            maxLength={1000}
            rows={4}
            placeholder="Enter project description"
          />
        </div>

        <div className="form-group">
          <label htmlFor="projectPassword">
            Project Password {!project && <span className="required">*</span>}
          </label>
          <input
            type="password"
            id="projectPassword"
            name="projectPassword"
            value={formData.projectPassword}
            onChange={handleChange}
            required={!project}
            minLength={6}
            placeholder={project ? "Leave blank to keep current password" : "Enter project password (min 6 characters)"}
          />
          <small>
            {project 
              ? "Only fill this if you want to change the project password" 
              : "Anyone with this password can access the project"}
          </small>
        </div>

        <div className="form-actions">
          <button type="submit" disabled={loading} className="btn-primary">
            {loading ? 'Saving...' : (project ? 'Update Project' : 'Create Project')}
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

export default ProjectForm;
