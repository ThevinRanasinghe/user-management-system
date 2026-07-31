# User Management System

A full-stack user management system built, covering REST API design with Spring Boot, JWT-based authentication, and a React frontend consuming it. The repository is organized as a monorepo with two independent projects:

```
user-management-system/
├── backend/     → Spring Boot REST API (Java, PostgreSQL, Spring Security, JWT)
└── frontend/    → React application (Vite, React Router)
```

## Features

- User registration and login
- **Passwords hashed with BCrypt** — never stored or transmitted in plain text
- **JWT authentication** — stateless, token-based access control on all protected endpoints
- Full CRUD operations on users (Create, Read, Update, Delete)
- **Search** users by name or email
- **Pagination and sorting** (by name, email, or id; ascending or descending) on both the full user list and search results
- Request validation (required fields, email format, password length) — enforced on both frontend and backend
- Centralized, clean error handling with consistent JSON error responses
- DTO-based API contracts — passwords are never exposed in any API response
- Interactive API documentation and testing via Swagger UI, with JWT-aware "Authorize" support
- A styled React UI: Login, Register, Dashboard, User List (with search/sort/pagination), and Edit User pages
- Persisted login session (via `localStorage`) with route-based access control
- CORS properly configured to work alongside Spring Security's filter chain

## Tech Stack

| Layer | Technology |
|---|---|
| Backend Language | Java 21 |
| Backend Framework | Spring Boot 4 |
| Security | Spring Security + JWT (jjwt) |
| Password Hashing | BCrypt |
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
React Frontend  →  JwtAuthFilter  →  Spring Boot Controller  →  Service  →  Repository  →  PostgreSQL
  (port 5173)      (validates JWT)       (port 8080)
```

The backend follows a standard layered architecture (Controller → Service → Repository), with DTOs decoupling the public API contract from the internal database entity. A custom `JwtAuthFilter` runs before Spring Security's default filters, validating the `Authorization: Bearer <token>` header on every request and populating the security context when the token is valid. The frontend manages authentication state via React Context, persists the JWT in `localStorage`, and attaches it automatically to every outgoing request via an Axios interceptor.

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

jwt.secret=your-long-random-secret-key-at-least-32-characters
jwt.expiration=3600000
```

> **Security note:** `jwt.secret` should be a long, randomly generated value, and in a real deployment should be supplied via an environment variable rather than committed to source control. `jwt.expiration` is in milliseconds (`3600000` = 1 hour).

Run it:
```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```
To test protected endpoints in Swagger, log in via `/api/login`, copy the returned `token`, click **Authorize**, and enter `Bearer <token>`.

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

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| POST | `/api/register` | No | Register a new user |
| POST | `/api/login` | No | Log in; returns user info + JWT |
| GET | `/api/users` | Yes | Get all users (paginated + sortable) |
| GET | `/api/users/search` | Yes | Search users by name/email (paginated + sortable) |
| GET | `/api/users/{id}` | Yes | Get a single user by ID |
| PUT | `/api/users/{id}` | Yes | Update a user (password optional) |
| DELETE | `/api/users/{id}` | Yes | Delete a user |

### Pagination & Sorting Query Parameters
Applies to `GET /api/users` and `GET /api/users/search`:

| Parameter | Default | Description |
|---|---|---|
| `page` | `0` | Zero-indexed page number |
| `size` | `10` | Number of results per page |
| `sortBy` | `id` | Field to sort by (`id`, `name`, or `email`) |
| `direction` | `asc` | `asc` or `desc` |
| `query` | — | Required for `/search`; matches against name or email |

Example:
```
GET /api/users?page=0&size=5&sortBy=name&direction=desc
GET /api/users/search?query=jane&page=0&size=5
```

### Example: Register Request
```json
POST /api/register
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "securepass123"
}
```

### Example: Login Response
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane@example.com",
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW5lQGV4YW1wbGUuY29tIiwiaWF0IjoxNzUzNzY0MDAwLCJleHAiOjE3NTM3Njc2MDB9.xxxxx"
}
```

### Example: Paginated List Response
```json
{
  "content": [
    { "id": 1, "name": "Jane Doe", "email": "jane@example.com" }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "number": 0,
  "size": 10,
  "first": true,
  "last": true
}
```

### Example: Validation Error Response
```json
{
  "timestamp": "2026-07-31T10:15:30",
  "status": 400,
  "message": "Validation failed",
  "validationErrors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

## Authentication Flow

1. User registers via `/api/register` — password is hashed with BCrypt before being stored.
2. User logs in via `/api/login` — the backend verifies the password against the stored hash, and if valid, issues a signed JWT (1 hour expiry by default) alongside the user's basic info.
3. The frontend stores the returned user object (including the token) in React Context and `localStorage`, so the session survives a page refresh.
4. Every subsequent API request from the frontend automatically includes `Authorization: Bearer <token>`, attached via an Axios request interceptor.
5. On the backend, `JwtAuthFilter` validates the token's signature and expiration on every request before it reaches any controller. Requests to protected endpoints without a valid token are rejected with `401 Unauthorized`.

## Application Flow (Frontend)

1. **Register** (`/register`) — creates a new account. On success, redirects to Login.
2. **Login** (`/login`) — authenticates against the backend and stores the returned JWT. Redirects to the Dashboard.
3. **Dashboard** (`/dashboard`) — shows the logged-in user's info.
4. **User List** (`/users`) — paginated, sortable table of all users, with search, Edit, and Delete actions.
5. **Edit User** (`/users/edit/:id`) — pre-filled form; password is optional (blank keeps the existing password).

## Known Limitations / Future Improvements

- No refresh-token mechanism — once a JWT expires (default 1 hour), the user must log in again rather than being silently re-authenticated.
- No role-based access control (e.g., admin vs. regular user) — any authenticated user can access all user management endpoints.
- Database credentials and the JWT secret are set via `application.properties`; a production setup should externalize these as environment variables.
- No automated tests (unit or integration) have been written yet.
- Route protection on the frontend is a simple per-page check rather than a dedicated `<ProtectedRoute>` wrapper component.

## Project Structure

```
user-management-system/
├── backend/
│   └── src/main/java/com/training/userManagementSystem/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── exception/
│       ├── security/        (JwtUtil, JwtAuthFilter)
│       └── config/          (SecurityConfig, SecurityBeansConfig, WebConfig)
└── frontend/
    └── src/
        ├── api/
        ├── components/
        ├── context/
        └── pages/
```
