import React, { useEffect, useState, useCallback } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { analyticsApi } from './api/analyticsApi';
import type { DashboardMetrics, RegistrationMetrics, AttendanceMetrics } from '@/types/analytics';
import { ArrowLeft, Users, DollarSign, Activity, CheckCircle, RefreshCw } from 'lucide-react';

type Tab = 'overview' | 'registration' | 'attendance';

export const AnalyticsDashboardPage: React.FC = () => {
  const { eventId } = useParams<{ eventId: string }>();
  const navigate = useNavigate();
  const [dashboardData, setDashboardData] = useState<DashboardMetrics | null>(null);
  const [registrationData, setRegistrationData] = useState<RegistrationMetrics | null>(null);
  const [attendanceData, setAttendanceData] = useState<AttendanceMetrics | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<Tab>('overview');

  const fetchAnalytics = useCallback(async () => {
    if (!eventId) return;
    try {
      setLoading(true);
      setError(null);
      // Call APIs concurrently for better performance
      const [dashRes, regRes, attRes] = await Promise.all([
        analyticsApi.getDashboard(eventId),
        analyticsApi.getRegistrationMetrics(eventId),
        analyticsApi.getAttendanceMetrics(eventId),
      ]);
      
      setDashboardData(dashRes);
      setRegistrationData(regRes);
      setAttendanceData(attRes);
    } catch (err: any) {
      setError(err.message || 'Có lỗi xảy ra khi lấy dữ liệu thống kê');
    } finally {
      setLoading(false);
    }
  }, [eventId]);

  useEffect(() => {
    fetchAnalytics();
  }, [fetchAnalytics]);

  if (loading && !dashboardData) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-indigo-600"></div>
      </div>
    );
  }

  if (error && !dashboardData) {
    return (
      <div className="bg-red-50 p-4 rounded-md text-red-700 m-8">
        <p className="font-bold">Lỗi</p>
        <p>{error}</p>
        <button onClick={() => navigate(-1)} className="text-indigo-600 hover:underline mt-4 inline-block">Quay lại</button>
        <button onClick={fetchAnalytics} className="ml-4 text-indigo-600 hover:underline inline-block">Thử lại</button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center">
          <button onClick={() => navigate(-1)} className="text-gray-500 hover:text-gray-900 mr-4">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard Sự kiện</h1>
        </div>
        <button 
          onClick={fetchAnalytics}
          disabled={loading}
          className="flex items-center px-4 py-2 bg-indigo-50 text-indigo-600 rounded-md hover:bg-indigo-100 disabled:opacity-50"
        >
          <RefreshCw className={`w-4 h-4 mr-2 ${loading ? 'animate-spin' : ''}`} />
          Cập nhật thông tin
        </button>
      </div>

      {/* Tabs */}
      <div className="border-b border-gray-200 mb-6">
        <nav className="-mb-px flex space-x-8">
          <button
            onClick={() => setActiveTab('overview')}
            className={`${
              activeTab === 'overview'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            Tổng quan
          </button>
          <button
            onClick={() => setActiveTab('registration')}
            className={`${
              activeTab === 'registration'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            Thống kê Đăng ký
          </button>
          <button
            onClick={() => setActiveTab('attendance')}
            className={`${
              activeTab === 'attendance'
                ? 'border-indigo-500 text-indigo-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm`}
          >
            Thống kê Check-in
          </button>
        </nav>
      </div>

      {error && (
        <div className="bg-red-50 p-4 rounded-md text-red-700 mb-6">
          <p>{error}</p>
        </div>
      )}

      {/* Tab Content: Overview */}
      {activeTab === 'overview' && (
        <div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center">
              <div className="p-3 bg-blue-100 text-blue-600 rounded-full mr-4">
                <DollarSign className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500 font-medium">Doanh thu</p>
                <p className="text-2xl font-bold text-gray-900">
                  {dashboardData?.revenue ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(dashboardData.revenue) : '0 ₫'}
                </p>
              </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center">
              <div className="p-3 bg-green-100 text-green-600 rounded-full mr-4">
                <Users className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500 font-medium">Tổng vé bán ra</p>
                <p className="text-2xl font-bold text-gray-900">{registrationData?.totalRegistrations || 0}</p>
              </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center">
              <div className="p-3 bg-indigo-100 text-indigo-600 rounded-full mr-4">
                <CheckCircle className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500 font-medium">Đã Check-in</p>
                <p className="text-2xl font-bold text-gray-900">{attendanceData?.checkedInCount || 0}</p>
              </div>
            </div>

            <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center">
              <div className="p-3 bg-purple-100 text-purple-600 rounded-full mr-4">
                <Activity className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500 font-medium">Tỉ lệ Check-in</p>
                <p className="text-2xl font-bold text-gray-900">
                  {attendanceData?.checkInRate ? (attendanceData.checkInRate * 100).toFixed(1) : 0}%
                </p>
              </div>
            </div>
          </div>
          <p className="text-xs text-gray-400 text-right">
             Cập nhật lần cuối: {dashboardData?.updatedAt ? new Date(dashboardData.updatedAt).toLocaleString('vi-VN') : 'N/A'}
          </p>
        </div>
      )}

      {/* Tab Content: Registration */}
      {activeTab === 'registration' && (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
            <h2 className="text-lg font-semibold text-gray-800 mb-4">Chi tiết Đăng ký (Vé)</h2>
            <div className="space-y-4">
              <div className="flex justify-between items-center border-b pb-2">
                <span className="text-gray-600">Vé Miễn phí</span>
                <span className="font-medium">{registrationData?.freeCount || 0}</span>
              </div>
              <div className="flex justify-between items-center border-b pb-2">
                <span className="text-gray-600">Vé Trả phí</span>
                <span className="font-medium">{registrationData?.paidCount || 0}</span>
              </div>
              <div className="flex justify-between items-center border-b pb-2">
                <span className="text-gray-600">Số lượt Hủy</span>
                <span className="font-medium text-red-500">{dashboardData?.cancelledCount || 0}</span>
              </div>
              <div className="flex justify-between items-center border-b pb-2">
                <span className="text-gray-600">Tỉ lệ lỗi thanh toán</span>
                <span className="font-medium text-orange-500">
                  {dashboardData?.failedPaymentRate ? (dashboardData.failedPaymentRate * 100).toFixed(1) : 0}%
                </span>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6">
             <h2 className="text-lg font-semibold text-gray-800 mb-4">Trạng thái lấp đầy (Fill Rate)</h2>
             <div className="mt-8">
               <div className="flex justify-between mb-2">
                 <span className="text-sm font-medium text-gray-700">Vé đã bán / Tổng vé</span>
                 <span className="text-sm font-medium text-indigo-600">
                   {registrationData?.fillRate ? (registrationData.fillRate * 100).toFixed(1) : 0}%
                 </span>
               </div>
               <div className="w-full bg-gray-200 rounded-full h-4">
                 <div 
                   className="bg-indigo-600 h-4 rounded-full transition-all duration-500" 
                   style={{ width: `${(registrationData?.fillRate || 0) * 100}%` }}
                 ></div>
               </div>
             </div>
          </div>
        </div>
      )}

      {/* Tab Content: Attendance */}
      {activeTab === 'attendance' && (
        <div className="bg-white rounded-lg shadow-sm border border-gray-100 p-6 max-w-lg">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Thống kê Tham dự (Check-in)</h2>
          <div className="space-y-4">
            <div className="flex justify-between items-center border-b pb-2">
              <span className="text-gray-600">Đã Check-in</span>
              <span className="font-medium text-green-600">{attendanceData?.checkedInCount || 0}</span>
            </div>
            <div className="flex justify-between items-center border-b pb-2">
              <span className="text-gray-600">Chưa Check-in</span>
              <span className="font-medium text-gray-600">
                {(registrationData?.totalRegistrations || 0) - (attendanceData?.checkedInCount || 0)}
              </span>
            </div>
            <div className="flex justify-between items-center border-b pb-2">
              <span className="text-gray-600">Tỉ lệ tham dự</span>
              <span className="font-bold text-indigo-600">
                {attendanceData?.checkInRate ? (attendanceData.checkInRate * 100).toFixed(1) : 0}%
              </span>
            </div>
          </div>
          <div className="mt-8">
             <div className="w-full bg-gray-200 rounded-full h-4">
               <div 
                 className="bg-green-500 h-4 rounded-full transition-all duration-500" 
                 style={{ width: `${(attendanceData?.checkInRate || 0) * 100}%` }}
               ></div>
             </div>
          </div>
        </div>
      )}
    </div>
  );
};

