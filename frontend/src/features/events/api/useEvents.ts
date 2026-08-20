import { useQuery } from '@tanstack/react-query';
import { eventApi } from './eventApi';
import { type EventListApiResponse } from '@/types/event';

export const useEvents = (status?: string) => {
  return useQuery<EventListApiResponse, Error>({
    queryKey: ['events', status],
    queryFn: () => eventApi.getEvents(status),
    // Vì event-service chưa bật hoặc proxy có thể gọi lỗi ban đầu, 
    // chúng ta set số lần retry thấp để tránh đợi quá lâu
    retry: 1, 
    refetchOnWindowFocus: false,
  });
};
