# Thiết kế chi tiết: Saga Orchestration — Luồng Thanh Toán Vé

## 1. Tổng quan kiến trúc

Hệ thống sử dụng mẫu **Saga Orchestration**  để quản lý giao dịch phân tán giữa 2 Bounded Context:

| Service | Vai trò trong Saga | Database |
|---|---|---|
| **Registration Service** | Orchestrator — Sở hữu vòng đời đăng ký vé | `registration_db` |
| **Payment Service** | Participant — Xử lý thu/hoàn tiền | `payment_db` |

## 2. State Machine — Vòng đời đăng ký vé

```mermaid
stateDiagram-v2
    [*] --> RESERVED : User đặt vé (reserveTicket)
    
    RESERVED --> CONFIRMED : Vé FREE (tự động confirm)
    RESERVED --> CONFIRMED : PaymentSucceeded (Saga chốt)
    RESERVED --> CANCELLED : PaymentFailed / Timeout (Saga rollback)
    RESERVED --> CANCELLED : User tự hủy
    
    CONFIRMED --> [*]
    CANCELLED --> [*]
```

**Phân nhánh tại `reserveTicket()`:**
- **Vé FREE:** `Registration.createNew(campaignId, userId, isFreeTicket=true)` → Trạng thái `CONFIRMED` ngay lập tức → KHÔNG kích hoạt Saga.
- **Vé PAID:** `Registration.createNew(campaignId, userId, isFreeTicket=false)` → Trạng thái `RESERVED` → Bắn `RegistrationRequestedEvent` lên Kafka → Kích hoạt Saga.

---

## 3. Sequence Diagram

### 3.1. Happy Path — Thanh toán thành công

```mermaid
sequenceDiagram
    autonumber
    actor U as User (Nhân viên)
    participant R as Registration Service
    participant DB_R as registration_db
    participant K as Kafka Broker
    participant P as Payment Service
    participant DB_P as payment_db
    participant V as VNPay Sandbox

    rect rgb(230, 245, 230)
    Note over U,R: BƯỚC 1 — Giữ chỗ (Đồng bộ)
    U->>R: POST /api/v1/registrations {campaignId}
    R->>DB_R: campaign.reserveTicket() → currentParticipants++
    R->>DB_R: INSERT Registration (status=RESERVED)
    R->>DB_R: INSERT outbox_events (RegistrationRequestedEvent)
    R-->>U: 201 Created {registrationId, paymentUrl}
    end

    rect rgb(230, 235, 250)
    Note over K,P: BƯỚC 2 — Tạo giao dịch thanh toán (Bất đồng bộ)
    K->>P: Consume: RegistrationRequestedEvent
    P->>DB_P: INSERT PaymentTransaction (status=PENDING, registrationId, amount)
    P->>P: Sinh paymentUrl từ VNPay SDK
    end

    rect rgb(255, 250, 230)
    Note over U,V: BƯỚC 3 — User thanh toán
    U->>V: Quét mã QR / Chuyển khoản
    V-->>P: Webhook IPN callback (vnp_ResponseCode=00)
    P->>DB_P: UPDATE PaymentTransaction SET status=SUCCESS
    P->>DB_P: INSERT outbox_events (PaymentSucceededEvent)
    end

    rect rgb(230, 245, 230)
    Note over K,R: BƯỚC 4 — Saga Orchestrator chốt vé
    K->>R: [SagaManager] Consume: PaymentSucceededEvent
    R->>R: SagaManager phát lệnh ConfirmPaidRegistrationCommand
    K->>R: [CommandHandler] Consume: ConfirmPaidRegistrationCommand
    R->>DB_R: UPDATE Registration SET status=CONFIRMED
    R->>DB_R: INSERT outbox_events (RegistrationConfirmedEvent)
    end

    Note over K: Notification Service lắng nghe RegistrationConfirmedEvent → Gửi Email xác nhận
```

### 3.2. Sad Path — Thanh toán thất bại / Hết hạn (Compensating Transaction)

```mermaid
sequenceDiagram
    autonumber
    participant S as Scheduler (Cron Job)
    participant P as Payment Service
    participant DB_P as payment_db
    participant K as Kafka Broker
    participant R as Registration Service
    participant DB_R as registration_db

    rect rgb(255, 235, 235)
    Note over S,P: BƯỚC 1 — Phát hiện giao dịch quá hạn
    S->>P: Quét bảng PaymentTransaction WHERE status=PENDING AND createdAt < now() - 15min
    P->>DB_P: UPDATE PaymentTransaction SET status=EXPIRED
    P->>DB_P: INSERT outbox_events (PaymentFailedEvent, reason=PAYMENT_TIMED_OUT)
    end

    rect rgb(255, 245, 230)
    Note over K,R: BƯỚC 2 — Saga Orchestrator ra lệnh hoàn trả
    K->>R: [SagaManager] Consume: PaymentFailedEvent
    R->>R: SagaManager phát lệnh RollbackPaidRegistrationCommand
    K->>R: [CommandHandler] Consume: RollbackPaidRegistrationCommand
    R->>DB_R: UPDATE Registration SET status=CANCELLED, cancelReason=PAYMENT_TIMED_OUT
    R->>DB_R: campaign.releaseTicket() → currentParticipants--
    R->>DB_R: INSERT outbox_events (PaidRegistrationRolledBackEvent)
    end

    Note over K: Slot vé đã được giải phóng, người khác có thể đặt lại
```

---

## 4. Kafka Topics & Message Flow

### 4.1. Bản đồ Topics

| Topic Name | Producer | Consumer | Mục đích |
|---|---|---|---|
| `registration-events` | Registration Service | Payment Service, Notification | Domain Events của Registration |
| `payment-events` | Payment Service | Registration Service (SagaManager) | Kết quả thanh toán |
| `registration-commands` | SagaManager (nội bộ) | Registration Service (CommandHandler) | Lệnh điều phối Saga |

### 4.2. Danh sách Messages

**Events (Sự kiện — Thông báo kết quả):**

| Message | Payload chính | Ai bắn? | Ai nghe? |
|---|---|---|---|
| `RegistrationRequestedEvent` | registrationId, campaignId, employeeId | Registration | Payment |
| `RegistrationConfirmedEvent` | registrationId, campaignId, paymentId | Registration | Notification |
| `PaidRegistrationRolledBackEvent` | registrationId, reason | Registration | Notification |
| `PaymentSucceededEvent` | paymentId, registrationId, amount | Payment | SagaManager |
| `PaymentFailedEvent` | paymentId, registrationId, reason | Payment | SagaManager |

**Commands (Lệnh — Yêu cầu hành động):**

| Message | Payload chính | Ai bắn? | Ai nghe? |
|---|---|---|---|
| `ConfirmPaidRegistrationCommand` | registrationId, paymentId | SagaManager | Registration CommandHandler |
| `RollbackPaidRegistrationCommand` | registrationId, reason | SagaManager | Registration CommandHandler |
