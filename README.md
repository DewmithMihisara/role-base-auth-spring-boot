# Role-Based Authentication — Spring Boot Template

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)
![Swagger](https://img.shields.io/badge/Docs-Swagger%20UI-green?logo=swagger)

## About

A production-ready, reusable **Role-Based Access Control (RBAC) authentication template** built with Spring Boot 3, Spring Security, and JWT. It provides a complete foundation for securing REST APIs — covering user management, role assignment, hierarchical menu and permission control, account lock-out on repeated failed logins, and password history tracking. Designed to be cloned and extended as the auth backbone for any new Spring Boot project.

---

## Features

- JWT-based stateless authentication (Bearer token)
- Role-based access control — User → Role → Menu → Permission hierarchy
- Account lock-out after repeated failed login attempts
- Password history tracking
- OTP entity support for future 2FA flows
- Paginated list APIs across all resources
- Swagger / OpenAPI 3.0 interactive documentation
- BCrypt password encoding
- Profile-based configuration (`dev` / `prod`)

---

## How It Works

### Authentication Flow

```text
Client
  │
  ├─ POST /auth/login  ──────────────────────────────────────────►  AuthController
  │   { email, password }                                              │
  │                                                                   AuthService
  │                                                                    │  verify credentials
  │                                                                    │  check lock status
  │                                                                    │  generate JWT (roles embedded)
  │◄── { token, roles, userInfo } ───────────────────────────────────┘
  │
  ├─ GET/POST /protected  (Authorization: Bearer <token>)
  │                                                         JWTFilter
  │                                                          │  extract token
  │                                                          │  validate signature & expiry
  │                                                          │  load roles from claims
  │                                                          │  set SecurityContext
  │                                                          ▼
  │                                                    Controller → Service → Repository → MySQL
  │◄── Response ───────────────────────────────────────────────────────────────────────────────
```

### Entity Model

```text
User ──────── UserRole ──────── Role
  │                               │
  │                            MenuRole ──────── Menu
  │                                                │
UserMenu                                   PermissionMenu ──── Permission
                                                │
                                        RoleMenuPermission
                                    (composite role+menu+permission)
```

Each user is assigned one or more roles. Roles control which menu items are accessible and what permissions apply to each menu. The `load_menu` endpoint returns the full permission-aware menu tree for the authenticated user.

---

## Technology Stack

| Layer | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.4.2 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8 |
| API Docs | SpringDoc OpenAPI 3 / Swagger UI |
| Build Tool | Apache Maven |
| Utilities | Lombok |

---

## API Endpoints

Base URL: `http://localhost:8080/template/api/`

### Auth

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/login` | Authenticate user, returns JWT | Public |

### User

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/user` | Create or update user | Required |
| GET | `/user/dis/{id}` | Disable user | Required |
| GET | `/user/enb/{id}` | Enable user | Required |
| GET | `/user/unlocked/{id}` | Unlock locked account | Required |
| POST | `/user/all` | List all users (paginated) | Required |
| POST | `/user/select_by_type` | Filter users by type | Required |
| POST | `/user/change_password` | Change own password | Required |
| POST | `/user/set_password` | Set initial password (first login) | Public |
| GET | `/user/load_menu` | Load role-aware menu for current user | Required |

### Role

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/role` | Create or update role | Required |
| GET | `/role/dis/{id}` | Disable role | Required |
| GET | `/role/enb/{id}` | Enable role | Required |
| GET | `/role/all` | List all roles (paginated) | Required |

### Menu

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/menu` | Create or update menu item | Required |
| GET | `/menu/dis/{id}` | Disable menu item | Required |
| GET | `/menu/enb/{id}` | Enable menu item | Required |
| GET | `/menu/all` | List all menus (paginated) | Required |
| GET | `/menu/select_by_type` | Filter menus by type | Required |

---

## Prerequisites

- Java 21 or later
- Maven 3.8 or later
- MySQL 8 running locally

---

## How to Run

### 1. Configure the Database

Open `src/main/resources/application-dev.properties` and update the credentials if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/template?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_password
```

The `template` schema is created automatically on first run.

### 2. Clone and Build

```bash
git clone https://github.com/DewmithMihisara/role-base-auth-spring-boot.git
cd role-base-auth-spring-boot
mvn clean install
```

### 3. Run

```bash
mvn spring-boot:run
```

Or run `TemplateApplication.java` directly from your IDE.

### 4. Access Swagger UI

```text
http://localhost:8080/template/api/swagger-ui.html
```

Use the **Authorize** button to paste your JWT token and test protected endpoints.

---

## Configuration Reference

All settings below are in `src/main/resources/application-dev.properties`.

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | HTTP server port |
| `server.servlet.context-path` | `/template/api/` | API base path |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/template` | MySQL connection URL |
| `spring.datasource.username` | `root` | Database username |
| `spring.datasource.password` | — | Database password |
| `security.jwt.secret` | _(256-char string)_ | HMAC-SHA256 signing key |
| `security.jwt.expire` | `900000000` | Access token TTL (ms) |
| `security.jwt.refresh.expire` | `1800000` | Refresh token TTL (ms) |

For production, copy values into `application-prod.properties` and activate with `--spring.profiles.active=prod`.

---

## GitHub About Description

> Spring Boot 3 JWT role-based authentication template — User, Role, Menu & Permission management with account locking and Swagger UI.
