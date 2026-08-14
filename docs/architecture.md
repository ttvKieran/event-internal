# System Architecture

## 1. Pattern Selection

| Pattern | Selected? | Derived from (Analysis Step) | Business/Technical Justification |
|---------|-----------|-----------------------------|----------------------------------|
| API Gateway | ✅        | 2.6 Context Map | Điểm vào duy nhất, xác thực JWT (IAM CF) tập trung và định tuyến. |
| Database per Service | ✅        | 1.3 NFR, 2.5 Bounded Contexts | Tách bạch dữ liệu giữa Core và Supporting domain, đảm bảo độc lập triển khai. |
| Shared Database | ❌        | | Không sử dụng. Mỗi service quản lý data riêng. |
| Saga | ✅        | 2.6 Context Map | Quản lý giao dịch phân tán giữa Registration và Payment (Partnership). |
| Event-driven / Message Queue | ✅        | 2.6 Context Map, 1.3 NFR | Giải quyết bất đồng bộ, giảm tải hệ thống (Notification, Analytics, Event Planning). |
| CQRS | ✅        | 2.6 Context Map | Tách biệt Model đọc (Analytics Service) và Model ghi (Core Services) để tối ưu truy vấn Dashboard. |
| Circuit Breaker | ✅        | 2.6 Context Map | Chống lỗi dây chuyền khi gọi Payment Gateway (VNPay/Momo) bị timeout. |
| Service Registry / Discovery |           |  |  |

---

## 2. System Components

| Component     | Responsibility | Tech Stack      | Port  |
|---------------|----------------|-----------------|-------|
| **Frontend**  | Giao diện Employee/Organizer | React / Vite | 3000  |
| **Gateway**   | Routing, JWT Auth | Spring Cloud Gateway | 8000  |
| **IAM Service**| Quản lý user, cấp JWT | Spring Boot, PostgreSQL | 8085  |
| **Resource Svc**| Danh mục phòng/thiết bị | Spring Boot, PostgreSQL | 8081  |
| **Event Svc** | Cấu hình sự kiện | Spring Boot, PostgreSQL | 8082  |
| **Registration Svc**| Giữ chỗ, đăng ký (Core) | Spring Boot, PostgreSQL | 8083  |
| **Payment Svc**| Xử lý thanh toán, Saga | Spring Boot, PostgreSQL | 8084  |
| **Attendance Svc**| Sinh QR, kiểm tra check-in | Spring Boot, Redis, PG | 8086  |
| **Notification Svc**| Gửi Email/Push | Spring Boot, PostgreSQL | 8087  |
| **Analytics Svc**| Dashboard thống kê (CQRS) | Spring Boot, PostgreSQL | 8088  |

---

## 3. Communication

### Inter-service Communication Matrix

| From → To     | IAM | Resource | Event | Registration | Payment | Attendance | Notification | Analytics | DBs | Kafka |
|---------------|-----|----------|-------|--------------|---------|------------|--------------|-----------|-----|-------|
| **Frontend**  | —   | —        | —     | —            | —       | —          | —            | —         | —   | —     |
| **Gateway**   | REST| REST     | REST  | REST         | REST    | REST       | REST         | REST      | —   | —     |
| **Event Svc** | —   | REST     | —     | —            | —       | —          | —            | —         | TCP | async |
| **Registration**| — | REST     | —     | —            | —       | —          | —            | —         | TCP | async |
| **Payment Svc**| —  | —        | —     | —            | —       | —          | —            | —         | TCP | async |
| **Attendance**| —   | —        | —     | —            | —       | —          | —            | —         | TCP | async |

---

## 4. Architecture Diagram

### 4.1 System Context (C4 Level 1)

```mermaid
C4Context
    title System Context — Quản lý Sự kiện Nội bộ

    Person(emp, "Employee", "Xem sự kiện, đăng ký, thanh toán, quét QR")
    Person(org, "Organizer", "Tạo sự kiện, bật màn hình QR, xem Dashboard")

    System(system, "Hệ thống Quản lý Sự kiện Nội bộ", "Nền tảng tổ chức sự kiện end-to-end")

    System_Ext(extPayment, "Payment Gateway (VNPay)", "Thanh toán vé sự kiện")
    System_Ext(extEmail, "Email / Push Provider", "Gửi thông báo")

    Rel(emp, system, "Truy cập / Sử dụng", "HTTPS")
    Rel(org, system, "Quản trị / Cấu hình", "HTTPS")
    Rel(system, extPayment, "Yêu cầu thanh toán, Webhook", "HTTPS/REST")
    Rel(system, extEmail, "Gửi thông báo xác nhận", "API")
```

### 4.2 Container Diagram (C4 Level 2) — Full Deployment View

```mermaid
C4Container
    title Container Diagram — Hệ thống Quản lý Sự kiện Nội bộ

    Person(user, "Employee / Organizer")

    Container_Boundary(sys, "Hệ thống Sự kiện") {
        Container(fe, "Frontend App", "React/Vite", "Web App UI (3000)")
        Container(gw, "API Gateway", "Spring Cloud", "Verify JWT, Rate Limiting (8000)")
        
        Container(broker, "Kafka Broker", "Apache Kafka", "Trục sự kiện bất đồng bộ (Saga, CQRS) (9092)")

        Container(iam, "IAM Service", "Spring Boot", "Login, RBAC (8085)")
        Container(res, "Resource Service", "Spring Boot", "Phòng ốc, Sức chứa (8081)")
        Container(evt, "Event Service", "Spring Boot", "Kế hoạch sự kiện (8082)")
        Container(reg, "Registration Service", "Spring Boot", "Logic đăng ký (8083)")
        Container(pay, "Payment Service", "Spring Boot", "Thanh toán (ACL) (8084)")
        Container(att, "Attendance Service", "Spring Boot", "Điểm danh QR (8086)")
        Container(noti, "Notification Service", "Spring Boot", "Email/Push (8087)")
        Container(ana, "Analytics Service", "Spring Boot", "CQRS Dashboard (8088)")
        
        ContainerDb(db_reg, "Registration DB", "PostgreSQL", "Chứa đơn đăng ký")
        ContainerDb(db_pay, "Payment DB", "PostgreSQL", "Lịch sử giao dịch")
        ContainerDb(db_ana, "Analytics DB", "PostgreSQL", "Read Model")
    }

    Rel(user, fe, "Sử dụng", "HTTPS")
    Rel(fe, gw, "Gọi API", "HTTPS/REST")
    
    Rel(gw, iam, "Xác thực JWT", "REST")
    Rel(gw, evt, "Routes", "REST")
    Rel(gw, reg, "Routes", "REST")
    Rel(gw, pay, "Routes", "REST")
    Rel(gw, att, "Routes", "REST")
    Rel(gw, ana, "Routes", "REST")

    Rel(reg, res, "Claim/Hold Slot", "REST")
    Rel(evt, res, "Check Resource", "REST")

    Rel(evt, broker, "Publish EventCreated", "Kafka")
    Rel(reg, broker, "Publish TempSlotHeld", "Kafka Saga")
    Rel(broker, pay, "Consume TempSlotHeld", "Kafka Saga")
    Rel(pay, broker, "Publish PaymentResult", "Kafka Saga")
    Rel(broker, reg, "Consume PaymentResult", "Kafka Saga")
    
    Rel(reg, broker, "Update Metrics", "Kafka")
    Rel(att, broker, "Update Metrics", "Kafka")
    Rel(pay, broker, "Update Metrics", "Kafka")
    Rel(broker, ana, "Consume to Update Read Model", "Kafka")

    Rel(broker, noti, "Consume for Alerts", "Kafka")
```

---

## 5. Deployment

- Tất cả dịch vụ được đóng gói (Containerized) bằng Docker
- Quản lý điều phối qua Docker Compose (`docker-compose.yml`)
- DNS nội bộ: `http://registration-service:8083`, `http://kafka:9092`
- Lệnh chạy: `docker compose up --build`
