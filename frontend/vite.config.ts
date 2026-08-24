import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import path from 'node:path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  server: {
    proxy: {
      // 1. Chuyển tất cả Event Service qua Gateway (8000)
      '/api/v1/events': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      '/api/v1/campaigns': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 2. Chuyển Registration Service qua Gateway (8000)
      '/api/v1/registrations': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 3. Chuyển Payment Service qua Gateway (8000)
      '/api/v1/payments': {
        target: 'http://localhost:8000',
        changeOrigin: true,
      },
      // 4. IAM Service qua Gateway (Đã cấu hình sẵn rewrite)
      '/api/v1/auth': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/auth/, '/v1/auth'),
      },
      '/api/v1/Employees': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/Employees/, '/v1/Employees'),
      },
      // 5. MỚI THÊM: Chuyển Analytics Service qua Gateway
      // Frontend gọi /api/v1/analytics/... -> Gateway nhận /v1/analytics/...
      '/api/v1/analytics': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/analytics/, '/v1/analytics'),
      },
      // 6. Chuyển Attendance Service qua Gateway
      '/api/v1/attendance': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/attendance/, '/v1/attendance'),
      }
    }
  }
})
