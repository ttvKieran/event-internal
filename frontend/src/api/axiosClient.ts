import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';

// The base URL uses relative path to trigger Vite Proxy (bypassing CORS)
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const axiosClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Add a request interceptor
axiosClient.interceptors.request.use(
  (config) => {
    // === MOCK IAM SERVICE: /auth/login ===
    // If the request is to login, we bypass the actual API call using a custom adapter or handling it
    // Wait, axios interceptor can't easily return a mock response without throwing or using a custom adapter.
    // Let's attach token from Zustand/LocalStorage if it exists
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    // Dynamic X-Employee-Id header from AuthStore or Fallback
    const authState = useAuthStore.getState();
    const currentEmpId = authState.employee?.employeeId || '999e4567-e89b-12d3-a456-426614174999';
    config.headers['X-Employee-Id'] = currentEmpId;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add a response interceptor
axiosClient.interceptors.response.use(
  (response) => {
    // Only return the data part of the response if it matches your ApiResponse structure
    // Assuming ApiResponse<T> has { success, code, message, data, timestamp }
    return response.data;
  },
  (error) => {
    // Handle global errors here (e.g., 401 Unauthorized -> redirect to login)
    return Promise.reject(error.response?.data || error.message);
  }
);
