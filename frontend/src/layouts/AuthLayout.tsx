import { Outlet } from 'react-router-dom';

export function AuthLayout() {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold tracking-tight text-slate-900">VTIT Event Portal</h1>
          <p className="text-slate-500 mt-2">Hệ thống quản lý sự kiện nội bộ</p>
        </div>
        <Outlet />
      </div>
    </div>
  );
}
