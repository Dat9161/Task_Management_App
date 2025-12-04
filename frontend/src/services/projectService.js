import api from './api';

const projectService = {
  // Create a new project
  createProject: async (projectData) => {
    const response = await api.post('/projects', projectData);
    return response.data;
  },

  // Get project by ID
  getProjectById: async (projectId) => {
    const response = await api.get(`/projects/${projectId}`);
    return response.data;
  },

  // Update project
  updateProject: async (projectId, projectData) => {
    const response = await api.put(`/projects/${projectId}`, projectData);
    return response.data;
  },

  // Delete project
  deleteProject: async (projectId) => {
    const response = await api.delete(`/projects/${projectId}`);
    return response.data;
  },

  // Get projects by user
  getProjectsByUser: async (userId) => {
    const response = await api.get(`/projects/user/${userId}`);
    return response.data;
  },

  // Add member to project
  addMember: async (projectId, userId) => {
    const response = await api.post(`/projects/${projectId}/members/${userId}`);
    return response.data;
  },

  // Remove member from project
  removeMember: async (projectId, userId) => {
    const response = await api.delete(`/projects/${projectId}/members/${userId}`);
    return response.data;
  },
};

export default projectService;
