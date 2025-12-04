import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import TaskForm from '../components/TaskForm';
import '../style.css';

const TaskDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [task, setTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);
  const [showAssignModal, setShowAssignModal] = useState(false);
  const [members, setMembers] = useState([]);
  const [selectedAssignee, setSelectedAssignee] = useState('');

  useEffect(() => {
    fetchTask();
  }, [id]);

  useEffect(() => {
    if (task && task.project) {
      fetchProjectMembers(task.project.id);
    }
  }, [task]);

  const fetchTask = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/tasks/${id}`);
      setTask(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching task:', err);
      setError('Failed to load task. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const fetchProjectMembers = async (projectId) => {
    try {
      const response = await api.get(`/projects/${projectId}`);
      setMembers(response.data.members || []);
    } catch (err) {
      console.error('Error fetching project members:', err);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this task?')) {
      return;
    }

    try {
      await api.delete(`/tasks/${id}`);
      navigate('/tasks');
    } catch (err) {
      console.error('Error deleting task:', err);
      setError('Failed to delete task. Please try again.');
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      await api.put(`/tasks/${id}/status`, { status: newStatus });
      fetchTask();
    } catch (err) {
      console.error('Error updating task status:', err);
      setError('Failed to update task status. Please try again.');
    }
  };

  const handleAssign = async () => {
    if (!selectedAssignee) {
      return;
    }

    try {
      await api.put(`/tasks/${id}/assign/${selectedAssignee}`);
      setShowAssignModal(false);
      setSelectedAssignee('');
      fetchTask();
    } catch (err) {
      console.error('Error assigning task:', err);
      setError('Failed to assign task. Please try again.');
    }
  };

  const handleEditSuccess = () => {
    setShowEditForm(false);
    fetchTask();
  };

  const handleEditCancel = () => {
    setShowEditForm(false);
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
    if (!dateString) return 'Not set';
    return new Date(dateString).toLocaleDateString();
  };

  const formatDateTime = (dateString) => {
    if (!dateString) return 'Not set';
    return new Date(dateString).toLocaleString();
  };

  const isProjectManager = () => {
    return task && task.project && task.project.manager && 
           user && task.project.manager.id === user.id;
  };

  const canEditTask = () => {
    return isProjectManager() || (task && task.assignee && user && task.assignee.id === user.id);
  };

  if (loading) {
    return (
      <div className="page-container">
        <h1>Task Details</h1>
        <p>Loading task...</p>
      </div>
    );
  }

  if (error && !task) {
    return (
      <div className="page-container">
        <h1>Task Details</h1>
        <div className="error-message">{error}</div>
        <button className="btn-secondary" onClick={() => navigate('/tasks')}>
          Back to Tasks
        </button>
      </div>
    );
  }

  if (!task) {
    return (
      <div className="page-container">
        <h1>Task Not Found</h1>
        <button className="btn-secondary" onClick={() => navigate('/tasks')}>
          Back to Tasks
        </button>
      </div>
    );
  }

  return (
    <div className="page-container">
      {error && (
        <div className="error-message">{error}</div>
      )}

      {showEditForm ? (
        <TaskForm
          task={task}
          onSuccess={handleEditSuccess}
          onCancel={handleEditCancel}
        />
      ) : (
        <>
          <div className="page-header">
            <div>
              <button className="btn-back" onClick={() => navigate('/tasks')}>
                ← Back to Tasks
              </button>
              <h1>{task.title}</h1>
            </div>
            <div className="header-actions">
              {canEditTask() && (
                <>
                  <button 
                    className="btn-primary" 
                    onClick={() => setShowEditForm(true)}
                  >
                    Edit
                  </button>
                  <button 
                    className="btn-danger" 
                    onClick={handleDelete}
                  >
                    Delete
                  </button>
                </>
              )}
            </div>
          </div>

          <div className="task-detail-container">
            <div className="task-detail-main">
              <div className="detail-section">
                <div className="detail-badges">
                  <span className={`badge ${getStatusBadgeClass(task.status)}`}>
                    {task.status?.replace('_', ' ')}
                  </span>
                  <span className={`badge ${getPriorityBadgeClass(task.priority)}`}>
                    {task.priority}
                  </span>
                </div>
              </div>

              <div className="detail-section">
                <h3>Description</h3>
                <p className="task-description-full">
                  {task.description || 'No description provided'}
                </p>
              </div>

              <div className="detail-section">
                <h3>Status Actions</h3>
                <div className="status-buttons">
                  {task.status !== 'TODO' && (
                    <button 
                      className="btn-status status-todo"
                      onClick={() => handleStatusChange('TODO')}
                    >
                      Mark as TODO
                    </button>
                  )}
                  {task.status !== 'IN_PROGRESS' && (
                    <button 
                      className="btn-status status-in-progress"
                      onClick={() => handleStatusChange('IN_PROGRESS')}
                    >
                      Mark as In Progress
                    </button>
                  )}
                  {task.status !== 'DONE' && (
                    <button 
                      className="btn-status status-done"
                      onClick={() => handleStatusChange('DONE')}
                    >
                      Mark as Done
                    </button>
                  )}
                  {task.status !== 'BLOCKED' && (
                    <button 
                      className="btn-status status-blocked"
                      onClick={() => handleStatusChange('BLOCKED')}
                    >
                      Mark as Blocked
                    </button>
                  )}
                </div>
              </div>
            </div>

            <div className="task-detail-sidebar">
              <div className="detail-section">
                <h3>Details</h3>
                <div className="detail-item">
                  <span className="detail-label">Project:</span>
                  <span className="detail-value">
                    {task.project?.name || 'N/A'}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Sprint:</span>
                  <span className="detail-value">
                    {task.sprint?.name || 'No sprint'}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Assignee:</span>
                  <span className="detail-value">
                    {task.assignee?.username || 'Unassigned'}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Due Date:</span>
                  <span className="detail-value">
                    {formatDate(task.dueDate)}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Created:</span>
                  <span className="detail-value">
                    {formatDateTime(task.createdAt)}
                  </span>
                </div>
                <div className="detail-item">
                  <span className="detail-label">Updated:</span>
                  <span className="detail-value">
                    {formatDateTime(task.updatedAt)}
                  </span>
                </div>
              </div>

              {isProjectManager() && (
                <div className="detail-section">
                  <h3>Assignment</h3>
                  <button 
                    className="btn-secondary btn-full-width"
                    onClick={() => setShowAssignModal(true)}
                  >
                    {task.assignee ? 'Reassign Task' : 'Assign Task'}
                  </button>
                </div>
              )}
            </div>
          </div>
        </>
      )}

      {showAssignModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2>{task.assignee ? 'Reassign Task' : 'Assign Task'}</h2>
            <div className="form-group">
              <label htmlFor="assignee-select">Select Assignee</label>
              <select
                id="assignee-select"
                className="filter-select"
                value={selectedAssignee}
                onChange={(e) => setSelectedAssignee(e.target.value)}
              >
                <option value="">Select a member</option>
                {members.map(member => (
                  <option key={member.id} value={member.id}>
                    {member.username}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-actions">
              <button 
                className="btn-primary"
                onClick={handleAssign}
                disabled={!selectedAssignee}
              >
                Assign
              </button>
              <button 
                className="btn-secondary"
                onClick={() => {
                  setShowAssignModal(false);
                  setSelectedAssignee('');
                }}
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskDetailPage;
