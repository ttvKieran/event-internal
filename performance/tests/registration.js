import http from 'k6/http';
import { check, sleep } from 'k6';
import { profiles } from '../config/profiles.js';

const PROFILE = __ENV.PROFILE || 'smoke';
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8083/api/v1';
const EVENT_ID = __ENV.EVENT_ID || 'ed602488-07bc-4fca-bc34-20eb139b9182';

export const options = profiles[PROFILE];

// Setup data trước khi chạy tất cả VUs
export function setup() {
  console.log(`Bắt đầu chạy Test: Profile [${PROFILE}], API [${BASE_URL}]`);
}

// Hàm sinh ngẫu nhiên UUID chuẩn (cho X-Employee-Id)
function uuidv4() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

// Hàm main được thực thi cho từng Virtual User (VU)
export default function () {
  // Sinh UUID ngẫu nhiên để pass được validation của Spring Boot (đòi hỏi @RequestHeader là UUID)
  const randomEmployeeId = uuidv4();

  const url = `${BASE_URL}/registrations`;

  const payload = JSON.stringify({
    campaignId: `${EVENT_ID}`,
    provider: "vnpay"
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Employee-Id': randomEmployeeId,
    },
  };

  // Gửi request POST
  const res = http.post(url, payload, params);

  // 5. Kiểm tra kết quả trả về
  check(res, {
    // API reserveTicketAsync trả về 202 ACCEPTED, nên cần phải check 202
    'Status Thành công': (r) => r.status === 200 || r.status === 201 || r.status === 202,
    'Thời gian phản hồi < 1000ms': (r) => r.timings.duration < 1000,
  });

  sleep(1);
}
