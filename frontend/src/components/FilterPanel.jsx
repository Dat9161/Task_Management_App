import React, { useState, useEffect } from 'react';
import '../style.css';

const FilterPanel = ({ onFilterChange, projects }) => {
  const [filters, setFilters] = useState({
    status: '',
    priority: '',
    assigneeId: ''
  });

  const [users, setUsers] = useState([]);

  useEffect(() => {
    // Extract unique users from projects
    if (projects && projects.length > 0) {
      const allUsers = new Map();
      projects.forEach(project => {
        if (project.members) {
          project.members.forEach(member => {
            allUsers.set(member.id, member);
          });
        }
      });
      setUsers(Array.from(allUsers.values()));
    }
  }, [projects]);

  const handleFilterChange = (filterName, value) => {
    const newFilters = {
      ...filters,
      [filterName]: value
    };
    setFilters(newFilters);
  };

  const handleApplyFilters = () => {
    // Only send non-empty filters
    const activeFilters = {};
    Object.keys(filters).forEach(key => {
      if (filters[key]) {
        activeFilters[key] = filters[key];
      }
    });
    onFilterChange(activeFilters);
  };

  const handleClearFilters = () => {
    const clearedFilters = {
      status: '',
      priority: '',
      assigneeId: ''
    };
    setFilters(clearedFilters);
    onFilterChange({});
  };

  const hasActiveFilters = filters.status || filters.priority || filters.assigneeId;

  return (
    <div className="filter-panel">
      <h3>Filters</h3>
      
      <div className="filter-group">
        <label htmlFor="status-filter">Status</label>
        <select
          id="status-filter"
          className="filter-select"
          value={filters.status}
          onChange={(e) => handleFilterChange('status', e.target.value)}
        >
          <option value="">All Statuses</option>
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN PROGRESS</option>
          <option value="DONE">DONE</option>
          <option value="BLOCKED">BLOCKED</option>
        </select>
      </div>

      <div className="filter-group">
        <label htmlFor="priority-filter">Priority</label>
        <select
          id="priority-filter"
          className="filter-select"
          value={filters.priority}
          onChange={(e) => handleFilterChange('priority', e.target.value)}
        >
          <option value="">All Priorities</option>
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
          <option value="CRITICAL">CRITICAL</option>
        </select>
      </div>

      <div className="filter-group">
        <label htmlFor="assignee-filter">Assignee</label>
        <select
          id="assignee-filter"
          className="filter-select"
          value={filters.assigneeId}
          onChange={(e) => handleFilterChange('assigneeId', e.target.value)}
        >
          <option value="">All Assignees</option>
          {users.map(user => (
            <option key={user.id} value={user.id}>
              {user.username}
            </option>
          ))}
        </select>
      </div>

      <div className="filter-actions">
        <button 
          className="btn-primary" 
          onClick={handleApplyFilters}
        >
          Apply Filters
        </button>
        {hasActiveFilters && (
          <button 
            className="btn-secondary" 
            onClick={handleClearFilters}
          >
            Clear Filters
          </button>
        )}
      </div>
    </div>
  );
};

export default FilterPanel;
