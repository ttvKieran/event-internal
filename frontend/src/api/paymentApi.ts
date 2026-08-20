import { axiosClient } from './axiosClient';

export const paymentApi = {
  // Gửi callback return từ VNPay lên Payment Service để kiểm tra chữ ký & cập nhật DB
  handleVnPayReturn: async (params: Record<string, string>) => {
    return axiosClient.get('/payments/vnpay-return', { params });
  },

  // Lấy thông tin link thanh toán cho Registration
  getPaymentUrl: async (registrationId: string) => {
    return axiosClient.get(`/payments/${registrationId}`);
  }
};
