import { type Employee, type EmployeeRole } from '../store/useAuthStore';

// MOCK DATA based on docs/api-specs/iam-service.yaml
const mockEmployeeData: Employee = {
  employeeId: 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
  employeeCode: 'EMP00123',
  fullName: 'Nguyễn Văn An',
  email: 'an.nguyen@company.vn',
  department: 'Trung tâm Phân tích Dữ liệu',
  role: 'ORGANIZER', // Change this to EMPLOYEE to test the other role
};

export const authApi = {
  login: async (employeeCode: string, password: string):Promise<{token: string, employee: Employee}> => {
    // Giả lập network delay
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (employeeCode === 'admin' && password === 'admin') {
          resolve({
            token: 'mock-jwt-token-ey...',
            employee: { ...mockEmployeeData, role: 'ORGANIZER' }
          });
        } else if (employeeCode === 'user' && password === 'user') {
          resolve({
            token: 'mock-jwt-token-ey...',
            employee: { ...mockEmployeeData, role: 'EMPLOYEE', employeeCode: 'EMP99999' }
          });
        } else {
          reject({
            success: false,
            code: 'INVALID_CREDENTIALS',
            message: 'Mã nhân viên hoặc mật khẩu không chính xác.'
          });
        }
      }, 1000);
    });
  }
};
