import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Loader2, AlertTriangle } from 'lucide-react';
import { useEventMutations } from '../api/useEventMutations';
import { type Event } from '@/types/event';

interface ConfirmActionDialogProps {
  event: Event | null;
  actionType: 'PUBLISH' | 'CANCEL' | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ConfirmActionDialog({ event, actionType, open, onOpenChange }: ConfirmActionDialogProps) {
  const { publishMutation, cancelMutation } = useEventMutations();
  const [reason, setReason] = useState('');

  const isPublish = actionType === 'PUBLISH';
  const isPending = publishMutation.isPending || cancelMutation.isPending;

  const handleConfirm = () => {
    if (!event) return;
    
    if (isPublish) {
      publishMutation.mutate(event.eventId, {
        onSuccess: () => onOpenChange(false)
      });
    } else {
      cancelMutation.mutate({ id: event.eventId, reason: reason || 'Hủy bởi Organizer' }, {
        onSuccess: () => {
          onOpenChange(false);
          setReason('');
        }
      });
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className={!isPublish ? "text-red-600 flex items-center gap-2" : ""}>
            {!isPublish && <AlertTriangle className="h-5 w-5" />}
            {isPublish ? 'Công bố sự kiện' : 'Hủy sự kiện'}
          </DialogTitle>
          <DialogDescription>
            {isPublish 
              ? `Bạn có chắc chắn muốn công bố sự kiện "${event?.title}"? Sau khi công bố, nhân viên có thể nhìn thấy và bắt đầu đăng ký nếu đến giờ mở cổng.`
              : `Bạn đang thực hiện Hủy sự kiện "${event?.title}". Vui lòng nhập lý do hủy bên dưới.`}
          </DialogDescription>
        </DialogHeader>

        {!isPublish && (
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <label htmlFor="reason" className="text-sm font-medium">Lý do hủy</label>
              <Input 
                id="reason" 
                placeholder="VD: Diễn giả bị ốm, Lịch trình thay đổi..."
                value={reason}
                onChange={(e) => setReason(e.target.value)}
              />
            </div>
          </div>
        )}
        
        <DialogFooter>
          <Button type="button" variant="outline" onClick={() => onOpenChange(false)} disabled={isPending}>
            Hủy bỏ
          </Button>
          <Button 
            type="button" 
            variant={isPublish ? "default" : "destructive"} 
            onClick={handleConfirm}
            disabled={isPending}
          >
            {isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
            {isPublish ? 'Xác nhận công bố' : 'Chắc chắn Hủy'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
