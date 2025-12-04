import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import ProgressMetrics from '../components/ProgressMetrics';
import '../style.css';

const ReportPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const sprintId = searchParams.get('sprintId');
  
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [exporting, setExporting] = useState(false);

  useEffect(() => {
    if (sprintId) {
      fetchSprintReport(sprintId);
    }
  }, [sprintId]);

  const fetchSprintReport = async (id) => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.get(`/reports/sprint/${id}`);
      setReport(response.data);
    } catch (err) {
      console.error('Error fetching sprint report:', err);
      setError('Failed to load sprint report. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleExportReport = async () => {
    if (!sprintId) return;

    try {
      setExporting(true);
      const response = await api.get(`/reports/sprint/${sprintId}/export`, {
        responseType: 'blob'
      });

      // Create a blob from the PDF data
      const blob = new Blob([response.data], { type: 'application/pdf' });
      
      // Create a link element and trigger download
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `sprint-report-${sprintId}.pdf`;
      document.body.appendChild(link);
      link.click();
      
      // Cleanup
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Error exporting report:', err);
      alert('Failed to export report. Please try again.');
    } finally {
      setExporting(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
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

  if (!sprintId) {
    return (
      <div className="page-container">
        <h1>Sprint Reports</h1>
        <div className="info-message">
          <p>Please select a sprint to view its report.</p>
          <button className="btn-primary" onClick={() => navigate('/sprints')}>
            Go to Sprints
          </button>
        </div>
      </div>
    );
  }

  if (loading) {
    return (
      <div className="page-container">
        <h1>Sprint Report</h1>
        <p>Loading report...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <h1>Sprint Report</h1>
        <div className="error-message">{error}</div>
        <button className="btn-secondary" onClick={() => navigate('/sprints')}>
          Back to Sprints
        </button>
      </div>
    );
  }

  if (!report) {
    return (
      <div className="page-container">
        <h1>Sprint Report</h1>
        <p>No report data available.</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="page-header">
        <div>
          <button className="btn-secondary" onClick={() => navigate('/sprints')}>
            ← Back to Sprints
          </button>
          <h1>Sprint Report: {report.sprintName}</h1>
        </div>
        <button 
          className="btn-primary" 
          onClick={handleExportReport}
          disabled={exporting}
        >
          {exporting ? 'Exporting...' : '📥 Export PDF'}
        </button>
      </div>

      <div className="report-container">
        {/* Sprint Summary Section */}
        <section className="detail-section">
          <h2>Sprint Summary</h2>
          <div className="detail-content">
            <p><strong>Sprint Name:</strong> {report.sprintName}</p>
            <p><strong>Start Date:</strong> {formatDate(report.startDate)}</p>
            <p><strong>End Date:</strong> {formatDate(report.endDate)}</p>
            <p><strong>Duration:</strong> {report.startDate && report.endDate ? 
              Math.ceil((new Date(report.endDate) - new Date(report.startDate)) / (1000 * 60 * 60 * 24)) + ' days' 
              : 'N/A'}
            </p>
            {report.sprintSummary && (
              <div className="summary-text">
                <strong>Summary:</strong>
                <p>{report.sprintSummary}</p>
              </div>
            )}
          </div>
        </section>

        {/* Metrics Section */}
        <section className="detail-section">
          <h2>Performance Metrics</h2>
          <div className="metrics-container">
            <div className="metric-card">
              <h3>Completion Rate</h3>
              <div className="metric-value-large">
                {report.taskCompletionRate.toFixed(1)}%
              </div>
              <div className="progress-bar-container">
                <div 
                  className="progress-bar-fill" 
                  style={{ width: `${report.taskCompletionRate}%` }}
                />
              </div>
            </div>

            <div className="metric-card">
              <h3>Velocity</h3>
              <div className="metric-value-large">
                {report.velocity}
              </div>
              <p className="metric-description">Tasks completed</p>
            </div>

            {report.metrics && (
              <div className="metric-card">
                <h3>Task Summary</h3>
                <div className="task-stats">
                  <div className="stat-item">
                    <span className="stat-label">Total Tasks:</span>
                    <span className="stat-value">{report.metrics.totalTasks}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Completed:</span>
                    <span className="stat-value">{report.metrics.completedTasks}</span>
                  </div>
                </div>
              </div>
            )}
          </div>
        </section>

        {/* Progress Metrics Component */}
        {report.metrics && (
          <section className="detail-section">
            <h2>Detailed Progress Metrics</h2>
            <ProgressMetrics metrics={report.metrics} />
          </section>
        )}

        {/* Completed Tasks Section */}
        <section className="detail-section">
          <h2>Completed Tasks ({report.completedTasks?.length || 0})</h2>
          {report.completedTasks && report.completedTasks.length > 0 ? (
            <div className="tasks-list">
              {report.completedTasks.map(task => (
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
                  {task.description && (
                    <p className="task-description">
                      {task.description.substring(0, 150)}
                      {task.description.length > 150 ? '...' : ''}
                    </p>
                  )}
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
          ) : (
            <p>No tasks were completed in this sprint.</p>
          )}
        </section>

        {/* Burndown Chart Data */}
        {report.burndownChartData && report.burndownChartData.length > 0 && (
          <section className="detail-section">
            <h2>Burndown Chart Data</h2>
            <div className="burndown-data">
              <p className="info-message">
                Burndown chart visualization would be displayed here with charting library.
              </p>
              <div className="data-points">
                {report.burndownChartData.map((value, index) => (
                  <span key={index} className="data-point">
                    Day {index + 1}: {value} tasks
                  </span>
                ))}
              </div>
            </div>
          </section>
        )}
      </div>
    </div>
  );
};

// Helper function to get status colors
const getStatusColor = (status) => {
  const colorMap = {
    'TODO': '#6c757d',
    'IN_PROGRESS': '#007bff',
    'DONE': '#28a745',
    'BLOCKED': '#dc3545'
  };
  return colorMap[status] || '#6c757d';
};

export default ReportPage;
