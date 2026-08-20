import { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { CheckCircle2, XCircle, Loader2, ArrowRight } from 'lucide-react';
import { paymentApi } from '@/api/paymentApi';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';

export function PaymentResultPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [success, setSuccess] = useState(false);
  const [message, setMessage] = useState('');
  const [orderId, setOrderId] = useState('');

  useEffect(() => {
    const processPaymentReturn = async () => {
      try {
        const params: Record<string, string> = {};
        searchParams.forEach((value, key) => {
          params[key] = value;
        });

        // Nếu có tham số từ VNPay
        if (params.vnp_ResponseCode) {
          const response: any = await paymentApi.handleVnPayReturn(params);
          if (params.vnp_ResponseCode === '00' || response?.message?.includes('thành công')) {
            setSuccess(true);
            setMessage('Thanh toán đơn hàng qua VNPay thành công!');
          } else {
            setSuccess(false);
            setMessage(response?.message || 'Giao dịch thanh toán thất bại hoặc bị hủy.');
          }
          setOrderId(params.vnp_TxnRef || '');
        } else {
          setSuccess(false);
          setMessage('Không tìm thấy thông tin giao dịch VNPay.');
        }
      } catch (err: any) {
        setSuccess(false);
        setMessage(err?.message || 'Đã xảy ra lỗi khi kiểm tra giao dịch.');
      } finally {
        setLoading(false);
      }
    };

    processPaymentReturn();
  }, [searchParams]);

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <div className="p-4 rounded-2xl bg-red-50 border border-red-100">
          <Loader2 className="h-10 w-10 text-red-600 animate-spin" />
        </div>
        <p className="text-slate-700 font-bold text-base">Đang xác minh kết quả thanh toán từ VNPay...</p>
        <p className="text-slate-400 text-xs">Vui lòng không đóng trình duyệt trong giây lát.</p>
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center min-h-[70vh] p-4">
      <Card className="max-w-md w-full text-center shadow-xl border border-slate-200/90 rounded-2xl overflow-hidden bg-white">
        <div className={`h-2.5 w-full ${success ? 'bg-emerald-500' : 'bg-red-600'}`} />
        
        <CardHeader className="pb-4 pt-6">
          <div className="mx-auto mb-4">
            {success ? (
              <div className="rounded-full bg-emerald-50 p-4 w-20 h-20 flex items-center justify-center mx-auto border border-emerald-200 shadow-inner">
                <CheckCircle2 className="h-12 w-12 text-emerald-600" />
              </div>
            ) : (
              <div className="rounded-full bg-red-50 p-4 w-20 h-20 flex items-center justify-center mx-auto border border-red-200 shadow-inner">
                <XCircle className="h-12 w-12 text-red-600" />
              </div>
            )}
          </div>

          <CardTitle className="text-2xl font-extrabold text-slate-900 tracking-tight">
            {success ? 'Thanh toán Thành công!' : 'Thanh toán Thất bại'}
          </CardTitle>
          <CardDescription className="text-slate-500 text-sm mt-1.5 px-4 leading-relaxed">
            {message}
          </CardDescription>
        </CardHeader>

        <CardContent className="space-y-3.5 text-sm text-slate-600 border-t border-b border-slate-100 py-4 my-2 px-6 bg-slate-50/50">
          {orderId && (
            <div className="flex justify-between items-center">
              <span className="text-slate-400 font-medium">Mã đăng ký:</span>
              <span className="font-mono font-bold text-slate-900 bg-white px-2 py-0.5 rounded border border-slate-200 text-xs">
                {orderId.slice(0, 12)}...
              </span>
            </div>
          )}
          <div className="flex justify-between items-center">
            <span className="text-slate-400 font-medium">Cổng thanh toán:</span>
            <span className="font-bold text-slate-900">VNPay Gateway</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-slate-400 font-medium">Trạng thái Saga:</span>
            <span className={`font-black text-xs px-2.5 py-0.5 rounded-full uppercase tracking-wider ${
              success 
                ? 'bg-emerald-100 text-emerald-700 border border-emerald-300' 
                : 'bg-red-100 text-red-700 border border-red-300'
            }`}>
              {success ? 'CONFIRMED' : 'CANCELLED'}
            </span>
          </div>
        </CardContent>

        <CardFooter className="pt-4 pb-6 px-6">
          <Button 
            className="w-full gap-2 bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white font-bold h-11 shadow-md shadow-red-600/20" 
            onClick={() => navigate('/events')}
          >
            Quay lại Danh sách Sự kiện <ArrowRight className="h-4 w-4" />
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
