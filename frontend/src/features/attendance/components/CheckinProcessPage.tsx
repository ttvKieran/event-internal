import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/store/useAuthStore';
import { attendanceApi } from '../api/attendanceApi';
import { CheckCircle, XCircle, Loader2, ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { toast } from 'sonner';

export const CheckinProcessPage = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { employee } = useAuthStore();
  
  const eventId = searchParams.get('eventId');
  const token = searchParams.get('token');
  
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    let mounted = true;

    const processCheckIn = async () => {
      if (!eventId || !token) {
        if (mounted) {
          setStatus('error');
          setErrorMessage('Đường dẫn không hợp lệ. Thiếu thông tin sự kiện hoặc mã xác thực.');
        }
        return;
      }

      if (!employee) {
        if (mounted) {
          setStatus('error');
          setErrorMessage('Vui lòng đăng nhập để thực hiện Check-in.');
        }
        return;
      }

      try {
        const employeeId = employee.id || employee.employeeId;
        await attendanceApi.scanQr({
          eventId,
          employeeId: employeeId as string,
          qrToken: token
        });
        
        if (mounted) {
          setStatus('success');
          toast.success('Điểm danh thành công!');
        }
      } catch (err: any) {
        if (mounted) {
          setStatus('error');
          setErrorMessage(err.message || 'Check-in thất bại. Mã QR đã hết hạn hoặc bạn không có quyền tham gia.');
          toast.error('Check-in thất bại!');
        }
      }
    };

    // Use a small timeout to let the UI render the loading state nicely
    const timer = setTimeout(() => {
      processCheckIn();
    }, 1000);

    return () => {
      mounted = false;
      clearTimeout(timer);
    };
  }, [eventId, token, employee]);

  return (
    <div className="min-h-[80vh] flex flex-col items-center justify-center p-4">
      <div className="max-w-md w-full bg-white p-8 rounded-2xl shadow-lg text-center border border-gray-100">
        
        {status === 'loading' && (
          <div className="flex flex-col items-center animate-in fade-in zoom-in duration-300">
            <div className="w-24 h-24 bg-indigo-50 rounded-full flex items-center justify-center mb-6">
              <Loader2 className="w-12 h-12 text-indigo-600 animate-spin" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">Đang xử lý...</h2>
            <p className="text-gray-500">Vui lòng chờ trong giây lát để hệ thống ghi nhận lượt tham gia của bạn.</p>
          </div>
        )}

        {status === 'success' && (
          <div className="flex flex-col items-center animate-in fade-in zoom-in duration-500">
            <div className="w-24 h-24 bg-green-50 rounded-full flex items-center justify-center mb-6">
              <CheckCircle className="w-16 h-16 text-green-500" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">Check-in Thành Công!</h2>
            <p className="text-gray-500 mb-8">Hệ thống đã ghi nhận sự có mặt của bạn tại sự kiện. Chúc bạn có một trải nghiệm tuyệt vời!</p>
            <Button onClick={() => navigate('/events')} className="w-full h-12 text-base">
              Về trang chủ
            </Button>
          </div>
        )}

        {status === 'error' && (
          <div className="flex flex-col items-center animate-in fade-in zoom-in duration-300">
            <div className="w-24 h-24 bg-red-50 rounded-full flex items-center justify-center mb-6">
              <XCircle className="w-16 h-16 text-red-500" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">Không thể Check-in</h2>
            <p className="text-red-500 mb-8 bg-red-50 p-3 rounded-lg w-full text-sm">{errorMessage}</p>
            <div className="flex gap-4 w-full">
              <Button variant="outline" onClick={() => navigate(-1)} className="flex-1 h-12">
                <ArrowLeft className="w-4 h-4 mr-2" />
                Quay lại
              </Button>
              <Button onClick={() => window.location.reload()} className="flex-1 h-12">
                Thử lại
              </Button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
