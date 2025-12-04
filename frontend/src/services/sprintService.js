import api from './api';

const sprintService = {
  // Create a new sprint
  createSprint: async (sprintData) => {
    const response = await api.post('/sprints', sprintData);
    return response.data;
  },

  // Get sprint by ID
  getSprintById: async (sprintId) => {
    const response = await api.get(`/sprints/${sprintId}`);
    return response.data;
  },

  // Update sprint
  updateSprint: async (sprintId, sprintData) => {
    const response = await api.put(`/sprints/${sprintId}`, sprintData);
    return response.data;
  },

  // Get sprints by project
  getSprintsByProject: async (projectId) => {
    const response = await api.get(`/projects/${projectId}/sprints`);
    return response.data;
  },

  // Complete sprint
  completeSprint: async (sprintId) => {
    const response = await api.post(`/sprints/${sprintId}/complete`);
    return response.data;
  },
};

export default sprintService;
