import api from './api';

const reportService = {
  // Get sprint report
  getSprintReport: async (sprintId) => {
    const response = await api.get(`/reports/sprint/${sprintId}`);
    return response.data;
  },

  // Get project progress
  getProjectProgress: async (projectId) => {
    const response = await api.get(`/reports/project/${projectId}/progress`);
    return response.data;
  },

  // Export sprint report
  exportReport: async (sprintId) => {
    const response = await api.get(`/reports/sprint/${sprintId}/export`, {
      responseType: 'blob',
    });
    
    // Create a download link for the file
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `sprint-report-${sprintId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    
    return response.data;
  },
};

export default reportService;
