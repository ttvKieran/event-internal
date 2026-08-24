import { axiosClient } from '@/api/axiosClient';
import type { DashboardMetrics, RegistrationMetrics, AttendanceMetrics } from '@/types/analytics';

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export const analyticsApi = {
  getDashboard: async (eventId: string): Promise<DashboardMetrics> => {
    try {
      const response = await axiosClient.get<any, ApiResponse<DashboardMetrics>>(`/analytics/events/${eventId}/dashboard`);
      if (!response.success) {
        throw new Error(response.message || 'Lỗi khi tải thông số Dashboard');
      }
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  getRegistrationMetrics: async (eventId: string): Promise<RegistrationMetrics> => {
    try {
      const response = await axiosClient.get<any, ApiResponse<RegistrationMetrics>>(`/analytics/events/${eventId}/registration-metrics`);
      if (!response.success) {
        throw new Error(response.message || 'Lỗi khi tải thông số Đăng ký');
      }
      return response.data;
    } catch (error) {
      throw error;
    }
  },

  getAttendanceMetrics: async (eventId: string): Promise<AttendanceMetrics> => {
    try {
      const response = await axiosClient.get<any, ApiResponse<AttendanceMetrics>>(`/analytics/events/${eventId}/attendance-metrics`);
      if (!response.success) {
        throw new Error(response.message || 'Lỗi khi tải thông số Check-in');
      }
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};
