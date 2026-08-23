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
      // Event Service (Bypass Gateway vì Backend có lỗi lệch path /v1 và /api/v1)
      '/api/v1/events': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/v1/campaigns': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      // Registration Service (Bypass Gateway)
      '/api/v1/registrations': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      // Payment Service (Bypass Gateway)
      '/api/v1/payments': {
        target: 'http://localhost:8084',
        changeOrigin: true,
      },
      // Auth / IAM Service (Đi qua Gateway vì path đã khớp chuẩn)
      '/api/v1/auth': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/auth/, '/v1/auth'),
      },
      '/api/v1/Employees': {
        target: 'http://localhost:8000',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/v1\/Employees/, '/v1/Employees'),
      }
    }
  }
})
