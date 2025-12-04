import React, { useState } from 'react';
import '../style.css';

const SearchBar = ({ onSearch }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    onSearch(searchQuery);
  };

  const handleChange = (e) => {
    setSearchQuery(e.target.value);
  };

  const handleClear = () => {
    setSearchQuery('');
    onSearch('');
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSearch} className="search-form">
        <input
          type="text"
          className="search-input"
          placeholder="Search tasks by title or description..."
          value={searchQuery}
          onChange={handleChange}
        />
        {searchQuery && (
          <button
            type="button"
            className="btn-clear"
            onClick={handleClear}
            aria-label="Clear search"
          >
            ✕
          </button>
        )}
        <button type="submit" className="btn-search">
          🔍 Search
        </button>
      </form>
    </div>
  );
};

export default SearchBar;
