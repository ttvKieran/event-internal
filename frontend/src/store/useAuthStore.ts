import { create } from 'zustand';

export type EmployeeRole = 'ORGANIZER' | 'EMPLOYEE';

export interface Employee {
  employeeId: string;
  employeeCode: string;
  fullName: string;
  email: string;
  department: string;
  role: EmployeeRole;
}

interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  employee: Employee | null;
  login: (token: string, employee: Employee) => void;
  logout: () => void;
}

const getInitialEmployee = (): Employee | null => {
  const saved = localStorage.getItem('employee');
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (e) {}
  }
  // Default fallback user so app works seamlessly
  return {
    employeeId: '999e4567-e89b-12d3-a456-426614174999',
    employeeCode: 'NV001',
    fullName: 'Nguyễn Văn A',
    email: 'nguyenvana@viettel.com.vn',
    department: 'Trung tâm Công nghệ Thông tin',
    role: 'EMPLOYEE',
  };
};

const initialEmp = getInitialEmployee();

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: true,
  token: localStorage.getItem('accessToken') || 'mock-jwt-token',
  employee: initialEmp,
  
  login: (token, employee) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('employee', JSON.stringify(employee));
    set({ isAuthenticated: true, token, employee });
  },
  
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employee');
    set({ isAuthenticated: false, token: null, employee: null });
  }
}));
