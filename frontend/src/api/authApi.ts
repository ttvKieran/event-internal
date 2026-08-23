import { type Employee } from '../store/useAuthStore';
import { axiosClient } from './axiosClient';

interface LoginResponseData {
  accessToken: string;
  refreshToken: string;
  employee: Employee;
}

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
}

export const authApi = {
  login: async (employeeCode: string, password: string): Promise<{ token: string, employee: Employee }> => {
    try {
      const response = await axiosClient.post<any, ApiResponse<LoginResponseData>>('/auth/login', {
        employeeCode,
        password,
      });

      if (!response.success || !response.data) {
        throw new Error(response.message || 'Đăng nhập thất bại');
      }

      return {
        token: response.data.accessToken,
        employee: response.data.employee,
      };
    } catch (error: any) {
      throw error;
    }
  },

  getMe: async (): Promise<Employee> => {
    try {
      const response = await axiosClient.get<any, ApiResponse<Employee>>('/auth/me');
      if (!response.success || !response.data) {
        throw new Error(response.message || 'Không thể lấy thông tin cá nhân');
      }
      return response.data;
    } catch (error: any) {
      throw error;
    }
  },

  updateProfile: async (id: string, data: { fullname?: string, email?: string, password?: string }): Promise<void> => {
    try {
      const response = await axiosClient.patch<any, ApiResponse<any>>(`/Employees/${id}`, data);
      if (!response.success) {
        throw new Error(response.message || 'Cập nhật thất bại');
      }
    } catch (error: any) {
      throw error;
    }
  }
};
