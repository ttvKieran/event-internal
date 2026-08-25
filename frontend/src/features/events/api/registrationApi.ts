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

export interface RegistrationResponse {
  registrationId: string;
  eventId: string;
  employeeId: string;
  status: 'RESERVED' | 'CONFIRMED' | 'CANCELLED';
  cancelReason?: string | null;
  registeredAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T;
  timestamp: string;
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

  // Lấy đăng ký của nhân viên, có thể giới hạn trong một sự kiện.
  getUserRegistrations: async (
    employeeId: string,
    eventId?: string,
  ): Promise<ApiResponse<RegistrationResponse[]>> => {
    return axiosClient.get('/registrations', {
      params: { employeeId, eventId },
    });
  },

  cancelRegistration: async (
    registrationId: string,
    reason: string,
  ): Promise<ApiResponse<RegistrationResponse>> => {
    return axiosClient.post(`/registrations/${registrationId}/cancel`, {
      reason,
    });
  },
};
