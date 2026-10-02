# Auth Service - Merezh

Microservice responsible for authentication, authorization, and token management.

📖 In Russian: [перевод на русский](#)

## 📋 Overview

Auth Service is a Spring Boot microservice that provides authentication functionality: user registration, login, logout, token refresh, and JWT validation. It interacts with User Service to create and validate credentials and stores refresh tokens as SHA-256 hashes.

The service does **not store user passwords** - it only hashes them with BCrypt before sending to User Service and validates them through `BCrypt.matches` when checking credentials (the request goes to User Service). Auth Service is responsible for **issuing** access and refresh tokens, as well as **rotating** refresh tokens.

## 🚀 Technology Stack

**Backend**

- Java 21 - core language
- Spring Boot 3 - application framework
- Spring Data JPA - database access and ORM
- Spring Security Crypto - BCrypt password encoder
- RestTemplate - synchronous HTTP calls to User Service

**Database**

- PostgreSQL - production database

**DevOps**

- Docker - containerization
- Docker Compose - multi-container orchestration
- Spring Boot Actuator - health checks and monitoring

**Testing**

- JUnit 5
- Mockito

## ✨ Features

### 🔐 Authentication & Authorization

- User registration (calls User Service + issues tokens)
- Login with credential validation via User Service
- Logout (removes the refresh session)
- Token pair refresh with refresh token rotation
- Issue access and refresh JWTs

### 🎫 JWT Tokens

- **Access token** - short-lived (30 minutes by default), `type: access`
- **Refresh token** - long-lived (7 days by default), `type: refresh`
- Both contain `sub` (userId), `role`, `type`, `exp`
- Refresh tokens are stored in the database as **SHA-256 hashes** (the raw token is not stored)
- Refresh token rotation on every refresh

### 🔗 User Service Integration

- `POST /register` → calls User Service `/api/v1/users/create`
- `POST /login` → calls User Service `/api/v1/users/validate`
- Passwords are hashed with BCrypt **in Auth Service** before being sent to User Service

### ✅ Data Validation

- Email format, login length, password length
- Consistent error responses via `@RestControllerAdvice` with `@Order` (multiple handlers)
- Error propagation from User Service (forwards `message` from the response)
- JWT exception handling (`ExpiredJwtException`, `MalformedJwtException`)

## 🛠️ Quick Start

### Prerequisites

- Docker
- Docker Compose

### Run with Docker Compose

```bash
docker compose up --build
```

The service will be available on port **8081**. 
Swagger path - `/swagger-ui.html`.

## 📚 API Endpoints

Base path: `/api/v1/auth`

| Method | Endpoint    | Description                              | Access        |
|--------|-------------|------------------------------------------|---------------|
| POST   | `/register` | Register a new user                      | Public        |
| POST   | `/login`    | Login and receive a token pair           | Public        |
| POST   | `/refresh`  | Refresh the token pair using refresh     | Public        |
| POST   | `/logout`   | Logout (removes the refresh session)     | Authenticated |

**Note:** Protected endpoints expect the `X-User-Id` header, which is set by the Gateway.

## 📦 Project Structure

```
src/main/java/ru/merezh/authservice/
├── config/                    # Spring configuration (RestTemplate, BCrypt)
├── controller/                # REST controllers
├── dto/                       # Data Transfer Objects
│   └── user/                  # DTOs for User Service interaction
├── entity/                    # JPA entities (UserAuth)
├── exception/                 # Custom exceptions and handlers
│   ├── controller/            # @RestControllerAdvice (multiple handlers)
│   └── dto/                   # Error response DTOs
├── repository/                # Spring Data JPA repositories
└── service/                   # Business logic (AuthService, JwtService)
```

## 🔒 Security

- **Passwords are hashed with BCrypt** in Auth Service before being sent to User Service. User Service stores the resulting hash.
- **Refresh tokens are hashed with SHA-256** before being stored in the database. In case of a DB leak, the raw tokens are not compromised.
- **Refresh token rotation**: on every `/refresh`, the old hash is replaced with a new one.
- **JWTs are signed with HS256** using a secret from `jwt.secret` (base64).
- **Access tokens are short-lived** (30 minutes), refresh tokens are long-lived (7 days).
- **`/register`, `/login`, `/refresh`** are public, **`/logout`** requires `X-User-Id`.
- **The JWT secret must match the one in the Gateway**, otherwise tokens will not validate.

## 🩺 Health Checks

The service exposes Spring Boot Actuator endpoints:

| Endpoint                     | Purpose                        |
|------------------------------|--------------------------------|
| `/actuator/health`           | Overall health                 |
| `/actuator/health/liveness`  | Liveness probe                 |
| `/actuator/health/readiness` | Readiness probe (includes DB)  |
| `/actuator/info`             | Service info                   |

## 🧪 Testing

```bash
mvn test
```

Unit tests cover the main `AuthService` flows:

- `registerUser` with a `null` response from User Service → exception
- `registerUser` with valid data → tokens saved
- `loginUser` with a `null` response from User Service → exception
- `loginUser` with valid data → tokens issued
- `refreshTokens` with no session in the DB → exception
- `refreshTokens` with an invalid token → exception
