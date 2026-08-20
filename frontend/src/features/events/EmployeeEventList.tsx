import { useNavigate } from 'react-router-dom';
import { Calendar, MapPin, Users, Ticket } from 'lucide-react';
import { useEvents } from './api/useEvents';
import { formatDate, formatCurrency } from '@/utils/formatters';
import { getEventBanner, DEFAULT_FALLBACK_BANNERS } from '@/utils/getEventBanner';

import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Skeleton } from '@/components/ui/skeleton';

export function EmployeeEventList() {
  const navigate = useNavigate();
  // Chỉ tải các sự kiện đã PUBLISHED
  const { data, isLoading, isError } = useEvents('PUBLISHED');

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Sự kiện sắp diễn ra</h2>
          <p className="text-slate-500">Đăng ký tham gia các sự kiện nội bộ mới nhất của VTIT.</p>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Card key={i} className="flex flex-col h-full overflow-hidden">
              <Skeleton className="h-48 w-full rounded-none" />
              <CardHeader className="space-y-2">
                <Skeleton className="h-4 w-20" />
                <Skeleton className="h-6 w-full" />
              </CardHeader>
              <CardContent className="space-y-2 flex-1">
                <Skeleton className="h-4 w-full" />
                <Skeleton className="h-4 w-2/3" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="flex flex-col items-center justify-center py-12 text-center">
        <div className="rounded-full bg-red-100 p-3 mb-4">
          <Calendar className="h-6 w-6 text-red-600" />
        </div>
        <h3 className="text-lg font-semibold text-slate-900">Không thể tải danh sách sự kiện</h3>
        <p className="text-slate-500 max-w-md mt-2">
          Hệ thống đang gặp sự cố khi kết nối đến Event Service. Vui lòng kiểm tra lại kết nối mạng hoặc thử lại sau.
        </p>
        <Button variant="outline" className="mt-6" onClick={() => window.location.reload()}>
          Thử lại
        </Button>
      </div>
    );
  }

  const events = data?.data || [];

  return (
    <div className="space-y-6">
      {/* Enterprise Hero Banner */}
      <div className="relative overflow-hidden rounded-lg bg-slate-900 text-white p-7 sm:p-8 shadow-md border border-slate-800">
        <div className="absolute top-0 right-0 -mt-10 -mr-10 w-60 h-60 rounded-full bg-red-600/15 blur-2xl pointer-events-none" />
        <div className="relative z-10 max-w-2xl space-y-2">
          <div className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded bg-red-600/20 border border-red-500/30 text-red-400 text-[11px] font-bold uppercase tracking-wider">
            <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse" />
            Viettel Software Internal Portal
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-white">
            Sự kiện & Khóa đào tạo Nội bộ
          </h1>
          <p className="text-slate-300 text-xs sm:text-sm leading-relaxed">
            Hệ thống đăng ký sự kiện và quản lý vé giữ chỗ dành riêng cho cán bộ nhân viên Viettel Software.
          </p>
        </div>
      </div>

      {events.length === 0 ? (
        <div className="rounded-lg border border-dashed border-slate-300 bg-white p-12 text-center shadow-xs">
          <Calendar className="mx-auto h-10 w-10 text-slate-300" />
          <h3 className="mt-4 text-base font-bold text-slate-800">Chưa có sự kiện nào mở đăng ký</h3>
          <p className="text-slate-500 mt-1 text-xs">Vui lòng quay lại sau khi Ban tổ chức đăng tải sự kiện mới.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {events.map((event) => (
            <Card 
              key={event.eventId} 
              className="group flex flex-col overflow-hidden rounded-lg bg-white border border-slate-200 hover:border-red-500/50 hover:shadow-lg transition-all duration-200"
            >
              {/* Image Banner */}
              <div className="aspect-[16/9] bg-slate-100 relative overflow-hidden">
                <img 
                  src={getEventBanner(event.eventId)} 
                  alt={event.title} 
                  className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
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
                <div className="absolute top-2.5 right-2.5 flex gap-2">
                  <Badge 
                    className={`font-bold text-xs rounded shadow-xs ${
                      event.ticketType === 'FREE' 
                        ? 'bg-slate-900 text-white' 
                        : 'bg-red-600 text-white'
                    }`}
                  >
                    {event.ticketType === 'FREE' ? 'Vé Miễn Phí' : formatCurrency(event.price)}
                  </Badge>
                </div>
              </div>

              {/* Card Content */}
              <CardHeader className="pb-2 pt-4 px-4">
                <CardTitle className="line-clamp-2 text-base font-bold text-slate-900 group-hover:text-red-600 transition-colors leading-snug">
                  {event.title}
                </CardTitle>
                <CardDescription className="line-clamp-2 text-xs text-slate-500 mt-1 leading-normal">
                  {event.description}
                </CardDescription>
              </CardHeader>

              <CardContent className="flex-1 pb-3 px-4">
                <div className="space-y-2 text-xs text-slate-600 font-medium">
                  <div className="flex items-center gap-2">
                    <Calendar className="h-3.5 w-3.5 text-red-600 shrink-0" />
                    <span>{formatDate(event.startTime)}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <MapPin className="h-3.5 w-3.5 text-red-600 shrink-0" />
                    <span className="line-clamp-1">{event.location}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Users className="h-3.5 w-3.5 text-slate-400 shrink-0" />
                    <span>Tối đa {event.maxParticipants || 0} người</span>
                  </div>
                </div>
              </CardContent>

              {/* Footer CTA Button */}
              <CardFooter className="border-t border-slate-100 bg-slate-50/60 p-3">
                <Button 
                  className="w-full gap-1.5 bg-slate-900 hover:bg-red-600 text-white font-semibold h-9 text-xs rounded transition-colors" 
                  onClick={() => navigate(`/events/${event.eventId}`)}
                >
                  <Ticket className="h-3.5 w-3.5" />
                  Xem chi tiết & Đăng ký
                </Button>
              </CardFooter>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
