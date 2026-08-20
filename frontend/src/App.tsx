import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useAuthStore } from './store/useAuthStore'

import { AuthLayout } from './layouts/AuthLayout'
import { MainLayout } from './layouts/MainLayout'
import { LoginPage } from './features/auth/LoginPage'
import { EmployeeEventList } from './features/events/EmployeeEventList'
import { OrganizerEventList } from './features/events/OrganizerEventList'
import { EventDetailPage } from './features/events/EventDetailPage'
import { PaymentResultPage } from './features/payment/PaymentResultPage'
import { Toaster } from '@/components/ui/sonner'

// Protected Route Component
function ProtectedRoute({ children, allowedRoles }: { children: React.ReactNode, allowedRoles?: string[] }) {
  const { isAuthenticated, employee } = useAuthStore()
  
  if (!isAuthenticated || !employee) {
    return <Navigate to="/login" replace />
  }
  
  if (allowedRoles && !allowedRoles.includes(employee.role)) {
    return <Navigate to="/events" replace />
  }
  
  return <>{children}</>
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Auth Routes */}
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
        </Route>

        {/* Main App Routes (Protected) */}
        <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
          
          {/* Default redirect based on role */}
          <Route path="/" element={<Navigate to="/events" replace />} />
          
          {/* Employee Routes */}
          <Route path="/events" element={<EmployeeEventList />} />
          <Route path="/events/:id" element={<EventDetailPage />} />
          <Route path="/payment/result" element={<PaymentResultPage />} />
          
          {/* Organizer Routes */}
          <Route 
            path="/admin/events" 
            element={
              <ProtectedRoute allowedRoles={['ORGANIZER']}>
                <OrganizerEventList />
              </ProtectedRoute>
            } 
          />
          
        </Route>
      </Routes>
      <Toaster position="top-center" />
    </BrowserRouter>
  )
}

export default App
