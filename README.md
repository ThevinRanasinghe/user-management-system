[User_Management_SystemREADME.md](https://github.com/user-attachments/files/30493310/User_Management_SystemREADME.md)
# User Management System

A full-stack user management system built as part of a hands-on training program, covering REST API design with Spring Boot and a React frontend consuming it. The repository is organized as a monorepo with two independent projects:

```
user-management-system/
├── backend/     → Spring Boot REST API (Java, PostgreSQL)
└── frontend/    → React application (Vite, React Router)
```

## Features

- User registration and login
- Full CRUD operations on users (Create, Read, Update, Delete)
- Request validation (required fields, email format, password length) — enforced on both frontend and backend
- Centralized, clean error handling with consistent JSON error responses
- DTO-based API contracts — passwords are never exposed in any API response
- Interactive API documentation and testing via Swagger UI
- A styled React UI: Login, Register, Dashboard, User List, and Edit User pages
- Persisted login session (via `localStorage`) with route-based access control
- CORS configured so the two projects can communicate in local development

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Language | Java 21 |
| Backend Framework | Spring Boot 4 |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Backend Build Tool | Maven |
| API Docs | springdoc-openapi (Swagger UI) |
| Frontend Library | React |
| Frontend Build Tool | Vite |
| Routing | React Router |
| HTTP Client | Axios |

## Architecture Overview

```
React Frontend  →  Spring Boot Controller  →  Service  →  Repository  →  PostgreSQL
  (port 5173)          (port 8080)
```

The backend follows a standard layered architecture (Controller → Service → Repository), with DTOs decoupling the public API contract from the internal database entity. The frontend manages authentication state via React Context and communicates with the backend exclusively through a centralized Axios instance.

## Getting Started

Both projects need to run at the same time for the app to work. Start the backend first.

### 1. Backend Setup

```bash
cd backend
```

**Prerequisites:** Java 21 (JDK), Maven, PostgreSQL running locally.

Create the database:
```sql
CREATE DATABASE user_management_db;
```

Configure `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/user_management_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Run it:
```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

### 2. Frontend Setup

In a separate terminal:
```bash
cd frontend
npm install
npm run dev
```

The app is available at `http://localhost:5173`.

> The frontend's API base URL is set in `frontend/src/api/axiosConfig.js` — update it there if the backend runs on a different host/port.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/register` | Register a new user |
| POST | `/api/login` | Log in with email + password |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get a single user by ID |
| PUT | `/api/users/{id}` | Update a user (password optional) |
| DELETE | `/api/users/{id}` | Delete a user |

### Example: Register Request
```json
POST /api/register
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "securepass123"
}
```

### Example: Success Response
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane@example.com"
}
```

### Example: Validation Error Response
```json
{
  "timestamp": "2026-07-29T10:15:30",
  "status": 400,
  "message": "Validation failed",
  "validationErrors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

## Application Flow (Frontend)

1. **Register** (`/register`) — creates a new account. On success, redirects to Login.
2. **Login** (`/login`) — authenticates against the backend. On success, the returned user (`id`, `name`, `email`) is stored in Context + `localStorage`, then redirects to the Dashboard.
3. **Dashboard** (`/dashboard`) — shows the logged-in user's info.
4. **User List** (`/users`) — table of all users, with Edit and Delete actions.
5. **Edit User** (`/users/edit/:id`) — pre-filled form; password is optional (blank keeps the existing password).

## Known Limitations / Future Improvements

- Passwords are currently stored and compared in **plain text** — password hashing (e.g., BCrypt) is a planned improvement.
- Authentication is not yet token-based (no JWT) — no session/token is issued or validated on the backend after login.
- Database credentials are stored directly in `application.properties` rather than externalized as environment variables.
- No search, pagination, or sorting yet on the User List.
- Route protection on the frontend is a simple per-page check rather than a dedicated `<ProtectedRoute>` wrapper.

## Project Structure

```
user-management-system/
├── backend/
│   └── src/main/java/com/training/user_management_system/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── exception/
│       └── config/
└── frontend/
    └── src/
        ├── api/
        ├── components/
        ├── context/
        └── pages/
```
