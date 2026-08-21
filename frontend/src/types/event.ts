export type EventStatus = 'DRAFT' | 'CONFIGURED' | 'PUBLISHED' | 'CANCELLED' | 'STARTED' | 'ENDED';
export type TicketType = 'FREE' | 'PAID';

export interface EventResourceAllocation {
  id: string;
  note: string;
  resourceId: string;
  quantity: number;
}

export interface Event {
  eventId: string;
  title: string;
  description: string;
  startTime: string; // ISO Date String
  endTime: string;
  location: string;
  ticketType?: TicketType;
  maxParticipants?: number;
  price?: number;
  allocatedResources?: EventResourceAllocation[];
  status: EventStatus;
  registrationOpenAt?: string;
  registrationCloseAt?: string;
  createdAt: string;
}

export interface EventListApiResponse {
  success: boolean;
  code: string;
  message: string;
  data: Event[];
  timestamp: string;
}

export interface EventApiResponse {
  success: boolean;
  code: string;
  message: string;
  data: Event;
  timestamp: string;
}
