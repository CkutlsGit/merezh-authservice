# Auth Service - Merezh

Микросервис, отвечающий за аутентификацию, авторизацию и управление токенами.

## 📋 Обзор

Auth Service - это микросервис на Spring Boot, который предоставляет функциональность аутентификации: регистрацию пользователей, вход, выход, обновление токенов и валидацию JWT. Он взаимодействует с User Service для создания и валидации учётных данных, а также хранит refresh-токены пользователей в виде SHA-256 хэшей.

Сервис **не хранит пароли пользователей** - он только хэширует их через BCrypt перед отправкой в User Service и проверяет через `BCrypt.matches` при валидации (запрос идёт в User Service). Auth Service отвечает за **выдачу** access и refresh токенов, а также за **ротацию** refresh-токенов.

## 🚀 Технологический стек

**Backend**

- Java 21 - основной язык
- Spring Boot 3 - фреймворк приложения
- Spring Data JPA - доступ к БД и ORM
- Spring Security Crypto - BCrypt для хэширования паролей
- RestTemplate - синхронные HTTP-вызовы к User Service

**База данных**

- PostgreSQL - основная БД

**DevOps**

- Docker - контейнеризация
- Docker Compose - оркестрация нескольких контейнеров
- Spring Boot Actuator - healthcheck и мониторинг

**Тестирование**

- JUnit 5
- Mockito

## ✨ Возможности

### 🔐 Аутентификация и авторизация

- Регистрация нового пользователя (вызов User Service + выдача токенов)
- Вход в систему (`login`) с проверкой кред через User Service
- Выход из системы (`logout`) с удалением refresh-сессии
- Обновление пары токенов (`refresh`) с ротацией refresh-токена
- Выдача access и refresh JWT

### 🎫 JWT-токены

- **Access token** - короткоживущий (по умолчанию 30 минут), `type: access`
- **Refresh token** - долгоживущий (по умолчанию 7 дней), `type: refresh`
- Оба содержат `sub` (userId), `role`, `type`, `exp`
- Refresh-токены хранятся в БД в виде **SHA-256 хэша** (сам токен не хранится)
- Ротация refresh-токена при каждом обновлении

### 🔗 Интеграция с User Service

- `POST /register` → вызывает User Service `/api/v1/users/create`
- `POST /login` → вызывает User Service `/api/v1/users/validate`
- Пароли хэшируются BCrypt'ом **в Auth Service** перед отправкой в User Service

### ✅ Валидация данных

- Формат email, длина логина, длина пароля
- Единый формат ошибок через `@RestControllerAdvice` с `@Order` (несколько handler'ов)
- Обработка ошибок от User Service (проброс `message` из ответа)
- Обработка JWT-исключений (`ExpiredJwtException`, `MalformedJwtException`)

## 🛠️ Быстрый старт

### Требования

- Docker
- Docker Compose

### Запуск через Docker Compose

```bash
docker compose up --build
```

Сервис будет доступен на порту **8081**. 
Swagger path - `/swagger-ui.html`.

## 📚 Эндпоинты API

Базовый путь: `/api/v1/auth`

| Метод | Эндпоинт   | Описание                                  | Доступ        |
|-------|------------|-------------------------------------------|---------------|
| POST  | `/register`| Регистрация нового пользователя           | Public        |
| POST  | `/login`   | Вход и получение пары токенов             | Public        |
| POST  | `/refresh` | Обновление пары токенов по refresh        | Public        |
| POST  | `/logout`  | Выход (удаление refresh-сессии)           | Authenticated |

**Примечание:** Защищённые эндпоинты ожидают заголовок `X-User-Id`, который устанавливает Gateway.

## 📦 Структура проекта

```
src/main/java/ru/merezh/authservice/
├── config/                    # Spring configuration (RestTemplate, BCrypt)
├── controller/                # REST controllers
├── dto/                       # Data Transfer Objects
│   └── user/                  # DTO для взаимодействия с User Service
├── entity/                    # JPA entities (UserAuth)
├── exception/                 # Custom exceptions and handlers
│   ├── controller/            # @RestControllerAdvice (несколько handler'ов)
│   └── dto/                   # Error response DTOs
├── repository/                # Spring Data JPA repositories
└── service/                   # Business logic (AuthService, JwtService)
```

## 🔒 Безопасность

- **Пароли хэшируются BCrypt'ом** в Auth Service перед отправкой в User Service. User Service хранит уже готовый хэш.
- **Refresh-токены хэшируются SHA-256** перед сохранением в БД. В случае утечки БД сами токены не скомпрометированы.
- **Ротация refresh-токенов**: при каждом `/refresh` старый хэш заменяется новым.
- **JWT подписываются HS256** с секретом из `jwt.secret` (base64).
- **Access-токены короткоживущие** (30 минут), refresh - долгоживущие (7 дней).
- **`/register`, `/login`, `/refresh`** - публичные, **`/logout`** - требует `X-User-Id`.
- **Секрет JWT** должен совпадать с секретом в Gateway, иначе токены не будут валидироваться.

## 🩺 Health Checks

Сервис предоставляет эндпоинты Spring Boot Actuator:

| Эндпоинт                     | Назначение                   |
|------------------------------|------------------------------|
| `/actuator/health`           | Общий статус                 |
| `/actuator/health/liveness`  | Liveness probe               |
| `/actuator/health/readiness` | Readiness probe (включая БД) |
| `/actuator/info`             | Информация о сервисе         |

## 🧪 Тестирование

```bash
mvn test
```

Unit-тесты покрывают основные сценарии `AuthService`:

- `registerUser` с `null`-ответом от User Service → исключение
- `registerUser` с валидными данными → сохранение токенов
- `loginUser` с `null`-ответом от User Service → исключение
- `loginUser` с валидными данными → выдача токенов
- `refreshTokens` без сессии в БД → исключение
- `refreshTokens` с невалидным токеном → исключение

Готово. Теперь у тебя есть README для **auth-service** в том же стиле.

Если что-то хочешь поправить (например, точные значения времени жизни токенов, порт, путь Swagger), скажи - поправлю. Дальше по плану - **wallet-service**, **order-service**, **payment-service**, **gateway** и общий README.
