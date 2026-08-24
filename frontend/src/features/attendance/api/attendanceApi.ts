import { axiosClient } from '@/api/axiosClient';

export interface QRSession {
  sessionId: string;
  qrCodes: string[];
  expiresAt: string;
}

export interface ScanQrPayload {
  eventId: string;
  employeeId: string;
  qrToken: string;
}

export const attendanceApi = {
  createSession: async (eventId: string): Promise<QRSession> => {
    const response: any = await axiosClient.post(`/attendance/events/${eventId}/session`);
    return response.data;
  },

  getSession: async (eventId: string): Promise<QRSession> => {
    const response: any = await axiosClient.get(`/attendance/events/${eventId}/session`);
    return response.data;
  },

  scanQr: async (payload: ScanQrPayload): Promise<any> => {
    const response: any = await axiosClient.post(`/attendance/check-in/scan`, payload);
    return response.data;
  }
};
