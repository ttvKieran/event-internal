import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/useAuthStore';
import { Button } from '@/components/ui/button';
import { LogOut, Calendar, ShieldCheck, Sparkles, UserCheck } from 'lucide-react';

export function MainLayout() {
  const { employee, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isOrganizer = employee?.role === 'ORGANIZER';

  return (
    <div className="min-h-screen bg-slate-50/70 flex flex-col font-sans antialiased text-slate-900 selection:bg-red-500 selection:text-white">
      {/* Viettel Top Accent Bar */}
      <div className="h-1.5 bg-gradient-to-r from-red-600 via-rose-600 to-red-700 w-full" />

      {/* Navbar Header */}
      <header className="bg-white/90 backdrop-blur-md border-b border-slate-200/80 sticky top-0 z-40 shadow-xs">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            
            {/* Logo & Main Nav */}
            <div className="flex items-center gap-8">
              <div 
                className="flex flex-col justify-center cursor-pointer select-none py-1 group" 
                onClick={() => navigate('/events')}
              >
                {/* Viettel Software SVG Wordmark Logo */}
                <div className="flex flex-col leading-none">
                  <span className="text-2xl font-black text-red-600 tracking-tight font-sans lowercase group-hover:opacity-90 transition-opacity">
                    viettel
                  </span>
                  <span className="text-sm font-extrabold text-slate-900 tracking-wider font-sans lowercase -mt-1 pl-0.5">
                    software
                  </span>
                </div>
              </div>

              {/* Navigation Links */}
              <nav className="hidden md:flex items-center gap-1 ml-4 border-l border-slate-200 pl-6 h-8">
                <Button 
                  variant={location.pathname === '/events' ? 'secondary' : 'ghost'} 
                  size="sm"
                  className={`gap-2 font-semibold text-xs rounded-md transition-all h-8 ${
                    location.pathname === '/events' 
                      ? 'bg-red-50 text-red-600 hover:bg-red-100/80 font-bold' 
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                  }`}
                  onClick={() => navigate('/events')}
                >
                  <Calendar className="h-3.5 w-3.5 text-red-600" />
                  Danh sách sự kiện
                </Button>

                {isOrganizer && (
                  <Button 
                    variant={location.pathname.startsWith('/admin') ? 'secondary' : 'ghost'} 
                    size="sm"
                    className={`gap-2 font-semibold text-xs rounded-md transition-all h-8 ${
                      location.pathname.startsWith('/admin') 
                        ? 'bg-red-50 text-red-600 hover:bg-red-100/80 font-bold' 
                        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                    }`}
                    onClick={() => navigate('/admin/events')}
                  >
                    <ShieldCheck className="h-3.5 w-3.5 text-red-600" />
                    Quản lý sự kiện (BTC)
                  </Button>
                )}
              </nav>
            </div>

            {/* Profile & Logout */}
            <div className="flex items-center gap-3">
              <div className="hidden sm:flex items-center gap-2.5 bg-slate-100/90 border border-slate-200 px-3 py-1 rounded-md">
                <div className="w-6 h-6 rounded bg-red-600 text-white font-extrabold flex items-center justify-center text-xs">
                  {employee?.fullName?.charAt(0) || 'U'}
                </div>
                <div className="flex flex-col text-left">
                  <span className="text-xs font-bold text-slate-900 leading-tight">
                    {employee?.fullName}
                  </span>
                  <span className="text-[10px] font-medium text-slate-500">
                    {employee?.department || 'VTIT'}
                  </span>
                </div>
                <span className={`ml-1 text-[10px] font-bold px-1.5 py-0.5 rounded text-white ${
                  isOrganizer 
                    ? 'bg-red-600' 
                    : 'bg-slate-800'
                }`}>
                  {employee?.role}
                </span>
              </div>

              <Button 
                variant="outline" 
                size="sm" 
                className="gap-1.5 border-slate-200 text-slate-700 hover:bg-red-50 hover:text-red-600 hover:border-red-200 transition-colors h-8 rounded-md text-xs font-semibold"
                onClick={handleLogout}
              >
                <LogOut className="h-3.5 w-3.5" />
                <span className="hidden sm:inline">Đăng xuất</span>
              </Button>
            </div>

          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 w-full max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-200 bg-white py-6 text-center text-xs text-slate-500">
        <div className="max-w-7xl mx-auto px-4 flex flex-col sm:flex-row justify-between items-center gap-2">
          <div className="flex items-center gap-2 font-medium">
            <span className="font-extrabold text-red-600">viettel software</span>
            <span>© 2026 Internal Event Management System</span>
          </div>
          <span className="text-slate-400">High-Concurrency Event Platform (Saga & Microservices)</span>
        </div>
      </footer>
    </div>
  );
}
