# Job Application Tracker — Project Plan

> Mục tiêu: Build một backend-heavy full-stack app để xin internship,
> giải quyết vấn đề thực tế (track job applications), tích hợp AI.

---

## 1. Tổng quan dự án

**Tên project:** Job Application Tracker  
**Mô tả:** Công cụ giúp sinh viên/developer quản lý quá trình xin việc —
lưu trữ job applications, track trạng thái, nhắc deadline phỏng vấn,
và dùng AI phân tích độ phù hợp giữa CV và Job Description.

**Điểm mạnh khi phỏng vấn:**
- Giải quyết vấn đề bạn tự gặp phải (credible)
- Thể hiện đủ kỹ năng backend: auth, database design, REST API, AI integration
- Có thể mở rộng vô hạn sau này

---

## 2. Tech Stack

### Backend (bắt buộc)
| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| Java | 17+ | Ngôn ngữ chính |
| Spring Boot | 3.x | Framework backend |
| Spring Security | 6.x | JWT Authentication |
| Spring Data JPA | 3.x | ORM / database access |
| Spring Mail | 3.x | Gửi email nhắc deadline |
| Spring Scheduler | built-in | Cron job nhắc deadline |
| PostgreSQL | 15+ | Database production |
| H2 | - | Database khi dev/test |
| Maven | 3.9+ | Build tool |

### AI Integration
| Công nghệ | Mục đích |
|---|---|
| Anthropic Claude API | Phân tích JD, gợi ý câu hỏi phỏng vấn |
| OkHttp / RestTemplate | Gọi API từ Spring Boot |

### Frontend (nhẹ, không phải trọng tâm)
| Công nghệ | Mục đích |
|---|---|
| Vue.js 3 | Framework frontend |
| Vue Router | Điều hướng trang |
| Axios | Gọi REST API |
| Bootstrap 5 | UI styling |

### DevOps / Deploy
| Công nghệ | Mục đích |
|---|---|
| Docker | Containerize app |
| Railway hoặc Render | Deploy miễn phí |
| GitHub Actions | CI/CD tự động |
| GitHub | Version control, portfolio |

---

## 3. Database Schema

### Bảng USER
```
id          BIGINT PRIMARY KEY AUTO_INCREMENT
email       VARCHAR UNIQUE NOT NULL
password    VARCHAR NOT NULL (bcrypt hashed)
name        VARCHAR
createdAt   TIMESTAMP
```

### Bảng APPLICATION
```
id            BIGINT PRIMARY KEY
userId        BIGINT FK → USER.id
companyName   VARCHAR NOT NULL
position      VARCHAR NOT NULL
status        ENUM (WISHLIST, APPLIED, INTERVIEWING, OFFER, REJECTED, ACCEPTED, GHOSTED)
appliedDate   DATE
deadline      DATE (deadline nộp hoặc phỏng vấn)
jobUrl        VARCHAR
notes         TEXT
salaryRange   VARCHAR
createdAt     TIMESTAMP
updatedAt     TIMESTAMP
```

### Bảng INTERVIEW
```
id              BIGINT PRIMARY KEY
applicationId   BIGINT FK → APPLICATION.id
round           INT (vòng 1, 2, 3...)
scheduledAt     TIMESTAMP
type            ENUM (PHONE, VIDEO, ONSITE, TECHNICAL, HR)
notes           TEXT
outcome         ENUM (PASSED, FAILED, PENDING, CANCELLED)
```

### Bảng AI_ANALYSIS
```
id              BIGINT PRIMARY KEY
applicationId   BIGINT FK → APPLICATION.id (unique)
matchScore      INT (0-100)
summary         TEXT (tóm tắt JD)
suggestion      TEXT (gợi ý cải thiện CV)
interviewQs     TEXT (câu hỏi phỏng vấn gợi ý)
analyzedAt      TIMESTAMP
```

### Bảng NOTIFICATION
```
id            BIGINT PRIMARY KEY
userId        BIGINT FK → USER.id
type          ENUM (DEADLINE_REMINDER, INTERVIEW_REMINDER, STATUS_UPDATE)
message       TEXT
isRead        BOOLEAN DEFAULT FALSE
sentAt        TIMESTAMP
scheduledFor  TIMESTAMP
```

---

## 4. Danh sách API (REST)

### Auth
```
POST   /api/auth/register     → Đăng ký tài khoản
POST   /api/auth/login        → Đăng nhập, trả về JWT token
GET    /api/auth/me           → Lấy thông tin user hiện tại
```

### Applications
```
GET    /api/applications                → Danh sách (filter: status, company, date)
POST   /api/applications                → Thêm application mới
GET    /api/applications/{id}           → Chi tiết 1 application
PUT    /api/applications/{id}           → Cập nhật
DELETE /api/applications/{id}           → Xóa
GET    /api/applications/stats          → Thống kê (tổng, tỉ lệ theo status)
```

### Interviews
```
GET    /api/applications/{id}/interviews    → Danh sách phỏng vấn
POST   /api/applications/{id}/interviews    → Thêm lịch phỏng vấn
PUT    /api/interviews/{id}                 → Cập nhật kết quả
DELETE /api/interviews/{id}
```

### AI Analysis
```
POST   /api/applications/{id}/analyze   → Gửi JD text, nhận phân tích AI
GET    /api/applications/{id}/analyze   → Lấy kết quả phân tích đã lưu
```

### Notifications
```
GET    /api/notifications               → Danh sách thông báo
PUT    /api/notifications/{id}/read     → Đánh dấu đã đọc
PUT    /api/notifications/read-all      → Đánh dấu tất cả đã đọc
```

---

## 5. Lộ trình build (8 tuần)

### Tuần 1-2: Foundation
- [ ] Setup Spring Boot project (Maven multi-module hoặc single module)
- [ ] Cấu hình PostgreSQL + H2
- [ ] Tạo entities: User, Application, Interview, AI_Analysis, Notification //Đang ở bước này và Đã tạo entity Users
- [ ] JWT Authentication (register, login, filter)
- [ ] CRUD API cho Application
- [ ] Viết unit test cơ bản

### Tuần 3: Core Features
- [ ] Interview tracking API
- [ ] Filter & search applications (theo status, công ty, ngày)
- [ ] Stats API (dashboard data)
- [ ] Validation & error handling chuẩn

### Tuần 4: Notifications & Scheduling
- [ ] Setup Spring Mail (dùng Gmail SMTP hoặc Resend)
- [ ] Spring Scheduler: mỗi ngày scan deadline gần → gửi email
- [ ] Notification entity + API
- [ ] Test luồng email end-to-end

### Tuần 5: AI Integration
- [ ] Tích hợp Claude API (Anthropic)
- [ ] Endpoint nhận Job Description text
- [ ] Prompt engineering: phân tích JD, chấm matchScore, gợi ý câu hỏi
- [ ] Lưu kết quả vào AI_ANALYSIS
- [ ] Rate limiting để không spam API

### Tuần 6: Frontend (Vue.js)
- [ ] Setup Vue.js 3 project
- [ ] Trang Login / Register
- [ ] Dashboard: danh sách applications + stats
- [ ] Form thêm/sửa application
- [ ] Trang chi tiết: interviews + AI analysis

### Tuần 7: Deploy & Polish
- [ ] Viết Dockerfile
- [ ] Deploy lên Railway hoặc Render
- [ ] Setup GitHub Actions CI/CD
- [ ] Cấu hình environment variables (không hardcode secrets)
- [ ] Test toàn bộ luồng trên production

### Tuần 8: Portfolio Ready
- [ ] Viết README.md chi tiết (có screenshot, có hướng dẫn chạy local)
- [ ] Vẽ architecture diagram
- [ ] Record demo video ngắn (2-3 phút)
- [ ] Thêm Postman collection để test API
- [ ] Seed data mẫu để demo dễ

---

## 6. Cấu trúc thư mục đề xuất

```
job-tracker/
├── backend/
│   └── src/main/java/com/jobtracker/
│       ├── auth/           → JWT, SecurityConfig
│       ├── user/           → User entity, service, controller
│       ├── application/    → Application entity, service, controller
│       ├── interview/      → Interview entity, service, controller
│       ├── notification/   → Notification + email service
│       ├── ai/             → Claude API integration
│       └── common/         → Exception handler, response wrapper
├── frontend/
│   └── src/
│       ├── views/          → Pages
│       ├── components/     → Reusable components
│       ├── stores/         → Vuex / Pinia state
│       └── services/       → API calls (axios)
├── docker-compose.yml
├── Dockerfile
└── README.md
```

---

## 7. Những thứ cần chuẩn bị trước khi code

### Tài khoản cần tạo
- [ ] GitHub account (lưu code, portfolio)
- [ ] Anthropic Console (lấy API key Claude)
- [ ] Railway hoặc Render account (deploy miễn phí)
- [ ] Gmail account riêng để gửi email notification

### Cài đặt local
- [ ] JDK 17+
- [ ] Maven 3.9+
- [ ] PostgreSQL (hoặc dùng Docker)
- [ ] Node.js 18+ (cho frontend Vue.js)
- [ ] IntelliJ IDEA (khuyến nghị) hoặc VS Code
- [ ] Postman (test API)
- [ ] Docker Desktop

---

## 8. Điểm có thể mở rộng sau này (Phase 2+)

- Chrome Extension: tự detect job listing trên LinkedIn/Indeed → add vào tracker
- Resume parser: upload CV PDF → AI phân tích và so sánh với JD
- Analytics: ngành nào đang tuyển nhiều, tech stack nào hot
- Kanban view: kéo thả application giữa các status
- Export: xuất báo cáo PDF/Excel
- Team mode: nhiều người cùng track trong 1 nhóm

---

*Plan tạo ngày 28/06/2026 — Job Application Tracker v1.0*
