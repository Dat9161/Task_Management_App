import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../style.css';

const ProjectCard = ({ project }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/projects/${project.id}`);
  };

  return (
    <div className="project-card" onClick={handleClick}>
      <div className="project-card-header">
        <h3>{project.name}</h3>
      </div>
      <div className="project-card-body">
        <p className="project-description">
          {project.description || 'No description provided'}
        </p>
        <div className="project-card-footer">
          <span className="member-count">
            👥 {project.memberCount || 0} member{project.memberCount !== 1 ? 's' : ''}
          </span>
        </div>
      </div>
    </div>
  );
};

export default ProjectCard;
