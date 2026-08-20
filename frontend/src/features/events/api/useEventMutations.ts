import { useMutation, useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';
import { eventApi, type CreateEventPayload, type ConfigureEventPayload } from './eventApi';

export const useEventMutations = () => {
  const queryClient = useQueryClient();

  // Tạo sự kiện
  const createMutation = useMutation({
    mutationFn: (data: CreateEventPayload) => eventApi.createEvent(data),
    onSuccess: () => {
      toast.success('Tạo sự kiện thành công!');
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi tạo sự kiện.');
    }
  });

  // Cấu hình sự kiện
  const configureMutation = useMutation({
    mutationFn: ({ id, data }: { id: string, data: ConfigureEventPayload }) => eventApi.configureEvent(id, data),
    onSuccess: () => {
      toast.success('Cấu hình sự kiện thành công!');
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi cấu hình.');
    }
  });

  // Công bố sự kiện
  const publishMutation = useMutation({
    mutationFn: (id: string) => eventApi.publishEvent(id),
    onSuccess: () => {
      toast.success('Công bố sự kiện thành công!');
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi công bố.');
    }
  });

  // Hủy sự kiện
  const cancelMutation = useMutation({
    mutationFn: ({ id, reason }: { id: string, reason: string }) => eventApi.cancelEvent(id, reason),
    onSuccess: () => {
      toast.success('Hủy sự kiện thành công!');
      queryClient.invalidateQueries({ queryKey: ['events'] });
    },
    onError: (error: any) => {
      toast.error(error?.response?.data?.message || 'Có lỗi xảy ra khi hủy.');
    }
  });

  return {
    createMutation,
    configureMutation,
    publishMutation,
    cancelMutation,
  };
};
