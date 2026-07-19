# Job Application Tracker

A full-stack web app to track job applications, manage interviews, and get AI-powered resume feedback.

**Live Demo:** https://job-tracker-rho-lilac.vercel.app

---

## Tech Stack

**Backend:** Java 17, Spring Boot 3.4.1, Spring Security (JWT), Spring Data JPA, PostgreSQL, Spring Mail

**Frontend:** Vue.js 3, Vue Router, Pinia, Axios, Tailwind CSS

**AI:** Claude API (Anthropic)

**Infrastructure:** Docker, Railway (backend + DB), Vercel (frontend)

---

## Features

- Register / Login with JWT authentication
- Track job applications with status (Wishlist → Applied → Interviewing → Offer → Accepted / Rejected / Ghosted)
- Log interview rounds — type, schedule, outcome, notes
- AI analysis: paste a job description, get a match score, improvement suggestions, and likely interview questions based on your resume
- Daily automated email + in-app notifications for applications with a deadline the next day
- Profile page to store your resume text

---

## Getting Started

### Prerequisites

- Java 17+
- Node.js 18+
- Docker (optional, for local PostgreSQL)

### Run backend locally

Create `backend/.env`:

```env
SPRING_DATASOURCE_URL=jdbc:h2:mem:jobtracker
SPRING_DATASOURCE_DRIVER=org.h2.Driver
SPRING_DATASOURCE_USERNAME=sa
SPRING_DATASOURCE_PASSWORD=
JPA_DDL_AUTO=create-drop
JWT_SECRET=<base64-secret>
ANTHROPIC_API_KEY=<your-key>
SPRING_MAIL_USERNAME=<gmail>
SPRING_MAIL_PASSWORD=<app-password>
```

```bash
cd backend
./mvnw spring-boot:run
```

### Run frontend locally

```bash
cd frontend
npm install
npm run dev
```

### Run with Docker Compose (backend + PostgreSQL)

Create `.env` at project root with `JWT_SECRET`, `ANTHROPIC_API_KEY`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`, then:

```bash
docker compose up --build
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register |
| POST | `/api/auth/login` | Login |
| GET | `/api/applications` | Get all applications |
| POST | `/api/applications` | Create application |
| GET | `/api/applications/{id}` | Get application detail |
| PUT | `/api/applications/{id}` | Update application |
| DELETE | `/api/applications/{id}` | Delete application |
| GET | `/api/applications/stats` | Get application stats |
| GET | `/api/applications/{id}/interviews` | Get interviews |
| POST | `/api/applications/{id}/interviews` | Add interview |
| PUT | `/api/applications/{id}/interviews/{iId}` | Update interview |
| DELETE | `/api/applications/{id}/interviews/{iId}` | Delete interview |
| GET | `/api/applications/{id}/analyze` | Get saved AI analysis |
| POST | `/api/applications/{id}/analyze` | Run AI analysis |
| GET | `/api/notifications` | Get notifications |
| PUT | `/api/notifications/{id}/read` | Mark notification as read |
| PUT | `/api/notifications/read-all` | Mark all as read |
| GET | `/api/users/me` | Get profile |
| PUT | `/api/users/me` | Update profile |

---

## Testing

Backend unit tests with JUnit 5 and Mockito, covering service-layer logic (application CRUD, ownership checks, stats).

```bash
cd backend
./mvnw test
```

---

## Deployment

**Backend → Railway**
- Connect GitHub repo, set root directory to `backend`, build with Dockerfile
- Add PostgreSQL service
- Set environment variables in Railway Variables tab

**Frontend → Vercel**
- Connect GitHub repo, set root directory to `frontend`
- Add `VITE_API_BASE_URL=https://<railway-url>/api` in Environment Variables

---

## Author

**Triet Nguyen** — [LinkedIn](https://www.linkedin.com/in/trietnguyen15/) · [GitHub](https://github.com/Minhtriet1501) · nguyenhoangminhtriet1501@gmail.com

---

## License

[MIT](LICENSE)

