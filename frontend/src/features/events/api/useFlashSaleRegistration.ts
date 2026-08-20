import { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { toast } from 'sonner';
import { registrationApi } from './registrationApi';
import { useAuthStore } from '@/store/useAuthStore';

type RegistrationState = 'IDLE' | 'SUBMITTING' | 'POLLING' | 'SUCCESS' | 'FAILED';

export const useFlashSaleRegistration = (campaignId: string, ticketType: 'FREE' | 'PAID' = 'FREE') => {
  const { employee } = useAuthStore();
  const [regState, setRegState] = useState<RegistrationState>('IDLE');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [registrationId, setRegistrationId] = useState<string>('');
  const [paymentUrl, setPaymentUrl] = useState<string>('');

  // 1. Mutation gửi request Đăng ký (nhận 202 Accepted)
  const submitMutation = useMutation({
    mutationFn: () => registrationApi.registerForEvent(campaignId, ticketType === 'PAID' ? 'vnpay' : 'free'),
    onMutate: () => {
      setRegState('SUBMITTING');
      setErrorMessage('');
    },
    onSuccess: (res) => {
      if (res?.data?.resourceId) {
        setRegistrationId(res.data.resourceId);
      }
      // Khởi động vòng lặp Polling để chờ kết quả ghi nhận từ Redis/Worker
      setRegState('POLLING');
    },
    onError: (error: any) => {
      setRegState('FAILED');
      const msg = error?.message || 'Đã có lỗi xảy ra khi gửi yêu cầu.';
      setErrorMessage(msg);
      toast.error(msg);
    },
  });

  const currentUserId = employee?.employeeId || '999e4567-e89b-12d3-a456-426614174999';

  // 2. Query Polling (Chỉ chạy khi regState === 'POLLING')
  useQuery({
    queryKey: ['registrationStatus', campaignId, currentUserId],
    queryFn: async () => {
      const res = await registrationApi.checkRegistrationStatus(campaignId, currentUserId);
      
      const statusData = res.data || ''; // e.g., "SUCCESS:uuid" or "PENDING"
      
      if (statusData.startsWith('SUCCESS')) {
        const parts = statusData.split(':');
        const regId = parts.length > 1 ? parts[1].trim() : '';
        if (regId) {
          setRegistrationId(regId);
        }

        // Nếu là vé PAID, cần lấy thêm paymentUrl từ Payment Service trước khi chốt polling
        if (ticketType === 'PAID' && regId) {
          try {
            const { paymentApi } = await import('@/api/paymentApi');
            const payRes: any = await paymentApi.getPaymentUrl(regId);
            const url = payRes?.data?.paymentUrl || payRes?.paymentUrl;
            if (url) {
              setPaymentUrl(url);
              setRegState('SUCCESS');
              toast.success('Hệ thống đã xếp chỗ & khởi tạo link VNPay thành công!');
              return res;
            }
          } catch (e) {
            // Payment Service chưa tạo xong transaction, tiếp tục polling ở lần tiếp theo
            return res;
          }
        } else {
          // Vé FREE hoặc đã có regId
          setRegState('SUCCESS');
          toast.success('Hệ thống đã xếp chỗ thành công!');
          return res;
        }
      }
      
      if (statusData === 'NOT_FOUND' || statusData.startsWith('FAILED')) {
        setRegState('FAILED');
        const reason = statusData === 'NOT_FOUND'
          ? 'Không tìm thấy chiến dịch đăng ký hoặc chưa được khởi tạo. Vui lòng kiểm tra lại sự kiện.'
          : (statusData.split(':')[1] || 'Đã hết vé hoặc có lỗi hệ thống.');
        setErrorMessage(reason);
        toast.error('Đăng ký không thành công', { description: reason });
        return res;
      }
      
      // Nếu PENDING, throw error ngầm để React Query retry (tiếp tục polling)
      // HOẶC return null nhưng set refetchInterval. Ở đây ta dùng refetchInterval.
      return res;
    },
    // Chỉ kích hoạt Polling khi đang ở trạng thái POLLING
    enabled: regState === 'POLLING' && !!currentUserId,
    // Polling mỗi 2 giây
    refetchInterval: (query) => {
      // Nếu đã có data và data báo SUCCESS/FAILED thì dừng polling
      if (regState !== 'POLLING') return false;
      return 2000;
    },
    // Không retry nếu lỗi network quá nhiều lúc polling, hoặc có thể để mặc định
  });

  const handleRegister = () => {
    if (!employee) {
      toast.error('Vui lòng đăng nhập để đăng ký vé.');
      return;
    }
    submitMutation.mutate();
  };

  return {
    handleRegister,
    regState,
    registrationId,
    paymentUrl,
    errorMessage,
    isProcessing: regState === 'SUBMITTING' || regState === 'POLLING',
  };
};
