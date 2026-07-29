# 🏥 RITES EHC Portal — Executive Health Checkup Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-11-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.18-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![SQL Server](https://img.shields.io/badge/Microsoft_SQL_Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![Vanilla JS](https://img.shields.io/badge/JavaScript-ES6+-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

**A full-stack enterprise digitized workflow solution replacing legacy paper-based health checkup approvals across SBU, HR, and Finance divisions of RITES Limited.**

</div>

---

## 📋 Table of Contents

- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [Production Readiness Roadmap](#-production-readiness-roadmap)
- [Directory Structure](#-directory-structure)
- [Quick Start](#-quick-start)
- [API Reference](#-api-reference)
- [Screenshots](#-screenshots)
- [Author & License](#-author--license)

---

## 🏛️ System Architecture

```
┌─────────────────────────────────────────────┐
│      Frontend SPA  (Vanilla HTML5/CSS3/JS)  │
│      http://localhost:8005                   │
└────────────────────┬────────────────────────┘
                     │  REST (Fetch API + JWT Auth)
                     ▼
┌─────────────────────────────────────────────┐
│   Spring Boot Backend  (Java 11)            │
│   http://localhost:8080/api                  │
│   Spring Web · Security · JDBC · Mail       │
│   Actuator Health: /actuator/health          │
└────────────────────┬────────────────────────┘
                     │  JDBC (Parameterized Queries)
                     ▼
┌─────────────────────────────────────────────┐
│   Microsoft SQL Server 2019+                │
│   Database: ehc_rites                        │
└─────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| **Frontend** | Vanilla HTML5, CSS3, JavaScript ES6+ (Zero-dependency SPA) |
| **Backend** | Java 11, Spring Boot 2.7.18 |
| **Spring Modules** | Spring Web, Spring JDBC, Spring Validation, Spring Security, Spring Mail, Actuator |
| **Authentication** | JWT (JJWT 0.11.5) — Stateless, Role-Based |
| **Database** | Microsoft SQL Server (mssql-jdbc 12.8.1.jre11) |
| **Connection Pool** | HikariCP 4.0.3 |
| **Build Tool** | Maven 3.8+ |
| **Testing** | JUnit 5, Mockito |
| **Containerization** | Docker & Docker Compose |

---

## ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🔐 **JWT Authentication** | Stateless token-based security with 4 roles: Employee, SBU, HR, Finance |
| 🔄 **One-Click Role Switcher** | Presentation-friendly dropdown to instantly switch roles without logout |
| 📄 **Server-Side Pagination** | `OFFSET/FETCH` SQL pagination with 4-column search across requests |
| 📎 **Multipart File Uploads** | Real binary file uploads for medical bills (PDF/images) stored on server |
| 📧 **Email Notifications** | Auto-emails on every status transition via `JavaMailSender` |
| 💚 **Health Monitoring** | Spring Boot Actuator endpoints (`/actuator/health`, `/actuator/info`, `/actuator/metrics`) |
| 🧪 **Unit Testing** | JUnit 5 + Mockito test suite for service layer verification |
| 🛡️ **XSS Prevention** | `escapeHtml()` applied to all frontend renders |
| 🐳 **Docker Ready** | Multi-stage Dockerfile + docker-compose for one-command deployment |

---

## 🗺️ Production Readiness Roadmap

| # | Step | Status |
|---|------|--------|
| 0 | Security Audit & Architecture Baseline | ✅ Done |
| 1 | DTO Architecture & Global Exception Handling | ✅ Done |
| 2 | Server-Side Pagination & Advanced Search | ✅ Done |
| 3 | JWT Role-Based Auth & One-Click Switcher | ✅ Done |
| 4 | Multipart File Upload for Medical Bills | ✅ Done |
| 5 | Email Notifications (JavaMailSender) | ✅ Done |
| 6 | Actuator Health Check & Unit Testing | ✅ Done |

---

### ✅ Step 0 — Security Audit & Architecture Baseline

> Comprehensive production audit. No code features — hardening only.

| # | Fix Applied | File |
|---|-------------|------|
| 1 | Replaced wildcard CORS with configurable `app.cors.allowed-origins` property | `WebConfig.java` |
| 2 | `escapeHtml()` applied to all frontend table renders to mitigate DOM-XSS | `frontend/app.js` |
| 3 | Non-clustered indexes on `ehc_requests` and `ehc_hospitals` for query performance | `sql/schema.sql` |
| 4 | `SeedData.java` checks `COUNT(*) == 0` before inserting — live data is never truncated on restart | `SeedData.java` |
| 5 | Multi-stage `Dockerfile` + `docker-compose.yml` for containerized deployment | Root directory |

---

### ✅ Step 1 — DTO Architecture & Global Exception Handling

> **Problem:** Backend used brittle regex-based JSON parsing. Errors returned raw Java stack traces.

| File | What Changed |
|------|-------------|
| `dto/RequestDto.java` | Structured DTO replacing manual JSON extraction |
| `dto/DependentDto.java` | Type-safe dependent data transfer object |
| `dto/ApiResponseDto.java` | Uniform success wrapper — `{ success, message, data }` |
| `dto/ErrorResponseDto.java` | Structured error response for all failure cases |
| `dto/PagedResponseDto.java` | Generic paged response wrapper |
| `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` — catches all unhandled exceptions |
| `JdbcRepository.java` | Tightened transactional boundaries |
| `WebConfig.java` | CORS updated to use configurable origins property |

---

### ✅ Step 2 — Server-Side Pagination & Advanced Search

> **Problem:** `GET /api/requests` loaded the entire table — would degrade with 1000+ records.

| File | What Changed |
|------|-------------|
| `JdbcRepository.java` | `listRequestsPaged()` — SQL `OFFSET/FETCH` with 4-column `LIKE` search |
| `service/RequestService.java` | `listRequestsPagedJson()` — builds paged JSON response |
| `controller/RequestController.java` | Accepts `?page=`, `?size=`, `?search=` query parameters |
| `frontend/api.js` | `getRequestsPaged()` — paginated API in live mode; client-side slice in demo mode |

```http
GET /api/requests?page=0&size=10&search=Apollo
```

---

### ✅ Step 3 — JWT Role-Based Authentication & Switcher

> **Goal:** Secure all API endpoints with JWT tokens. Four roles: `EMPLOYEE`, `SBU`, `HR`, `FINANCE`.

| File | What Changed |
|------|-------------|
| `security/SecurityConfig.java` | Spring Security filter chain — stateless sessions, JWT validation |
| `security/JwtTokenProvider.java` | Token generation, validation, and claim extraction |
| `security/JwtAuthFilter.java` | `OncePerRequestFilter` — extracts Bearer token and sets SecurityContext |
| `security/JwtAuthenticationEntryPoint.java` | Returns 401 for unauthenticated requests |
| `controller/AuthController.java` | `GET /api/auth/demo-login?role=SBU` — presentation-friendly token endpoint |
| `frontend/api.js` | `login(role)` — fetches JWT and stores in Authorization header |
| `frontend/index.html` | One-click Role Switcher dropdown in header bar |
| `frontend/app.js` | `setupRoleSwitcher()` — auto-authenticates and refreshes views on role change |

---

### ✅ Step 4 — Multipart File Upload for Medical Bills

> **Goal:** Replace JSON bill payload with real `multipart/form-data` file uploads.

| File | What Changed |
|------|-------------|
| `controller/RequestController.java` | `POST /api/requests/{ehcId}/bill` — accepts `MultipartFile` + `billDetails` |
| `service/RequestService.java` | Saves file to `uploads/` directory, embeds path in response |
| `frontend/api.js` | `uploadRequestBill()` — constructs `FormData` with file + metadata |
| `frontend/index.html` | Added native `<input type="file">` with PDF/image accept filter |
| `frontend/app.js` | Extracts `File` object from DOM and passes to API |

---

### ✅ Step 5 — Email Notifications (JavaMailSender)

> **Goal:** Auto-send emails on status transitions.

| File | What Changed |
|------|-------------|
| `pom.xml` | Added `spring-boot-starter-mail` |
| `application.properties` | Mock SMTP config (`localhost:1025`) for development |
| `service/EmailService.java` | `sendStatusUpdateEmail()` — sends formatted notification emails |
| `service/RequestService.java` | Triggers `EmailService` on every status change |

---

### ✅ Step 6 — Actuator Health Check & Unit Testing

> **Goal:** Spring Boot Actuator + JUnit 5 test coverage.

| File | What Changed |
|------|-------------|
| `pom.xml` | Added `spring-boot-starter-actuator` |
| `application.properties` | Exposed `health`, `info`, `metrics` endpoints |
| `test/.../RequestServiceTest.java` | JUnit 5 + Mockito unit tests for service layer |

**Actuator Endpoints:**

```
GET /actuator/health    → { "status": "UP", "components": { ... } }
GET /actuator/info      → Application metadata
GET /actuator/metrics   → JVM and request metrics
```

---

## 📁 Directory Structure

```
rites-ehc-portal/
│
├── backend/
│   ├── src/main/java/com/rites/ehc/
│   │   ├── controller/
│   │   │   ├── AuthController.java          ← JWT demo-login endpoint
│   │   │   ├── CityController.java
│   │   │   ├── EmployeeController.java
│   │   │   ├── HealthController.java
│   │   │   ├── HospitalController.java
│   │   │   └── RequestController.java       ← Paginated + Multipart upload
│   │   ├── security/
│   │   │   ├── SecurityConfig.java          ← Spring Security config
│   │   │   ├── JwtTokenProvider.java        ← Token generation/validation
│   │   │   ├── JwtAuthFilter.java           ← Bearer token filter
│   │   │   └── JwtAuthenticationEntryPoint.java
│   │   ├── service/
│   │   │   ├── EmailService.java            ← JavaMailSender notifications
│   │   │   ├── RequestService.java          ← Core business logic
│   │   │   ├── CityService.java
│   │   │   ├── EmployeeService.java
│   │   │   └── HospitalService.java
│   │   ├── dto/
│   │   │   ├── ApiResponseDto.java
│   │   │   ├── DependentDto.java
│   │   │   ├── ErrorResponseDto.java
│   │   │   ├── PagedResponseDto.java
│   │   │   └── RequestDto.java
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── Db.java
│   │   ├── EhcBackendApplication.java
│   │   ├── JdbcRepository.java
│   │   ├── SeedData.java
│   │   └── WebConfig.java
│   ├── src/test/java/com/rites/ehc/
│   │   └── service/
│   │       └── RequestServiceTest.java      ← JUnit 5 tests
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── uploads/                              ← Bill file storage (auto-created)
│   ├── sql/
│   │   └── schema.sql
│   ├── cp_clean.txt
│   ├── start-backend.bat
│   └── pom.xml
│
├── frontend/
│   ├── index.html                            ← SPA Layout + Role Switcher
│   ├── app.js                                ← UI Controller + Events
│   ├── api.js                                ← REST Client (Demo/Live)
│   └── styles.css                            ← Vanilla CSS — RITES Branding
│
├── Dockerfile
├── docker-compose.yml
├── .gitignore
├── LICENSE
└── README.md
```

---

## 🚀 Quick Start

### Option 1 — Docker Compose (Recommended)

```bash
git clone <repo_url>
cd rites-ehc-portal

# Start SQL Server + Backend in containers
docker-compose up -d --build
```

| Service | URL |
|---------|-----|
| Backend API | `http://localhost:8080/api` |
| Actuator Health | `http://localhost:8080/actuator/health` |
| SQL Server | `localhost:1433` |

---

### Option 2 — Local Manual Setup (Windows)

#### Step 1: Database

Open **SQL Server Management Studio (SSMS)** and run `backend/sql/schema.sql`.

#### Step 2: Configure

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ehc_rites;encrypt=true;trustServerCertificate=true
spring.datasource.username=ehc_rites_user
spring.datasource.password=Ehc@12345
app.cors.allowed-origins=http://localhost:8005
```

#### Step 3: Start Backend

```powershell
cd backend
.\start-backend.bat
```

> Backend starts at `http://localhost:8080`

#### Step 4: Serve Frontend

```bash
npx http-server frontend -p 8005
```

> Open `http://localhost:8005` in your browser

In **Settings → API Configuration**:
- Mode → `Live Mode`
- Backend URL → `http://localhost:8080/api`

---

## 📡 API Reference

### Authentication

| Method | Endpoint | Params | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/auth/demo-login` | `?role=EMPLOYEE` | Get JWT token for a role (Demo) |

### EHC Requests

| Method | Endpoint | Params | Description |
|--------|----------|--------|-------------|
| `GET` | `/api/requests` | `?page=0&size=10&search=` | List (paginated + searchable) |
| `GET` | `/api/requests/{ehcId}` | — | Get single request |
| `POST` | `/api/requests` | body: request JSON | Submit new request |
| `PUT` | `/api/requests/{ehcId}` | body: `{status, remarks}` | Update status (SBU/HR) |
| `POST` | `/api/requests/{ehcId}/bill` | `multipart/form-data` | Upload bill with file |
| `PUT` | `/api/requests/{ehcId}/approve-bill` | body: `{financeRemarks}` | Finance approve |
| `PUT` | `/api/requests/{ehcId}/reject-bill` | body: `{financeRemarks}` | Finance reject |
| `PUT` | `/api/requests/{ehcId}/disburse` | body: disbursement JSON | Disburse payment |

### Master Data

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/cities` | State-city master list |
| `GET` | `/api/hospitals` | Empanelled hospitals |
| `POST` | `/api/hospitals` | Add new hospital |
| `PUT` | `/api/hospitals/{vendorCode}/rates` | Update hospital rates |
| `GET` | `/api/employees/{empNo}` | Employee profile + dependents |

### Health & Monitoring

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Application health status |
| `GET` | `/actuator/info` | Application info metadata |
| `GET` | `/actuator/metrics` | JVM and request metrics |

---

## 👩‍💻 Author & License

**Developed by:** Shrishti  
**Organization:** RITES Limited (Rail India Technical and Economic Service) — IT Department  
**Year:** 2026

### ⚖️ All Rights Reserved

**© 2026 Shrishti. All rights reserved.**

This project is proprietary. No part of this project may be reproduced, distributed, or transmitted in any form or by any means without the prior written permission of the author.

---

<div align="center">
<sub>Built with ❤️ by Shrishti for RITES Limited | Production-ready enterprise workflow automation</sub>
</div>
