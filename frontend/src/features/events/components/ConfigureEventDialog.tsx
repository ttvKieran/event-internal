import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Loader2 } from 'lucide-react';
import { useEventMutations } from '../api/useEventMutations';
import { type Event, type TicketType } from '@/types/event';

interface ConfigureEventDialogProps {
  event: Event | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ConfigureEventDialog({ event, open, onOpenChange }: ConfigureEventDialogProps) {
  const { configureMutation } = useEventMutations();

  const [formData, setFormData] = useState({
    ticketType: 'FREE' as TicketType,
    maxParticipants: 100,
    price: 0,
    registrationOpenAt: '',
    registrationCloseAt: '',
  });

  useEffect(() => {
    if (event && open) {
      // Khởi tạo data default dựa trên thời gian event
      // Mở đăng ký trước 7 ngày, đóng trước 1 ngày
      const start = new Date(event.startTime);
      const openDate = new Date(start.getTime() - 7 * 24 * 60 * 60 * 1000);
      const closeDate = new Date(start.getTime() - 1 * 24 * 60 * 60 * 1000);
      
      const toLocalDatetime = (d: Date) => d.toISOString().slice(0, 16); // YYYY-MM-DDThh:mm

      setFormData({
        ticketType: event.ticketType || 'FREE',
        maxParticipants: event.maxParticipants || 100,
        price: event.price || 0,
        registrationOpenAt: event.registrationOpenAt ? toLocalDatetime(new Date(event.registrationOpenAt)) : toLocalDatetime(openDate),
        registrationCloseAt: event.registrationCloseAt ? toLocalDatetime(new Date(event.registrationCloseAt)) : toLocalDatetime(closeDate),
      });
    }
  }, [event, open]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!event) return;

    const payload = {
      ticketType: formData.ticketType,
      maxParticipants: Number(formData.maxParticipants),
      price: formData.ticketType === 'PAID' ? Number(formData.price) : 0,
      registrationOpenAt: new Date(formData.registrationOpenAt).toISOString(),
      registrationCloseAt: new Date(formData.registrationCloseAt).toISOString(),
      allocatedResources: [], // Giả định frontend tạm ẩn phần quản lý tài nguyên phức tạp
    };
    
    configureMutation.mutate({ id: event.eventId, data: payload }, {
      onSuccess: () => {
        onOpenChange(false);
      }
    });
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Cấu hình vé & Đăng ký</DialogTitle>
            <DialogDescription>
              Thiết lập thông tin bán vé cho sự kiện: <strong className="text-slate-900">{event?.title}</strong>
            </DialogDescription>
          </DialogHeader>
          
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <label className="text-sm font-medium">Loại vé</label>
                <Select 
                  value={formData.ticketType} 
                  onValueChange={(val) => setFormData({...formData, ticketType: val as TicketType})}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Chọn loại vé" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="FREE">Miễn phí (FREE)</SelectItem>
                    <SelectItem value="PAID">Trả phí (PAID)</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="grid gap-2">
                <label htmlFor="price" className="text-sm font-medium">Giá vé (VNĐ)</label>
                <Input 
                  id="price" 
                  type="number" 
                  min="0"
                  disabled={formData.ticketType === 'FREE'}
                  value={formData.ticketType === 'FREE' ? 0 : formData.price}
                  onChange={(e) => setFormData({...formData, price: Number(e.target.value)})}
                />
              </div>
            </div>

            <div className="grid gap-2">
              <label htmlFor="maxParticipants" className="text-sm font-medium">Số lượng vé tối đa</label>
              <Input 
                id="maxParticipants" 
                type="number" 
                min="1"
                required 
                value={formData.maxParticipants}
                onChange={(e) => setFormData({...formData, maxParticipants: Number(e.target.value)})}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <label htmlFor="openAt" className="text-sm font-medium">Mở đăng ký</label>
                <Input 
                  id="openAt" 
                  type="datetime-local" 
                  required 
                  value={formData.registrationOpenAt}
                  onChange={(e) => setFormData({...formData, registrationOpenAt: e.target.value})}
                />
              </div>
              <div className="grid gap-2">
                <label htmlFor="closeAt" className="text-sm font-medium">Đóng đăng ký</label>
                <Input 
                  id="closeAt" 
                  type="datetime-local" 
                  required 
                  value={formData.registrationCloseAt}
                  onChange={(e) => setFormData({...formData, registrationCloseAt: e.target.value})}
                />
              </div>
            </div>
          </div>
          
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
              Hủy
            </Button>
            <Button type="submit" disabled={configureMutation.isPending}>
              {configureMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Lưu cấu hình
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
