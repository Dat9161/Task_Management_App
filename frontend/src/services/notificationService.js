import api from './api';

const notificationService = {
  // Get all notifications for a user
  getNotifications: async (userId) => {
    const response = await api.get(`/notifications/user/${userId}`);
    return response.data;
  },

  // Get unread notifications for a user
  getUnreadNotifications: async (userId) => {
    const response = await api.get(`/notifications/unread/${userId}`);
    return response.data;
  },

  // Mark notification as read
  markAsRead: async (notificationId) => {
    const response = await api.put(`/notifications/${notificationId}/read`);
    return response.data;
  },
};

export default notificationService;
