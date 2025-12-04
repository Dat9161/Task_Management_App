import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import TaskCard from '../components/TaskCard';
import TaskForm from '../components/TaskForm';
import SearchBar from '../components/SearchBar';
import FilterPanel from '../components/FilterPanel';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import '../style.css';

const TaskListPage = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [filteredTasks, setFilteredTasks] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showFilters, setShowFilters] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilters, setActiveFilters] = useState({});

  useEffect(() => {
    fetchData();
  }, [user]);

  useEffect(() => {
    applyFiltersAndSearch();
  }, [tasks, searchQuery, activeFilters]);

  const fetchData = async () => {
    if (!user || !user.id) {
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      
      // Fetch user's projects and tasks
      const [projectsResponse, tasksResponse] = await Promise.all([
        api.get(`/projects/user/${user.id}`),
        api.get(`/users/${user.id}/tasks`)
      ]);
      
      setProjects(projectsResponse.data);
      setTasks(tasksResponse.data);
      setError(null);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError('Failed to load tasks. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const applyFiltersAndSearch = () => {
    let result = [...tasks];

    // Apply search
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      result = result.filter(task => 
        task.title.toLowerCase().includes(query) ||
        (task.description && task.description.toLowerCase().includes(query))
      );
    }

    // Apply filters
    if (activeFilters.status) {
      result = result.filter(task => task.status === activeFilters.status);
    }

    if (activeFilters.priority) {
      result = result.filter(task => task.priority === activeFilters.priority);
    }

    if (activeFilters.assigneeId) {
      result = result.filter(task => 
        task.assignee && task.assignee.id === parseInt(activeFilters.assigneeId)
      );
    }

    setFilteredTasks(result);
  };

  const handleSearch = (query) => {
    setSearchQuery(query);
  };

  const handleFilterChange = (filters) => {
    setActiveFilters(filters);
  };

  const handleCreateSuccess = () => {
    setShowCreateForm(false);
    fetchData();
  };

  const handleCreateCancel = () => {
    setShowCreateForm(false);
  };

  const groupTasksByStatus = () => {
    const grouped = {
      TODO: [],
      IN_PROGRESS: [],
      DONE: [],
      BLOCKED: []
    };

    filteredTasks.forEach(task => {
      if (grouped[task.status]) {
        grouped[task.status].push(task);
      }
    });

    return grouped;
  };

  const hasActiveFilters = () => {
    return searchQuery || Object.keys(activeFilters).length > 0;
  };

  if (loading) {
    return <Loading message="Loading tasks..." />;
  }

  if (error) {
    return (
      <ErrorMessage 
        title="Tasks Error"
        message={error}
        onRetry={fetchData}
      />
    );
  }

  const groupedTasks = groupTasksByStatus();

  return (
    <div className="page-container fade-in">
      <div className="page-header">
        <h1>Tasks</h1>
        <div className="header-actions">
          <button 
            className="btn-secondary" 
            onClick={() => setShowFilters(!showFilters)}
          >
            {showFilters ? 'Hide Filters' : 'Show Filters'}
          </button>
          <button 
            className="btn-primary" 
            onClick={() => setShowCreateForm(true)}
          >
            + Create Task
          </button>
        </div>
      </div>

      <SearchBar onSearch={handleSearch} />

      <div className="task-list-layout">
        {showFilters && (
          <div className="filters-sidebar">
            <FilterPanel 
              onFilterChange={handleFilterChange}
              projects={projects}
            />
          </div>
        )}

        <div className="task-list-main">
          {hasActiveFilters() && (
            <div className="filter-info">
              <p>
                Showing {filteredTasks.length} of {tasks.length} tasks
                {searchQuery && ` matching "${searchQuery}"`}
              </p>
            </div>
          )}

          {filteredTasks.length === 0 ? (
            <div className="empty-state">
              {hasActiveFilters() ? (
                <p>No tasks match your search or filters.</p>
              ) : (
                <p>No tasks found. Create your first task to get started!</p>
              )}
            </div>
          ) : (
            <div className="task-columns">
              <div className="task-column">
                <div className="column-header">
                  <h3>TODO</h3>
                  <span className="task-count">{groupedTasks.TODO.length}</span>
                </div>
                <div className="task-column-content">
                  {groupedTasks.TODO.map(task => (
                    <TaskCard key={task.id} task={task} />
                  ))}
                </div>
              </div>

              <div className="task-column">
                <div className="column-header">
                  <h3>IN PROGRESS</h3>
                  <span className="task-count">{groupedTasks.IN_PROGRESS.length}</span>
                </div>
                <div className="task-column-content">
                  {groupedTasks.IN_PROGRESS.map(task => (
                    <TaskCard key={task.id} task={task} />
                  ))}
                </div>
              </div>

              <div className="task-column">
                <div className="column-header">
                  <h3>DONE</h3>
                  <span className="task-count">{groupedTasks.DONE.length}</span>
                </div>
                <div className="task-column-content">
                  {groupedTasks.DONE.map(task => (
                    <TaskCard key={task.id} task={task} />
                  ))}
                </div>
              </div>

              <div className="task-column">
                <div className="column-header">
                  <h3>BLOCKED</h3>
                  <span className="task-count">{groupedTasks.BLOCKED.length}</span>
                </div>
                <div className="task-column-content">
                  {groupedTasks.BLOCKED.map(task => (
                    <TaskCard key={task.id} task={task} />
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {showCreateForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <TaskForm 
              onSuccess={handleCreateSuccess}
              onCancel={handleCreateCancel}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskListPage;
