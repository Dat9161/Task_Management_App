import React from 'react';
import { useNavigate } from 'react-router-dom';

const ProjectListWidget = ({ activeProjects, currentSprints }) => {
  const navigate = useNavigate();

  const formatDate = (dateString) => {
    if (!dateString) return 'No date';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric' 
    });
  };

  const getSprintStatus = (sprint) => {
    if (!sprint.status) return 'PLANNED';
    return sprint.status;
  };

  const getStatusColor = (status) => {
    const colors = {
      'PLANNED': '#6c757d',
      'ACTIVE': '#007bff',
      'COMPLETED': '#28a745'
    };
    return colors[status] || '#6c757d';
  };

  const handleProjectClick = (projectId) => {
    navigate(`/projects/${projectId}`);
  };

  const handleSprintClick = (sprintId) => {
    navigate(`/sprints/${sprintId}`);
  };

  return (
    <div className="project-list-widget">
      {/* Active Projects */}
      <div className="widget-section">
        <h3 className="widget-section-title">Active Projects</h3>
        {activeProjects && activeProjects.length > 0 ? (
          <div className="project-list">
            {activeProjects.map((project) => (
              <div 
                key={project.id} 
                className="project-widget-item"
                onClick={() => handleProjectClick(project.id)}
                style={{ cursor: 'pointer' }}
              >
                <div className="project-widget-header">
                  <span className="project-widget-name">{project.name}</span>
                </div>
                {project.description && (
                  <div className="project-widget-description">
                    {project.description.length > 100 
                      ? `${project.description.substring(0, 100)}...` 
                      : project.description}
                  </div>
                )}
                <div className="project-widget-meta">
                  <span>Manager: {project.manager?.username || 'N/A'}</span>
                  <span>Members: {project.members?.length || 0}</span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p className="no-items">No active projects</p>
        )}
      </div>

      {/* Current Sprints */}
      <div className="widget-section">
        <h3 className="widget-section-title">Current Sprints</h3>
        {currentSprints && currentSprints.length > 0 ? (
          <div className="sprint-list">
            {currentSprints.map((sprint) => {
              const status = getSprintStatus(sprint);
              return (
                <div 
                  key={sprint.id} 
                  className="sprint-widget-item"
                  onClick={() => handleSprintClick(sprint.id)}
                  style={{ cursor: 'pointer' }}
                >
                  <div className="sprint-widget-header">
                    <span className="sprint-widget-name">{sprint.name}</span>
                    <span 
                      className="sprint-status-badge"
                      style={{ backgroundColor: getStatusColor(status) }}
                    >
                      {status}
                    </span>
                  </div>
                  <div className="sprint-widget-dates">
                    {formatDate(sprint.startDate)} - {formatDate(sprint.endDate)}
                  </div>
                  {sprint.tasks && (
                    <div className="sprint-widget-tasks">
                      Tasks: {sprint.tasks.length}
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        ) : (
          <p className="no-items">No current sprints</p>
        )}
      </div>
    </div>
  );
};

export default ProjectListWidget;
