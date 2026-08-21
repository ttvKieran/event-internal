import { axiosClient } from '@/api/axiosClient';

export interface AsyncOperationApiResponse {
  success: boolean;
  code: string;
  message: string;
  data: {
    resourceId: string;
    status: string;
    statusCheckUrl?: string;
    submittedAt: string;
  };
  timestamp: string;
}

export interface RegistrationStatusResponse {
  success: boolean;
  code: string;
  message: string;
  data: string; // e.g. "SUCCESS:uuid" or "PENDING" or "FAILED:reason"
}

export const registrationApi = {
  // Gửi lệnh đăng ký vé (Bất đồng bộ)
  registerForEvent: async (campaignId: string, provider: string = 'vnpay'): Promise<AsyncOperationApiResponse> => {
    return axiosClient.post('/registrations', { campaignId, provider });
  },

  // Polling check trạng thái đăng ký
  checkRegistrationStatus: async (campaignId: string, userId: string): Promise<RegistrationStatusResponse> => {
    return axiosClient.get('/registrations/status', {
      params: { campaignId, userId },
    });
  },

  // Lấy danh sách đơn đăng ký của nhân viên
  getUserRegistrations: async (employeeId: string): Promise<any> => {
    return axiosClient.get('/registrations', {
      params: { employeeId },
    });
  },
};
