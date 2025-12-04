import api from './api';

const dashboardService = {
  // Get dashboard data for a user
  getDashboard: async (userId) => {
    const response = await api.get(`/dashboard/user/${userId}`);
    return response.data;
  },
};

export default dashboardService;
