import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom'
import { useAuthStore } from './store/useAuthStore'

import { AuthLayout } from './layouts/AuthLayout'
import { MainLayout } from './layouts/MainLayout'
import { LoginPage } from './features/auth/LoginPage'
import { EmployeeEventList } from './features/events/EmployeeEventList'
import { OrganizerEventList } from './features/events/OrganizerEventList'
import { ProfilePage } from './features/profile/ProfilePage'
import { EventDetailPage } from './features/events/EventDetailPage'
import { PaymentResultPage } from './features/payment/PaymentResultPage'
import { AnalyticsDashboardPage } from './features/analytics/AnalyticsDashboardPage'
import { CheckinProcessPage } from './features/attendance/components/CheckinProcessPage'
import { Toaster } from '@/components/ui/sonner'

// Protected Route Component
function ProtectedRoute({ children, allowedRoles }: { children: React.ReactNode, allowedRoles?: string[] }) {
  const { isAuthenticated, employee } = useAuthStore()
  const location = useLocation()
  
  if (!isAuthenticated || !employee) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }
  
  const roleName = typeof employee.role === 'object' ? employee.role.name : employee.role;

  if (allowedRoles && !allowedRoles.includes(roleName as string)) {
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
          
          {/* Common Routes */}
          <Route path="/profile" element={<ProfilePage />} />
          
          {/* Employee Routes */}
          <Route path="/events" element={<EmployeeEventList />} />
          <Route path="/events/:id" element={<EventDetailPage />} />
          <Route path="/payment/result" element={<PaymentResultPage />} />
          <Route path="/checkin-process" element={<CheckinProcessPage />} />
          
          {/* Organizer Routes */}
          <Route 
            path="/admin/events" 
            element={
              <ProtectedRoute allowedRoles={['ORGANIZER']}>
                <OrganizerEventList />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/admin/events/:eventId/dashboard" 
            element={
              <ProtectedRoute allowedRoles={['ORGANIZER']}>
                <AnalyticsDashboardPage />
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
