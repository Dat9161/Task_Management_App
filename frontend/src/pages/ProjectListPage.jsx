import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import ProjectCard from '../components/ProjectCard';
import ProjectForm from '../components/ProjectForm';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import '../style.css';

const ProjectListPage = () => {
  const { user } = useAuth();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);

  const fetchProjects = async () => {
    if (!user || !user.id) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      const response = await api.get(`/projects/user/${user.id}`);
      setProjects(response.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching projects:', err);
      setError('Failed to load projects. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProjects();
  }, [user]);

  const handleCreateSuccess = () => {
    setShowCreateForm(false);
    fetchProjects();
  };

  const handleCreateCancel = () => {
    setShowCreateForm(false);
  };

  if (loading) {
    return <Loading message="Loading projects..." />;
  }

  if (error) {
    return (
      <ErrorMessage 
        title="Projects Error"
        message={error}
        onRetry={fetchProjects}
      />
    );
  }

  return (
    <div className="page-container fade-in">
      <div className="page-header">
        <h1>Projects</h1>
        <button 
          className="btn-primary" 
          onClick={() => setShowCreateForm(true)}
        >
          + Create Project
        </button>
      </div>

      {showCreateForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <ProjectForm 
              onSuccess={handleCreateSuccess}
              onCancel={handleCreateCancel}
            />
          </div>
        </div>
      )}

      {projects.length === 0 ? (
        <div className="empty-state">
          <p>No projects found. Create your first project to get started!</p>
        </div>
      ) : (
        <div className="projects-grid">
          {projects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      )}
    </div>
  );
};

export default ProjectListPage;
