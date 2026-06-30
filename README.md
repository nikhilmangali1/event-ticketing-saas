# TicketFlow Backend

Spring Boot backend for **TicketFlow**, a multi-role event ticketing SaaS platform featuring JWT authentication, QR-code ticketing, role-based access control, AWS S3 integration, and Dockerized local development.

---

## Features

- JWT Authentication (Access & Refresh Tokens)
- Role-Based Access Control (USER, ORGANIZER, ADMIN)
- Event Creation & Management
- Ticket Booking & Cancellation
- QR Code Generation for Tickets
- Ticket Verification & Check-In
- Organizer Approval Workflow
- Email Notifications (Booking, Cancellation, Check-In)
- AWS S3 Image Storage
- Flyway Database Migrations
- Docker & Docker Compose Support

---

## Architecture

```text
React Frontend
        │
        ▼
Spring Boot REST API
        │
        ├──────────────► PostgreSQL
        │
        ├──────────────► AWS S3 (Event Images)
        │
        └──────────────► Gmail SMTP (Emails)
```

---

## Tech Stack

| Layer              | Technology                  |
| ------------------ | --------------------------- |
| Language           | Java 21                     |
| Framework          | Spring Boot 3.5.3           |
| Security           | Spring Security 6.x, JWT    |
| Database           | PostgreSQL                  |
| ORM                | Spring Data JPA (Hibernate) |
| Database Migration | Flyway                      |
| QR Code            | ZXing                       |
| Email              | Gmail SMTP (Spring Mail)    |
| Cloud              | AWS S3                      |
| Build Tool         | Maven                       |
| Containerization   | Docker, Docker Compose      |

---

## Prerequisites

### Docker (Recommended)

- Docker Desktop
- Docker Compose

### Without Docker

- Java 21
- PostgreSQL
- Maven (or Maven Wrapper)

---

## Quick Start (Docker)

### 1. Clone the repository

```bash
git clone https://github.com/nikhilmangali1/event-ticketing-saas
cd event-ticketing-saas
```

### 2. Create a `.env` file

Copy `.env.example` to `.env` and update it with your local credentials.

### 3. Build and start the application

```bash
docker compose up --build
```

### 4. View application logs

```bash
docker compose logs -f backend
```

The backend will be available at:

```
http://localhost:8081/api/v1
```

Flyway automatically applies database migrations during application startup.

---

## Services

| Service             | Host Port | Container Port |
| ------------------- | --------: | -------------: |
| Spring Boot Backend |      8081 |           8080 |
| PostgreSQL          |      5433 |           5432 |

---

## Running Without Docker

Ensure PostgreSQL is running locally and update your `.env` file accordingly:

```
LOCAL_DB_URL=jdbc:postgresql://localhost:5432/ticketflow
```

Start the application:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## API Overview

| Module         | Base Path         | Authentication |
| -------------- | ----------------- | -------------- |
| Authentication | `/api/v1/auth`    | Public         |
| Events         | `/api/v1/events`  | Authenticated  |
| Tickets        | `/api/v1/tickets` | Authenticated  |
| Users          | `/api/v1/users`   | Authenticated  |
| Admin          | `/api/v1/admin`   | ADMIN Only     |

The Postman collection is available in the **API Collection/** directory.

---

## Project Structure

```text
src/main/java/com/nikhil/ticketflow/
├── auth/           Authentication & JWT
├── security/       Security configuration, filters, CurrentUser
├── users/          User management & organizer workflow
├── events/         Event management
├── tickets/        Booking, QR generation, verification & check-in
├── email/          Email service & templates
└── common/         Shared exceptions, config, S3 service
```

---

## Related Repositories

- Backend: https://github.com/nikhilmangali1/event-ticketing-saas
- Frontend: https://github.com/nikhilmangali1/event-ticketing-saas-frontend

---

## Future Improvements

- Unit & Integration Testing (JUnit 5, Testcontainers)
- GitHub Actions CI/CD
- OpenAPI / Swagger documentation
- Pagination & sorting on event listing
- Input validation hardening
