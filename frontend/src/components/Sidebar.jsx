import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import '../style.css';

const Sidebar = () => {
  const location = useLocation();

  const menuItems = [
    { path: '/projects', icon: '📁', label: 'Projects' },
    { path: '/tasks', icon: '✓', label: 'Tasks' },
    { path: '/sprints', icon: '🏃', label: 'Sprints' },
    { path: '/reports', icon: '📈', label: 'Reports' },
    { path: '/notifications', icon: '🔔', label: 'Notifications' }
  ];

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <ul className="sidebar-menu">
          {menuItems.map((item) => (
            <li key={item.path} className="sidebar-menu-item">
              <Link 
                to={item.path} 
                className={`sidebar-link ${isActive(item.path) ? 'active' : ''}`}
              >
                <span className="sidebar-icon">{item.icon}</span>
                <span className="sidebar-label">{item.label}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
};

export default Sidebar;
