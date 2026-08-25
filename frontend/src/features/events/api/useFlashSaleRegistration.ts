import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { registrationApi } from './registrationApi';
import { useAuthStore } from '@/store/useAuthStore';

type RegistrationState = 'IDLE' | 'SUBMITTING' | 'POLLING' | 'SUCCESS' | 'FAILED';

export const useFlashSaleRegistration = (campaignId: string, ticketType: 'FREE' | 'PAID' = 'FREE') => {
  const { employee } = useAuthStore();
  const queryClient = useQueryClient();
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

  const currentUserId = employee?.id || employee?.employeeId;

  const registrationsQuery = useQuery({
    queryKey: ['registrations', campaignId, currentUserId],
    queryFn: () => registrationApi.getUserRegistrations(currentUserId!, campaignId),
    enabled: !!campaignId && !!currentUserId,
  });

  const activeRegistration = registrationsQuery.data?.data.find(
    (registration) =>
      registration.status === 'RESERVED' || registration.status === 'CONFIRMED',
  );

  // 2. Query Polling (Chỉ chạy khi regState === 'POLLING')
  useQuery({
    queryKey: ['registrationStatus', campaignId, currentUserId],
    queryFn: async () => {
      const res = await registrationApi.checkRegistrationStatus(campaignId, currentUserId!);
      
      const statusData = res.data || ''; // e.g., "SUCCESS:uuid" or "PENDING"
      
      if (statusData.startsWith('SUCCESS')) {
        const parts = statusData.split(':');
        const regId = parts.length > 1 ? parts[1].trim() : '';
        if (regId) {
          setRegistrationId(regId);
          void queryClient.invalidateQueries({
            queryKey: ['registrations', campaignId, currentUserId],
          });
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
          } catch {
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
    refetchInterval: () => {
      // Nếu đã có data và data báo SUCCESS/FAILED thì dừng polling
      if (regState !== 'POLLING') return false;
      return 2000;
    },
    // Không retry nếu lỗi network quá nhiều lúc polling, hoặc có thể để mặc định
  });

  const cancelMutation = useMutation({
    mutationFn: (reason: string) => {
      if (ticketType !== 'FREE') {
        throw new Error('Vé trả phí hiện chưa hỗ trợ hủy trực tuyến.');
      }
      const id = registrationId || activeRegistration?.registrationId;
      if (!id) {
        throw new Error('Không tìm thấy đơn đăng ký cần hủy.');
      }
      return registrationApi.cancelRegistration(id, reason);
    },
    onSuccess: async () => {
      setRegState('IDLE');
      setRegistrationId('');
      setPaymentUrl('');
      setErrorMessage('');
      await queryClient.invalidateQueries({
        queryKey: ['registrations', campaignId, currentUserId],
      });
      toast.success('Đã hủy đăng ký và hoàn trả slot vé.');
    },
    onError: (error: any) => {
      const message = error?.message || 'Không thể hủy đăng ký.';
      toast.error(message);
    },
  });

  const handleRegister = () => {
    if (!employee) {
      toast.error('Vui lòng đăng nhập để đăng ký vé.');
      return;
    }
    if (activeRegistration) {
      toast.info('Bạn đã đăng ký sự kiện này.');
      return;
    }
    submitMutation.mutate();
  };

  const effectiveRegistrationId = registrationId || activeRegistration?.registrationId || '';
  const effectiveStatus = activeRegistration?.status || (
    regState === 'SUCCESS'
      ? ticketType === 'PAID' ? 'RESERVED' : 'CONFIRMED'
      : undefined
  );

  return {
    handleRegister,
    cancelRegistration: (reason: string) => cancelMutation.mutateAsync(reason),
    regState,
    registrationId: effectiveRegistrationId,
    registrationStatus: effectiveStatus,
    paymentUrl,
    errorMessage,
    hasActiveRegistration: !!activeRegistration || regState === 'SUCCESS',
    isCheckingRegistration: registrationsQuery.isLoading,
    isCancelling: cancelMutation.isPending,
    isProcessing: regState === 'SUBMITTING' || regState === 'POLLING',
  };
};
