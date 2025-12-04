import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import SprintForm from '../components/SprintForm';
import '../style.css';

const SprintDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [sprint, setSprint] = useState(null);
  const [tasks, setTasks] = useState([]);
  const [progressMetrics, setProgressMetrics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showEditForm, setShowEditForm] = useState(false);
  const [completing, setCompleting] = useState(false);

  const fetchSprintDetails = async () => {
    try {
      setLoading(true);
      
      // Fetch sprint details
      const sprintResponse = await api.get(`/sprints/${id}`);
      setSprint(sprintResponse.data);

      // Fetch sprint tasks
      const tasksResponse = await api.get(`/sprints/${id}/tasks`);
      setTasks(tasksResponse.data);

      // Fetch sprint progress metrics
      try {
        const progressResponse = await api.get(`/reports/sprint/${id}/progress`);
        setProgressMetrics(progressResponse.data);
      } catch (err) {
        console.error('Error fetching progress metrics:', err);
        // Continue even if progress metrics fail
      }

      setError(null);
    } catch (err) {
      console.error('Error fetching sprint details:', err);
      setError('Failed to load sprint details. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSprintDetails();
  }, [id]);

  const handleEditSuccess = () => {
    setShowEditForm(false);
    fetchSprintDetails();
  };

  const handleEditCancel = () => {
    setShowEditForm(false);
  };

  const handleCompleteSprint = async () => {
    if (!window.confirm('Are you sure you want to complete this sprint? This will generate a final report.')) {
      return;
    }

    try {
      setCompleting(true);
      await api.post(`/sprints/${id}/complete`);
      fetchSprintDetails();
    } catch (err) {
      console.error('Error completing sprint:', err);
      alert('Failed to complete sprint. Please try again.');
    } finally {
      setCompleting(false);
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

  const groupTasksByStatus = () => {
    const grouped = {
      TODO: [],
      IN_PROGRESS: [],
      DONE: [],
      BLOCKED: []
    };

    tasks.forEach(task => {
      if (grouped[task.status]) {
        grouped[task.status].push(task);
      }
    });

    return grouped;
  };

  const calculateProgress = () => {
    if (tasks.length === 0) return 0;
    const completedTasks = tasks.filter(task => task.status === 'DONE').length;
    return Math.round((completedTasks / tasks.length) * 100);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  };

  const isProjectManager = sprint && user && sprint.project?.managerId === user.id;
  const canComplete = sprint && sprint.status !== 'COMPLETED' && isProjectManager;
  const groupedTasks = groupTasksByStatus();
  const progress = calculateProgress();

  if (loading) {
    return (
      <div className="page-container">
        <h1>Sprint Details</h1>
        <p>Loading sprint details...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1>Sprint Details</h1>
        <div className="error-message">{error}</div>
        <button className="btn-secondary" onClick={() => navigate('/sprints')}>
          Back to Sprints
        </button>
      </div>
    );
  }

  if (!sprint) {
    return (
      <div className="page-container">
        <h1>Sprint Details</h1>
        <p>Sprint not found.</p>
        <button className="btn-secondary" onClick={() => navigate('/sprints')}>
          Back to Sprints
        </button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <button 
            className="btn-secondary" 
            onClick={() => navigate(`/projects/${sprint.projectId}`)}
          >
            ← Back to Project
          </button>
          <h1>{sprint.name}</h1>
        </div>
        <div className="button-group">
          {isProjectManager && sprint.status !== 'COMPLETED' && (
            <button className="btn-primary" onClick={() => setShowEditForm(true)}>
              Edit Sprint
            </button>
          )}
          {canComplete && (
            <button 
              className="btn-success" 
              onClick={handleCompleteSprint}
              disabled={completing}
            >
              {completing ? 'Completing...' : 'Complete Sprint'}
            </button>
          )}
          <button 
            className="btn-primary" 
            onClick={() => navigate(`/reports?sprintId=${id}`)}
          >
            📊 View Report
          </button>
        </div>
      </div>

      {showEditForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <SprintForm 
              sprint={sprint}
              projectId={sprint.projectId}
              onSuccess={handleEditSuccess}
              onCancel={handleEditCancel}
            />
          </div>
        </div>
      )}

      <div className="sprint-details">
        {/* Sprint Information */}
        <section className="detail-section">
          <h2>Sprint Information</h2>
          <div className="detail-content">
            <p>
              <strong>Status:</strong>{' '}
              <span className={`badge ${getStatusBadgeClass(sprint.status)}`}>
                {sprint.status}
              </span>
            </p>
            <p><strong>Start Date:</strong> {formatDate(sprint.startDate)}</p>
            <p><strong>End Date:</strong> {formatDate(sprint.endDate)}</p>
            <p><strong>Duration:</strong> {sprint.startDate && sprint.endDate ? 
              Math.ceil((new Date(sprint.endDate) - new Date(sprint.startDate)) / (1000 * 60 * 60 * 24)) + ' days' 
              : 'N/A'}
            </p>
          </div>
        </section>

        {/* Sprint Progress Metrics */}
        <section className="detail-section">
          <h2>Progress Metrics</h2>
          <div className="metrics-container">
            <div className="metric-card">
              <h3>Overall Progress</h3>
              <div className="progress-circle">
                <span className="progress-value">{progress}%</span>
              </div>
              <div className="progress-bar-container">
                <div 
                  className="progress-bar-fill" 
                  style={{ width: `${progress}%` }}
                />
              </div>
            </div>
            
            <div className="metric-card">
              <h3>Task Distribution</h3>
              <div className="task-stats">
                <div className="stat-item">
                  <span className="stat-label">Total Tasks:</span>
                  <span className="stat-value">{tasks.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">Completed:</span>
                  <span className="stat-value">{groupedTasks.DONE.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">In Progress:</span>
                  <span className="stat-value">{groupedTasks.IN_PROGRESS.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">To Do:</span>
                  <span className="stat-value">{groupedTasks.TODO.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-label">Blocked:</span>
                  <span className="stat-value">{groupedTasks.BLOCKED.length}</span>
                </div>
              </div>
            </div>

            {progressMetrics && (
              <div className="metric-card">
                <h3>Velocity</h3>
                <div className="velocity-display">
                  <span className="velocity-value">{progressMetrics.velocity || 0}</span>
                  <span className="velocity-label">tasks completed</span>
                </div>
              </div>
            )}
          </div>
        </section>

        {/* Sprint Tasks Grouped by Status */}
        <section className="detail-section">
          <h2>Sprint Tasks ({tasks.length})</h2>
          
          {tasks.length === 0 ? (
            <p>No tasks assigned to this sprint yet.</p>
          ) : (
            <div className="tasks-by-status">
              {Object.entries(groupedTasks).map(([status, statusTasks]) => (
                statusTasks.length > 0 && (
                  <div key={status} className="status-group">
                    <h3 className={`status-header ${getStatusBadgeClass(status)}`}>
                      {status.replace('_', ' ')} ({statusTasks.length})
                    </h3>
                    <div className="tasks-list">
                      {statusTasks.map(task => (
                        <div 
                          key={task.id} 
                          className="task-item"
                          onClick={() => navigate(`/tasks/${task.id}`)}
                        >
                          <div className="task-item-header">
                            <h4>{task.title}</h4>
                            <span className={`badge priority-${task.priority?.toLowerCase()}`}>
                              {task.priority}
                            </span>
                          </div>
                          <p className="task-description">
                            {task.description?.substring(0, 100)}
                            {task.description?.length > 100 ? '...' : ''}
                          </p>
                          <div className="task-item-footer">
                            {task.assigneeName && (
                              <span className="assignee">👤 {task.assigneeName}</span>
                            )}
                            {task.dueDate && (
                              <span className="due-date">
                                📅 {formatDate(task.dueDate)}
                              </span>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )
              ))}
            </div>
          )}
        </section>
      </div>
    </div>
  );
};

export default SprintDetailPage;
