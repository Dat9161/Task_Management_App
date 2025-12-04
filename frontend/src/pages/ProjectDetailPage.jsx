import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import ProjectForm from '../components/ProjectForm';
import '../style.css';

const ProjectDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [project, setProject] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [sprints, setSprints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);

  const isProjectManager = project && user && project.managerId === user.id;

  const fetchProjectDetails = async () => {
    try {
      setLoading(true);
      
      // Fetch project details
      const projectResponse = await api.get(`/projects/${id}`);
      setProject(projectResponse.data);

      // Fetch project tasks
      const tasksResponse = await api.get(`/projects/${id}/tasks`);
      setTasks(tasksResponse.data);

      // Fetch project sprints
      const sprintsResponse = await api.get(`/projects/${id}/sprints`);
      setSprints(sprintsResponse.data);

      setError(null);
    } catch (err) {
      console.error('Error fetching project details:', err);
      setError('Failed to load project details. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjectDetails();
  }, [id]);

  const handleEditSuccess = () => {
    setShowEditForm(false);
    fetchProjectDetails();
  };

  const handleEditCancel = () => {
    setShowEditForm(false);
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
      return;
    }

    try {
      await api.delete(`/projects/${id}`);
      navigate('/projects');
    } catch (err) {
      console.error('Error deleting project:', err);
      alert('Failed to delete project. Please try again.');
    }
  };

  const getStatusBadgeClass = (status) => {
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

  if (loading) {
    return (
      <div className="page-container">
        <h1>Project Details</h1>
        <p>Loading project details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1>Project Details</h1>
        <div className="error-message">{error}</div>
        <button className="btn-secondary" onClick={() => navigate('/projects')}>
          Back to Projects
        </button>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="page-container">
        <h1>Project Details</h1>
        <p>Project not found.</p>
        <button className="btn-secondary" onClick={() => navigate('/projects')}>
          Back to Projects
        </button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <button className="btn-secondary" onClick={() => navigate('/projects')}>
            ← Back to Projects
          </button>
          <h1>{project.name}</h1>
        </div>
        {isProjectManager && (
          <div className="button-group">
            <button className="btn-primary" onClick={() => setShowEditForm(true)}>
              Edit Project
            </button>
            <button className="btn-danger" onClick={handleDelete}>
              Delete Project
            </button>
          </div>
        )}
      </div>

      {showEditForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <ProjectForm 
              project={project}
              onSuccess={handleEditSuccess}
              onCancel={handleEditCancel}
            />
          </div>
        </div>
      )}

      <div className="project-details">
        {/* Project Dashboard */}
        <section className="detail-section dashboard-section">
          <h2>📊 Project Dashboard</h2>
          <div className="dashboard-metrics">
            <div className="metric-card">
              <div className="metric-value">{tasks.length}</div>
              <div className="metric-label">Total Tasks</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{tasks.filter(t => t.status === 'DONE').length}</div>
              <div className="metric-label">Completed</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{tasks.filter(t => t.status === 'IN_PROGRESS').length}</div>
              <div className="metric-label">In Progress</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{tasks.filter(t => t.status === 'TODO').length}</div>
              <div className="metric-label">To Do</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{sprints.length}</div>
              <div className="metric-label">Total Sprints</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{sprints.filter(s => s.status === 'ACTIVE').length}</div>
              <div className="metric-label">Active Sprints</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">{project.members?.length || 0}</div>
              <div className="metric-label">Team Members</div>
            </div>
            <div className="metric-card">
              <div className="metric-value">
                {tasks.length > 0 ? Math.round((tasks.filter(t => t.status === 'DONE').length / tasks.length) * 100) : 0}%
              </div>
              <div className="metric-label">Completion Rate</div>
            </div>
          </div>
        </section>

        {/* Project Information */}
        <section className="detail-section">
          <h2>Project Information</h2>
          <div className="detail-content">
            <p><strong>Description:</strong> {project.description || 'No description provided'}</p>
            <p><strong>Manager:</strong> {project.managerName || 'Unknown'}</p>
            <p><strong>Created:</strong> {project.createdAt ? new Date(project.createdAt).toLocaleDateString() : 'Unknown'}</p>
          </div>
        </section>

        {/* Project Members */}
        <section className="detail-section">
          <h2>Project Members ({project.members?.length || 0})</h2>
          <div className="members-list">
            {project.members && project.members.length > 0 ? (
              <ul>
                {project.members.map(member => (
                  <li key={member.id}>
                    {member.username} ({member.email})
                    {member.id === project.managerId && <span className="badge">Manager</span>}
                  </li>
                ))}
              </ul>
            ) : (
              <p>No members assigned to this project.</p>
            )}
          </div>
        </section>

        {/* Project Sprints */}
        <section className="detail-section">
          <h2>Sprints ({sprints.length})</h2>
          <div className="sprints-list">
            {sprints.length > 0 ? (
              <div className="items-grid">
                {sprints.map(sprint => (
                  <div 
                    key={sprint.id} 
                    className="item-card"
                    onClick={() => navigate(`/sprints/${sprint.id}`)}
                  >
                    <h3>{sprint.name}</h3>
                    <p>
                      {sprint.startDate ? new Date(sprint.startDate).toLocaleDateString() : 'N/A'} - 
                      {sprint.endDate ? new Date(sprint.endDate).toLocaleDateString() : 'N/A'}
                    </p>
                    <span className={`badge ${getStatusBadgeClass(sprint.status)}`}>
                      {sprint.status}
                    </span>
                  </div>
                ))}
              </div>
            ) : (
              <p>No sprints created for this project yet.</p>
            )}
          </div>
        </section>

        {/* Project Tasks */}
        <section className="detail-section">
          <h2>Tasks ({tasks.length})</h2>
          <div className="tasks-list">
            {tasks.length > 0 ? (
              <div className="items-grid">
                {tasks.map(task => (
                  <div 
                    key={task.id} 
                    className="item-card"
                    onClick={() => navigate(`/tasks/${task.id}`)}
                  >
                    <h3>{task.title}</h3>
                    <p>{task.description?.substring(0, 100)}{task.description?.length > 100 ? '...' : ''}</p>
                    <div className="task-meta">
                      <span className={`badge ${getStatusBadgeClass(task.status)}`}>
                        {task.status}
                      </span>
                      <span className="badge priority-badge">
                        {task.priority}
                      </span>
                      {task.assigneeName && (
                        <span className="assignee">
                          👤 {task.assigneeName}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p>No tasks created for this project yet.</p>
            )}
          </div>
        </section>
      </div>
    </div>
  );
};

export default ProjectDetailPage;
