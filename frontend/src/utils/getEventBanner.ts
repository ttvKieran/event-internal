// Helper lấy ảnh banner cho sự kiện
// Sẽ xoay vòng 5 tấm ảnh trong thư mục public/images/events/

export const getEventBanner = (eventId?: string): string => {
  if (!eventId) return '/images/events/event-1.png';
  
  // Tính hash đơn giản từ eventId để chọn cố định 1 ảnh từ 1 đến 5 cho mỗi event
  let hash = 0;
  for (let i = 0; i < eventId.length; i++) {
    hash = eventId.charCodeAt(i) + ((hash << 5) - hash);
  }
  
  const index = (Math.abs(hash) % 5) + 1;
  return `/images/events/event-${index}.png`;
};

// Ảnh mặc định chất lượng cao từ Unsplash (dùng làm fallback nếu chưa bỏ ảnh vào thư mục)
export const DEFAULT_FALLBACK_BANNERS = [
  'https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1511578314322-379afb476865?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1475721027785-f74eccf877e2?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1505373877841-8d25f7d46678?w=800&auto=format&fit=crop&q=80',
  'https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&auto=format&fit=crop&q=80',
];
