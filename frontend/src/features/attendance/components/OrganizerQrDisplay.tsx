import React, { useState, useEffect, useRef } from 'react';
import { QRCodeSVG } from 'qrcode.react';
import { attendanceApi } from '../api/attendanceApi';
import type { QRSession } from '../api/attendanceApi';
import { RefreshCw, Play, Maximize, X } from 'lucide-react';
import { toast } from 'sonner';

interface Props {
  eventId: string;
}

export const OrganizerQrDisplay: React.FC<Props> = ({ eventId }) => {
  const [session, setSession] = useState<QRSession | null>(null);
  const [loading, setLoading] = useState(true);
  const [currentToken, setCurrentToken] = useState<string | null>(null);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const rotationTimerRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    checkActiveSession();
    return () => stopRotation();
  }, [eventId]);

  const checkActiveSession = async () => {
    setLoading(true);
    try {
      const activeSession = await attendanceApi.getSession(eventId);
      if (activeSession && activeSession.qrCodes && activeSession.qrCodes.length > 0) {
        setSession(activeSession);
        startRotation(activeSession.qrCodes);
      }
    } catch (err: any) {
      // 404 is expected if no active session
      if (err.code !== 'NOT_FOUND' && err.message !== 'NOT_FOUND') {
        console.error('Lỗi khi kiểm tra phiên check-in:', err);
      }
    } finally {
      setLoading(false);
    }
  };

  const createSession = async () => {
    try {
      setLoading(true);
      const newSession = await attendanceApi.createSession(eventId);
      setSession(newSession);
      toast.success('Đã tạo phiên Check-in thành công!');
      startRotation(newSession.qrCodes);
    } catch (err: any) {
      toast.error(err.message || 'Lỗi khi tạo phiên check-in');
      setLoading(false);
    }
  };

  const startRotation = (qrCodes: string[]) => {
    stopRotation();
    if (!qrCodes || qrCodes.length === 0) return;
    
    // Pick first token immediately
    pickRandomToken(qrCodes);
    
    // Rotate every 10 seconds
    rotationTimerRef.current = setInterval(() => {
      pickRandomToken(qrCodes);
    }, 10000);
    setLoading(false);
  };

  const stopRotation = () => {
    if (rotationTimerRef.current) {
      clearInterval(rotationTimerRef.current);
    }
  };

  const pickRandomToken = (qrCodes: string[]) => {
    const randomIndex = Math.floor(Math.random() * qrCodes.length);
    setCurrentToken(qrCodes[randomIndex]);
  };

  // Generate a URL instead of JSON so that native phone cameras can open it
  const appBaseUrl = window.location.origin;
  const qrPayload = currentToken ? `${appBaseUrl}/checkin-process?eventId=${eventId}&token=${currentToken}` : '';

  if (loading && !session) {
    return (
      <div className="flex justify-center py-12">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-600" />
      </div>
    );
  }

  if (!session) {
    return (
      <div className="text-center py-12 bg-gray-50 rounded-lg border-2 border-dashed border-gray-300">
        <p className="text-gray-500 mb-4">Chưa có phiên Check-in nào đang mở cho sự kiện này.</p>
        <button
          onClick={createSession}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700"
        >
          <Play className="w-4 h-4 mr-2" />
          Bắt đầu phiên Check-in (Tạo QR)
        </button>
      </div>
    );
  }

  // Fullscreen Render
  if (isFullscreen) {
    return (
      <div className="fixed inset-0 z-50 bg-white flex flex-col items-center justify-center">
        <button 
          onClick={() => setIsFullscreen(false)}
          className="absolute top-8 right-8 p-2 rounded-full bg-gray-100 hover:bg-gray-200"
        >
          <X className="w-8 h-8 text-gray-700" />
        </button>
        <h2 className="text-4xl font-bold mb-12 text-gray-900">Quét mã QR để Check-in</h2>
        {qrPayload && (
          <div className="p-8 bg-white rounded-3xl shadow-2xl border-4 border-indigo-100">
            <QRCodeSVG value={qrPayload} size={400} />
          </div>
        )}
        <p className="mt-8 text-xl text-gray-500 animate-pulse">Mã sẽ tự động làm mới liên tục...</p>
      </div>
    );
  }

  // Normal Render
  return (
    <div className="flex flex-col items-center justify-center py-8">
      <div className="mb-4 text-center">
        <h3 className="text-lg font-medium text-gray-900">Mã QR Check-in</h3>
        <p className="text-sm text-gray-500">Người tham gia mở ứng dụng để quét mã này</p>
      </div>
      
      {qrPayload ? (
        <div className="p-4 bg-white rounded-xl shadow-sm border border-gray-200 mb-6 relative">
          <QRCodeSVG value={qrPayload} size={256} />
        </div>
      ) : (
        <div className="w-64 h-64 bg-gray-100 rounded-xl mb-6 flex items-center justify-center">
          <span className="text-gray-400">Đang chuẩn bị mã...</span>
        </div>
      )}
      
      <div className="flex space-x-4">
        <button
          onClick={() => setIsFullscreen(true)}
          className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
        >
          <Maximize className="w-4 h-4 mr-2" />
          Phóng to toàn màn hình
        </button>
      </div>
      <p className="text-xs text-gray-400 mt-4">
        Phiên này sẽ hết hạn vào: {new Date(session.expiresAt).toLocaleString('vi-VN')}
      </p>
    </div>
  );
};
