import { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Plus, Loader2 } from 'lucide-react';
import { useEventMutations } from '../api/useEventMutations';

export function CreateEventDialog() {
  const [open, setOpen] = useState(false);
  const { createMutation } = useEventMutations();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    location: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Convert to ISO string if needed by backend, though datetime-local is usually YYYY-MM-DDThh:mm
    const payload = {
      ...formData,
      startTime: new Date(formData.startTime).toISOString(),
      endTime: new Date(formData.endTime).toISOString(),
    };
    
    createMutation.mutate(payload, {
      onSuccess: () => {
        setOpen(false);
        setFormData({ title: '', description: '', startTime: '', endTime: '', location: '' });
      }
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger
        render={
          <Button className="gap-2">
            <Plus className="h-4 w-4" />
            Tạo sự kiện mới
          </Button>
        }
      />
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Tạo sự kiện mới</DialogTitle>
            <DialogDescription>
              Nhập thông tin cơ bản của sự kiện. Bạn có thể cấu hình vé sau khi tạo.
            </DialogDescription>
          </DialogHeader>
          
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <label htmlFor="title" className="text-sm font-medium">Tên sự kiện</label>
              <Input 
                id="title" 
                required 
                placeholder="VD: VTIT Tech Talk 2026"
                value={formData.title}
                onChange={(e) => setFormData({...formData, title: e.target.value})}
              />
            </div>
            
            <div className="grid gap-2">
              <label htmlFor="location" className="text-sm font-medium">Địa điểm</label>
              <Input 
                id="location" 
                required 
                placeholder="VD: Tòa nhà Viettel"
                value={formData.location}
                onChange={(e) => setFormData({...formData, location: e.target.value})}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <label htmlFor="startTime" className="text-sm font-medium">Bắt đầu</label>
                <Input 
                  id="startTime" 
                  type="datetime-local" 
                  required 
                  value={formData.startTime}
                  onChange={(e) => setFormData({...formData, startTime: e.target.value})}
                />
              </div>
              <div className="grid gap-2">
                <label htmlFor="endTime" className="text-sm font-medium">Kết thúc</label>
                <Input 
                  id="endTime" 
                  type="datetime-local" 
                  required 
                  value={formData.endTime}
                  onChange={(e) => setFormData({...formData, endTime: e.target.value})}
                />
              </div>
            </div>

            <div className="grid gap-2">
              <label htmlFor="description" className="text-sm font-medium">Mô tả chi tiết</label>
              <Textarea 
                id="description" 
                rows={3}
                placeholder="Nhập nội dung mô tả sự kiện..."
                value={formData.description}
                onChange={(e) => setFormData({...formData, description: e.target.value})}
              />
            </div>
          </div>
          
          <DialogFooter>
            <Button type="button" variant="outline" onClick={() => setOpen(false)}>
              Hủy
            </Button>
            <Button type="submit" disabled={createMutation.isPending}>
              {createMutation.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
              Tạo sự kiện
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
