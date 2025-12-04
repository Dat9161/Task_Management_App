import api from './api';

const taskService = {
  // Create a new task
  createTask: async (taskData) => {
    const response = await api.post('/tasks', taskData);
    return response.data;
  },

  // Get task by ID
  getTaskById: async (taskId) => {
    const response = await api.get(`/tasks/${taskId}`);
    return response.data;
  },

  // Update task
  updateTask: async (taskId, taskData) => {
    const response = await api.put(`/tasks/${taskId}`, taskData);
    return response.data;
  },

  // Delete task
  deleteTask: async (taskId) => {
    const response = await api.delete(`/tasks/${taskId}`);
    return response.data;
  },

  // Get tasks by project
  getTasksByProject: async (projectId) => {
    const response = await api.get(`/projects/${projectId}/tasks`);
    return response.data;
  },

  // Get tasks by user
  getTasksByUser: async (userId) => {
    const response = await api.get(`/users/${userId}/tasks`);
    return response.data;
  },

  // Assign task to user
  assignTask: async (taskId, userId) => {
    const response = await api.put(`/tasks/${taskId}/assign/${userId}`);
    return response.data;
  },

  // Update task status
  updateStatus: async (taskId, status) => {
    const response = await api.put(`/tasks/${taskId}/status`, { status });
    return response.data;
  },

  // Search tasks with keyword
  searchTasks: async (keyword) => {
    const response = await api.get('/tasks/search', {
      params: { keyword },
    });
    return response.data;
  },

  // Filter tasks with multiple criteria
  filterTasks: async (filters) => {
    const response = await api.get('/tasks/search', {
      params: filters,
    });
    return response.data;
  },
};

export default taskService;
