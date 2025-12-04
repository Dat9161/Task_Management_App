import { useNavigate } from 'react-router-dom';
import '../style.css';

const SprintCard = ({ sprint }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/sprints/${sprint.id}`);
  };

  const getStatusBadgeClass = (status) => {
    const statusMap = {
      'PLANNED': 'status-planned',
      'ACTIVE': 'status-active',
      'COMPLETED': 'status-completed'
    };
    return statusMap[status] || 'status-default';
  };

  const calculateProgress = () => {
    if (!sprint.tasks || sprint.tasks.length === 0) return 0;
    const completedTasks = sprint.tasks.filter(task => task.status === 'DONE').length;
    return Math.round((completedTasks / sprint.tasks.length) * 100);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  };

  const progress = calculateProgress();

  return (
    <div className="sprint-card" onClick={handleClick}>
      <div className="sprint-card-header">
        <h3>{sprint.name}</h3>
        <span className={`badge ${getStatusBadgeClass(sprint.status)}`}>
          {sprint.status}
        </span>
      </div>
      <div className="sprint-card-body">
        <p className="sprint-dates">
          📅 {formatDate(sprint.startDate)} - {formatDate(sprint.endDate)}
        </p>
        <div className="progress-section">
          <div className="progress-label">
            <span>Progress</span>
            <span>{progress}%</span>
          </div>
          <div className="progress-bar-container">
            <div 
              className="progress-bar-fill" 
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default SprintCard;
