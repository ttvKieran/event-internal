export const profiles = {
  //Smoke test
  smoke: {
    vus: 1,
    duration: '10s',
  },

  // Load test
  load: {
    stages: [
      { duration: '30s', target: 50 }, // Ramp-up: Tăng dần từ 0 lên 50 users trong 30s
      { duration: '1m', target: 50 },  // Duy trì: Giữ nguyên 50 users trong 1 phút
      { duration: '30s', target: 0 },  // Ramp-down: Giảm dần về 0 users trong 30s
    ],
  },

  // Stress test
  stress: {
    stages: [
      { duration: '30s', target: 50 },
      { duration: '1m', target: 50 },
      { duration: '30s', target: 200 }, // Đẩy lên 200 users cùng lúc
      { duration: '1m', target: 200 },
      { duration: '30s', target: 0 },
    ],
  },

  // Spike test (Flash Sale): Giả lập lượng lớn user ùa vào đột ngột trong thời gian rất ngắn
  spike: {
    stages: [
      { duration: '10s', target: 100 },  // Khởi động nhanh
      { duration: '10s', target: 1000 }, // Đổ ập 1000 users
      { duration: '1m', target: 1000 },  // Duy trì đỉnh điểm trong 1 phút để xem server có sập không
      { duration: '20s', target: 0 },
    ],
  },
};
