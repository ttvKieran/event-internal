export interface DashboardMetrics {
  eventId: string;
  registrationCount: number;
  freeRegistrationCount: number;
  paidRegistrationCount: number;
  cancelledCount: number;
  revenue: number;
  failedPaymentRate: number;
  checkInRate: number;
  updatedAt: string;
}

export interface RegistrationMetrics {
  eventId: string;
  totalRegistrations: number;
  freeCount: number;
  paidCount: number;
  fillRate: number;
  updatedAt: string;
}

export interface AttendanceMetrics {
  eventId: string;
  checkedInCount: number;
  checkInRate: number;
  updatedAt: string;
}

