import { axiosClient } from '@/api/axiosClient';
import { type EventListApiResponse, type EventApiResponse, type TicketType } from '@/types/event';

export interface CreateEventPayload {
  title: string;
  description?: string;
  startTime: string;
  endTime: string;
  location: string;
}

export interface ConfigureEventPayload {
  ticketType: TicketType;
  maxParticipants: number;
  price?: number;
  registrationOpenAt: string;
  registrationCloseAt: string;
  allocatedResources: any[];
}

export const eventApi = {
  // Lấy danh sách sự kiện (Dành cho cả Employee và Organizer)
  getEvents: async (status?: string): Promise<EventListApiResponse> => {
    // /events endpoint will be proxied to event-service
    return axiosClient.get('/events', {
      params: {
        ...(status ? { status } : {}),
        page: 0,
        size: 50,
      }
    });
  },

  // Lấy chi tiết sự kiện
  getEventById: async (eventId: string): Promise<EventApiResponse> => {
    return axiosClient.get(`/events/${eventId}`);
  },
  
  // Tạo sự kiện mới
  createEvent: async (payload: CreateEventPayload): Promise<EventApiResponse> => {
    return axiosClient.post('/events', payload);
  },

  // Cấu hình vé sự kiện
  configureEvent: async (eventId: string, payload: ConfigureEventPayload): Promise<EventApiResponse> => {
    return axiosClient.put(`/events/${eventId}/details`, payload);
  },

  // (Dành cho Organizer) Công bố sự kiện
  publishEvent: async (eventId: string): Promise<EventApiResponse> => {
    return axiosClient.post(`/events/${eventId}/publish`);
  },

  // Hủy sự kiện
  cancelEvent: async (eventId: string, reason: string): Promise<any> => {
    return axiosClient.post(`/events/${eventId}/cancel`, { reason });
  }
};
