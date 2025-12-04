import React, { useState, useEffect } from 'react';
import api from '../services/api';
import '../style.css';

const TaskForm = ({ task, projectId, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    priority: 'MEDIUM',
    dueDate: '',
    projectId: projectId || '',
    sprintId: '',
    assigneeId: ''
  });

  const [projects, setProjects] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [members, setMembers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [validationErrors, setValidationErrors] = useState({});

  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title || '',
        description: task.description || '',
        priority: task.priority || 'MEDIUM',
        dueDate: task.dueDate ? task.dueDate.split('T')[0] : '',
        projectId: task.project?.id || projectId || '',
        sprintId: task.sprint?.id || '',
        assigneeId: task.assignee?.id || ''
      });
    }
  }, [task, projectId]);

  useEffect(() => {
    fetchProjects();
  }, []);

  useEffect(() => {
    if (formData.projectId) {
      fetchSprintsAndMembers(formData.projectId);
    }
  }, [formData.projectId]);

  const fetchProjects = async () => {
    try {
      const userStr = localStorage.getItem('user');
      if (!userStr) return;
      
      const user = JSON.parse(userStr);
      const response = await api.get(`/projects/user/${user.id}`);
      setProjects(response.data);
    } catch (err) {
      console.error('Error fetching projects:', err);
    }
  };

  const fetchSprintsAndMembers = async (projectId) => {
    try {
      const [sprintsResponse, projectResponse] = await Promise.all([
        api.get(`/projects/${projectId}/sprints`),
        api.get(`/projects/${projectId}`)
      ]);
      
      setSprints(sprintsResponse.data);
      setMembers(projectResponse.data.members || []);
    } catch (err) {
      console.error('Error fetching sprints and members:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear validation error for this field
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const validateForm = () => {
    const errors = {};
    
    if (!formData.title || formData.title.trim().length === 0) {
      errors.title = 'Title is required';
    } else if (formData.title.length > 200) {
      errors.title = 'Title must be 200 characters or less';
    }
    
    if (formData.description && formData.description.length > 2000) {
      errors.description = 'Description must be 2000 characters or less';
    }
    
    if (!formData.projectId) {
      errors.projectId = 'Project is required';
    }
    
    if (formData.dueDate) {
      const dueDate = new Date(formData.dueDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      if (dueDate < today && !task) {
        errors.dueDate = 'Due date cannot be in the past';
      }
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }
    
    setLoading(true);
    setError(null);
    
    try {
      const payload = {
        title: formData.title.trim(),
        description: formData.description.trim(),
        priority: formData.priority,
        projectId: parseInt(formData.projectId),
        sprintId: formData.sprintId ? parseInt(formData.sprintId) : null,
        assigneeId: formData.assigneeId ? parseInt(formData.assigneeId) : null,
        dueDate: formData.dueDate || null
      };
      
      if (task) {
        // Update existing task
        await api.put(`/tasks/${task.id}`, payload);
      } else {
        // Create new task
        await api.post('/tasks', payload);
      }
      
      onSuccess();
    } catch (err) {
      console.error('Error saving task:', err);
      setError(err.response?.data?.message || 'Failed to save task. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <h2>{task ? 'Edit Task' : 'Create New Task'}</h2>
      
      {error && (
        <div className="error-message">{error}</div>
      )}
      
      <form onSubmit={handleSubmit} className="task-form">
        <div className="form-group">
          <label htmlFor="title">
            Title <span className="required">*</span>
          </label>
          <input
            type="text"
            id="title"
            name="title"
            className={validationErrors.title ? 'input-error' : ''}
            value={formData.title}
            onChange={handleChange}
            maxLength={200}
            required
          />
          {validationErrors.title && (
            <span className="error-text">{validationErrors.title}</span>
          )}
        </div>

        <div className="form-group">
          <label htmlFor="description">Description</label>
          <textarea
            id="description"
            name="description"
            className={validationErrors.description ? 'input-error' : ''}
            value={formData.description}
            onChange={handleChange}
            rows={4}
            maxLength={2000}
          />
          {validationErrors.description && (
            <span className="error-text">{validationErrors.description}</span>
          )}
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="priority">
              Priority <span className="required">*</span>
            </label>
            <select
              id="priority"
              name="priority"
              value={formData.priority}
              onChange={handleChange}
              required
            >
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
              <option value="CRITICAL">Critical</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="dueDate">Due Date</label>
            <input
              type="date"
              id="dueDate"
              name="dueDate"
              className={validationErrors.dueDate ? 'input-error' : ''}
              value={formData.dueDate}
              onChange={handleChange}
            />
            {validationErrors.dueDate && (
              <span className="error-text">{validationErrors.dueDate}</span>
            )}
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="projectId">
            Project <span className="required">*</span>
          </label>
          <select
            id="projectId"
            name="projectId"
            className={validationErrors.projectId ? 'input-error' : ''}
            value={formData.projectId}
            onChange={handleChange}
            disabled={!!projectId || !!task}
            required
          >
            <option value="">Select a project</option>
            {projects.map(project => (
              <option key={project.id} value={project.id}>
                {project.name}
              </option>
            ))}
          </select>
          {validationErrors.projectId && (
            <span className="error-text">{validationErrors.projectId}</span>
          )}
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="sprintId">Sprint</label>
            <select
              id="sprintId"
              name="sprintId"
              value={formData.sprintId}
              onChange={handleChange}
              disabled={!formData.projectId}
            >
              <option value="">No sprint</option>
              {sprints.map(sprint => (
                <option key={sprint.id} value={sprint.id}>
                  {sprint.name}
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="assigneeId">Assignee</label>
            <select
              id="assigneeId"
              name="assigneeId"
              value={formData.assigneeId}
              onChange={handleChange}
              disabled={!formData.projectId}
            >
              <option value="">Unassigned</option>
              {members.map(member => (
                <option key={member.id} value={member.id}>
                  {member.username}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="form-actions">
          <button 
            type="submit" 
            className="btn-primary"
            disabled={loading}
          >
            {loading ? 'Saving...' : (task ? 'Update Task' : 'Create Task')}
          </button>
          <button 
            type="button" 
            className="btn-secondary"
            onClick={onCancel}
            disabled={loading}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default TaskForm;
