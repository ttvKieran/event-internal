import { create } from 'zustand';

export type EmployeeRole = 'ORGANIZER' | 'EMPLOYEE';

export interface Employee {
  id: string;
  employeeId?: string; // Add this just in case other parts of frontend still use it
  employeeCode: string;
  fullName?: string;
  fullname?: string;
  email: string;
  department?: string | { id: string; name: string; description: string };
  role?: string | { id: string; name: string; description: string };
}

interface AuthState {
  isAuthenticated: boolean;
  token: string | null;
  employee: Employee | null;
  login: (token: string, employee: Employee) => void;
  updateEmployee: (employee: Employee) => void;
  logout: () => void;
}

const getInitialEmployee = (): Employee | null => {
  const saved = localStorage.getItem('employee');
  if (saved) {
    try {
      return JSON.parse(saved);
    } catch (e) {}
  }
  return null;
};

const initialEmp = getInitialEmployee();
const initialToken = localStorage.getItem('accessToken');

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: !!initialToken && !!initialEmp,
  token: initialToken,
  employee: initialEmp,
  
  login: (token, employee) => {
    localStorage.setItem('accessToken', token);
    localStorage.setItem('employee', JSON.stringify(employee));
    set({ isAuthenticated: true, token, employee });
  },
  
  updateEmployee: (employee) => {
    localStorage.setItem('employee', JSON.stringify(employee));
    set({ employee });
  },
  
  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('employee');
    set({ isAuthenticated: false, token: null, employee: null });
  }
}));
