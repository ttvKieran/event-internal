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
      // Event Service
      '/api/v1/events': {
        target: 'http://localhost:8082',
        changeOrigin: true,
      },
      '/api/v1/campaigns': {
        target: 'http://localhost:8082', // Some campaigns endpoints might be on event-service or registration
        changeOrigin: true,
      },
      // Registration Service
      '/api/v1/registrations': {
        target: 'http://localhost:8083',
        changeOrigin: true,
      },
      // Payment Service
      '/api/v1/payments': {
        target: 'http://localhost:8084',
        changeOrigin: true,
      }
    }
  }
})
