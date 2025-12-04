import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import SprintCard from '../components/SprintCard';
import SprintForm from '../components/SprintForm';
import '../style.css';

const SprintListPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const projectId = searchParams.get('projectId');
  
  const [sprints, setSprints] = useState([]);
  const [project, setProject] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);

  const fetchSprints = async () => {
    try {
      setLoading(true);
      
      if (projectId) {
        // Fetch sprints for specific project
        const sprintsResponse = await api.get(`/projects/${projectId}/sprints`);
        setSprints(sprintsResponse.data);
        
        // Fetch project details
        const projectResponse = await api.get(`/projects/${projectId}`);
        setProject(projectResponse.data);
      } else {
        // If no projectId, we could fetch all sprints for user's projects
        // For now, we'll show an error message
        setError('Please select a project to view sprints');
      }
      
      setError(null);
    } catch (err) {
      console.error('Error fetching sprints:', err);
      setError('Failed to load sprints. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (projectId) {
      fetchSprints();
    } else {
      setLoading(false);
      setError('No project selected. Please select a project first.');
    }
  }, [projectId]);

  const handleCreateSuccess = () => {
    setShowCreateForm(false);
    fetchSprints();
  };

  const handleCreateCancel = () => {
    setShowCreateForm(false);
  };

  const handleBackToProject = () => {
    if (projectId) {
      navigate(`/projects/${projectId}`);
    } else {
      navigate('/projects');
    }
  };

  const isProjectManager = project && user && project.managerId === user.id;

  if (loading) {
    return (
      <div className="page-container">
        <h1>Sprints</h1>
        <p>Loading sprints...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1>Sprints</h1>
        <div className="error-message">{error}</div>
        <button className="btn-secondary" onClick={handleBackToProject}>
          Back to Projects
        </button>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <button className="btn-secondary" onClick={handleBackToProject}>
            ← Back to Project
          </button>
          <h1>Sprints {project && `- ${project.name}`}</h1>
        </div>
        {isProjectManager && (
          <button 
            className="btn-primary" 
            onClick={() => setShowCreateForm(true)}
          >
            + Create Sprint
          </button>
        )}
      </div>

      {showCreateForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <SprintForm 
              projectId={projectId}
              onSuccess={handleCreateSuccess}
              onCancel={handleCreateCancel}
            />
          </div>
        </div>
      )}

      {sprints.length === 0 ? (
        <div className="empty-state">
          <p>No sprints found. {isProjectManager ? 'Create your first sprint to get started!' : 'Ask your project manager to create a sprint.'}</p>
        </div>
      ) : (
        <div className="sprints-grid">
          {sprints.map(sprint => (
            <SprintCard key={sprint.id} sprint={sprint} />
          ))}
        </div>
      )}
    </div>
  );
};

export default SprintListPage;
