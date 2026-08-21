import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Settings, Globe, Trash } from 'lucide-react';
import { useEvents } from './api/useEvents';
import { formatDate } from '@/utils/formatters';
import { type Event } from '@/types/event';

import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';

import { CreateEventDialog } from './components/CreateEventDialog';
import { ConfigureEventDialog } from './components/ConfigureEventDialog';
import { ConfirmActionDialog } from './components/ConfirmActionDialog';

export function OrganizerEventList() {
  const navigate = useNavigate();
  const { data, isLoading, isError } = useEvents();

  const [selectedEvent, setSelectedEvent] = useState<Event | null>(null);
  const [configOpen, setConfigOpen] = useState(false);
  const [actionOpen, setActionOpen] = useState(false);
  const [actionType, setActionType] = useState<'PUBLISH' | 'CANCEL' | null>(null);

  const openConfig = (e: React.MouseEvent, event: Event) => {
    e.stopPropagation();
    setSelectedEvent(event);
    setConfigOpen(true);
  };

  const openAction = (e: React.MouseEvent, event: Event, type: 'PUBLISH' | 'CANCEL') => {
    e.stopPropagation();
    setSelectedEvent(event);
    setActionType(type);
    setActionOpen(true);
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'DRAFT': return <Badge variant="outline" className="text-slate-500">Nháp</Badge>;
      case 'CONFIGURED': return <Badge variant="secondary" className="bg-blue-100 text-blue-800 hover:bg-blue-200">Đã cấu hình</Badge>;
      case 'PUBLISHED': return <Badge className="bg-green-600 hover:bg-green-700">Đã công bố</Badge>;
      case 'CANCELLED': return <Badge variant="destructive">Đã hủy</Badge>;
      case 'STARTED': return <Badge className="bg-amber-500 hover:bg-amber-600">Đang diễn ra</Badge>;
      case 'ENDED': return <Badge variant="outline" className="bg-slate-100">Đã kết thúc</Badge>;
      default: return <Badge variant="outline">{status}</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">Quản lý sự kiện</h2>
          <p className="text-slate-500">Quản lý các sự kiện, cấu hình vé và theo dõi trạng thái.</p>
        </div>
        <CreateEventDialog />
      </div>

      <div className="rounded-md border bg-white shadow-sm overflow-hidden">
        <Table>
          <TableHeader className="bg-slate-50">
            <TableRow>
              <TableHead className="w-[300px]">Tên sự kiện</TableHead>
              <TableHead>Trạng thái</TableHead>
              <TableHead>Thời gian</TableHead>
              <TableHead>Loại vé</TableHead>
              <TableHead className="text-right">Hành động</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {isLoading ? (
              // Loading State
              Array.from({ length: 5 }).map((_, i) => (
                <TableRow key={i}>
                  <TableCell><Skeleton className="h-5 w-3/4" /><Skeleton className="h-4 w-1/2 mt-2" /></TableCell>
                  <TableCell><Skeleton className="h-6 w-24 rounded-full" /></TableCell>
                  <TableCell><Skeleton className="h-4 w-32" /><Skeleton className="h-4 w-24 mt-2" /></TableCell>
                  <TableCell><Skeleton className="h-5 w-16" /></TableCell>
                  <TableCell className="text-right"><Skeleton className="h-8 w-8 inline-block rounded-md" /></TableCell>
                </TableRow>
              ))
            ) : isError ? (
              // Error State
              <TableRow>
                <TableCell colSpan={5} className="h-32 text-center text-slate-500">
                  Lỗi kết nối tới Event Service. Vui lòng kiểm tra lại.
                </TableCell>
              </TableRow>
            ) : !data?.data || data.data.length === 0 ? (
              // Empty State
              <TableRow>
                <TableCell colSpan={5} className="h-32 text-center text-slate-500">
                  Bạn chưa có sự kiện nào. Hãy bấm "Tạo sự kiện mới" để bắt đầu.
                </TableCell>
              </TableRow>
            ) : (
              // Data rows
              data.data.map((event) => (
                <TableRow key={event.eventId} className="hover:bg-slate-50 transition-colors cursor-pointer" onClick={() => navigate(`/admin/events/${event.eventId}`)}>
                  <TableCell className="font-medium">
                    <div className="line-clamp-1">{event.title}</div>
                    <div className="text-xs text-slate-500 mt-1 font-normal line-clamp-1">{event.location}</div>
                  </TableCell>
                  <TableCell>
                    {getStatusBadge(event.status)}
                  </TableCell>
                  <TableCell>
                    <div className="text-sm">{formatDate(event.startTime)}</div>
                  </TableCell>
                  <TableCell>
                    {event.ticketType ? (
                      <span className="text-sm font-medium">{event.ticketType}</span>
                    ) : (
                      <span className="text-xs text-slate-400 italic">Chưa cấu hình</span>
                    )}
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2" onClick={(e) => e.stopPropagation()}>
                      {event.status === 'DRAFT' && (
                        <Button variant="outline" size="icon" title="Cấu hình sự kiện" onClick={(e) => openConfig(e, event)}>
                          <Settings className="h-4 w-4 text-slate-600" />
                        </Button>
                      )}
                      {event.status === 'CONFIGURED' && (
                        <Button variant="outline" size="icon" title="Công bố sự kiện" className="text-blue-600 hover:text-blue-700 hover:bg-blue-50 border-blue-200" onClick={(e) => openAction(e, event, 'PUBLISH')}>
                          <Globe className="h-4 w-4" />
                        </Button>
                      )}
                      {['DRAFT', 'CONFIGURED', 'PUBLISHED'].includes(event.status) && (
                        <Button variant="ghost" size="icon" title="Hủy sự kiện" className="text-red-600 hover:text-red-700 hover:bg-red-50" onClick={(e) => openAction(e, event, 'CANCEL')}>
                          <Trash className="h-4 w-4" />
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </div>

      <ConfigureEventDialog 
        event={selectedEvent} 
        open={configOpen} 
        onOpenChange={setConfigOpen} 
      />
      
      <ConfirmActionDialog 
        event={selectedEvent} 
        actionType={actionType} 
        open={actionOpen} 
        onOpenChange={setActionOpen} 
      />
    </div>
  );
}
