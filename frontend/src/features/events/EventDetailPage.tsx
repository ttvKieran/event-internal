import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, Calendar, MapPin, Users, Ticket, Loader2, CheckCircle2, AlertCircle } from 'lucide-react';
import { toast } from 'sonner';
import { useAuthStore } from '@/store/useAuthStore';
import { registrationApi } from './api/registrationApi';
import { eventApi } from './api/eventApi';
import { useFlashSaleRegistration } from './api/useFlashSaleRegistration';
import { formatDate, formatCurrency } from '@/utils/formatters';
import { getEventBanner, DEFAULT_FALLBACK_BANNERS } from '@/utils/getEventBanner';

import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Skeleton } from '@/components/ui/skeleton';

export function EventDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  // Load event details
  const { data, isLoading, isError } = useQuery({
    queryKey: ['event', id],
    queryFn: () => eventApi.getEventById(id!),
    enabled: !!id,
  });

  const event = data?.data;

  // Use the Flash Sale Hook
  const { handleRegister, regState, registrationId, paymentUrl: fetchedPaymentUrl, errorMessage, isProcessing } = useFlashSaleRegistration(id!, event?.ticketType || 'FREE');

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Button variant="ghost" className="mb-4" disabled>
          <ArrowLeft className="mr-2 h-4 w-4" /> Quay lại
        </Button>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2 space-y-6">
            <Skeleton className="h-64 w-full rounded-xl" />
            <Skeleton className="h-10 w-3/4" />
            <Skeleton className="h-4 w-full" />
            <Skeleton className="h-4 w-full" />
          </div>
          <div className="space-y-6">
            <Skeleton className="h-64 w-full rounded-xl" />
          </div>
        </div>
      </div>
    );
  }

  if (isError || !data?.data) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-center">
        <AlertCircle className="h-12 w-12 text-red-500 mb-4" />
        <h2 className="text-2xl font-bold">Không tìm thấy sự kiện</h2>
        <p className="text-slate-500 mt-2">Sự kiện không tồn tại hoặc bạn không có quyền truy cập.</p>
        <Button variant="outline" className="mt-6" onClick={() => navigate('/events')}>
          Quay lại danh sách
        </Button>
      </div>
    );
  }

  // Render logic for Registration Button based on regState
  const renderRegisterButton = () => {
    if (regState === 'SUCCESS') {
      if (event.ticketType === 'PAID') {
        return (
          <div className="space-y-4">
            <div className="bg-amber-50 border border-amber-200 text-amber-800 p-4 rounded-lg flex items-start gap-3">
              <CheckCircle2 className="h-5 w-5 text-amber-600 mt-0.5" />
              <div>
                <p className="font-semibold">Đã giữ chỗ thành công (RESERVED)!</p>
                <p className="text-sm mt-1">
                  Vé của bạn đang được giữ chỗ trong <strong>15 phút</strong>. Vui lòng hoàn tất thanh toán qua VNPay để xác nhận vé (CONFIRMED).
                </p>
              </div>
            </div>

            <Button 
              className="w-full bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white font-bold h-12 text-base shadow-lg shadow-red-600/25 transition-all"
              onClick={async () => {
                if (fetchedPaymentUrl) {
                  window.location.href = fetchedPaymentUrl;
                  return;
                }

                if (!registrationId) {
                  toast.error('Hệ thống chưa hoàn tất ghi nhận đơn đăng ký. Vui lòng thử lại sau giây lát.');
                  return;
                }

                try {
                  const { paymentApi } = await import('@/api/paymentApi');
                  const res: any = await paymentApi.getPaymentUrl(registrationId);
                  const url = res?.data?.paymentUrl || res?.paymentUrl;
                  if (url) {
                    window.location.href = url;
                  } else {
                    toast.error('Chưa tạo được link VNPay. Vui lòng kiểm tra lại Payment Service.');
                  }
                } catch (err: any) {
                  toast.error(err?.message || 'Chưa khởi tạo giao dịch thanh toán hoặc Payment Service chưa mở.');
                }
              }}
            >
              Thanh toán ngay qua VNPay
            </Button>
            
            <Button variant="outline" className="w-full" onClick={() => navigate('/events')}>
              Quay lại danh sách
            </Button>
          </div>
        );
      }

      return (
        <div className="space-y-4">
          <div className="bg-green-50 border border-green-200 text-green-700 p-4 rounded-lg flex items-start gap-3">
            <CheckCircle2 className="h-5 w-5 mt-0.5" />
            <div>
              <p className="font-semibold">Đăng ký thành công!</p>
              <p className="text-sm mt-1">Bạn đã đăng ký giữ chỗ thành công cho sự kiện miễn phí này.</p>
            </div>
          </div>
          <Button variant="outline" className="w-full" onClick={() => navigate('/events')}>
            Quay lại danh sách
          </Button>
        </div>
      );
    }

    return (
      <div className="space-y-4">
        <Button 
          className="w-full bg-gradient-to-r from-red-600 to-rose-600 hover:from-red-700 hover:to-rose-700 text-white font-bold text-lg h-12 shadow-lg shadow-red-600/25 active:scale-[0.99] transition-all" 
          size="lg"
          onClick={handleRegister}
          disabled={isProcessing}
        >
          {isProcessing ? (
            <>
              <Loader2 className="mr-2 h-5 w-5 animate-spin" />
              {regState === 'POLLING' ? 'Đang xếp hàng & chuẩn bị link VNPay...' : 'Đang xử lý...'}
            </>
          ) : (
            'Đăng ký giữ chỗ ngay'
          )}
        </Button>
        {errorMessage && (
          <p className="text-sm text-red-500 text-center">{errorMessage}</p>
        )}
      </div>
    );
  };

  return (
    <div className="space-y-6 pb-12">
      <Button variant="ghost" className="mb-2 -ml-4 hover:bg-transparent text-slate-500 hover:text-slate-900" onClick={() => navigate('/events')}>
        <ArrowLeft className="mr-2 h-4 w-4" /> Quay lại danh sách
      </Button>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-8">
          <div className="aspect-[21/9] bg-slate-100 rounded-xl overflow-hidden relative border border-slate-200 shadow-sm">
            <img 
              src={getEventBanner(event.eventId)} 
              alt={event.title} 
              className="w-full h-full object-cover"
              onError={(e) => {
                const img = e.target as HTMLImageElement;
                if (img.src.endsWith('.png')) {
                  img.src = img.src.replace('.png', '.jpg');
                } else {
                  const hash = event.eventId ? Math.abs(event.eventId.charCodeAt(0)) % 5 : 0;
                  img.src = DEFAULT_FALLBACK_BANNERS[hash];
                }
              }}
            />
          </div>
          
          <div>
            <div className="flex items-center gap-3 mb-4">
              <Badge variant={event.ticketType === 'FREE' ? 'secondary' : 'default'} className="text-sm">
                {event.ticketType === 'FREE' ? 'Vé Miễn phí' : 'Vé Trả phí'}
              </Badge>
              <Badge variant="outline" className="text-sm">
                {event.status}
              </Badge>
            </div>
            <h1 className="text-3xl sm:text-4xl font-bold tracking-tight text-slate-900 mb-4">
              {event.title}
            </h1>
            <p className="text-lg text-slate-600 leading-relaxed whitespace-pre-wrap">
              {event.description}
            </p>
          </div>
        </div>

        {/* Sidebar / Ticket Card */}
        <div>
          <div className="sticky top-24 rounded-xl border border-slate-200 bg-white p-6 shadow-sm space-y-6">
            <h3 className="font-semibold text-lg border-b pb-4">Thông tin đăng ký</h3>
            
            <div className="space-y-4">
              <div className="flex items-start gap-3">
                <Calendar className="h-5 w-5 text-slate-400 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-slate-900">Thời gian</p>
                  <p className="text-sm text-slate-500">{formatDate(event.startTime)}</p>
                </div>
              </div>
              
              <div className="flex items-start gap-3">
                <MapPin className="h-5 w-5 text-slate-400 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-slate-900">Địa điểm</p>
                  <p className="text-sm text-slate-500">{event.location}</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Users className="h-5 w-5 text-slate-400 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-slate-900">Số lượng giới hạn</p>
                  <p className="text-sm text-slate-500">{event.maxParticipants || 0} người tham gia</p>
                </div>
              </div>

              <div className="flex items-start gap-3">
                <Ticket className="h-5 w-5 text-red-600 mt-0.5" />
                <div>
                  <p className="text-sm font-medium text-slate-900">Giá vé</p>
                  <p className="text-2xl font-black text-red-600">
                    {event.ticketType === 'FREE' ? 'MIỄN PHÍ' : formatCurrency(event.price)}
                  </p>
                </div>
              </div>
            </div>

            <div className="pt-4 border-t">
              {renderRegisterButton()}
            </div>
            
            <p className="text-xs text-center text-slate-400">
              Bằng việc đăng ký, bạn đồng ý với các quy định tham gia sự kiện nội bộ của công ty.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
