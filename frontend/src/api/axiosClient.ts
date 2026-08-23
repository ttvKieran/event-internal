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
    // Attach token from LocalStorage if it exists
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
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
