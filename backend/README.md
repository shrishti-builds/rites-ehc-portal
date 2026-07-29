# ⚙️ RITES EHC — Spring Boot Backend

> Java 11 · Spring Boot 2.7.18 · Spring JDBC · Microsoft SQL Server

---

## 🔗 URLs

| Component | URL |
|-----------|-----|
| Backend API Base | `http://localhost:8080/api` |
| Frontend SPA | `http://localhost:8005` |

---

## ▶️ Starting the Backend (Windows)

```powershell
# From this backend/ directory
.\start-backend.bat
```

The script reads the full Maven dependency classpath from `cp_clean.txt` and launches:

```
java -cp <classpath> com.rites.ehc.EhcBackendApplication
```

---

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=ehc_rites;encrypt=true;trustServerCertificate=true
spring.datasource.username=ehc_rites_user
spring.datasource.password=Ehc@12345
app.cors.allowed-origins=http://localhost:8005
```

Run `sql/schema.sql` in SSMS to create all tables and indexes before starting.

---

## 📡 API Endpoints

### EHC Requests — Paginated & Searchable ✅

| Method | Endpoint | Query Params | Description |
|--------|----------|-------------|-------------|
| `GET` | `/api/requests` | `page`, `size`, `search` | Paginated list with search |
| `GET` | `/api/requests/{ehcId}` | — | Get single request |
| `POST` | `/api/requests` | — | Submit new request |
| `PUT` | `/api/requests/{ehcId}` | — | Update status (SBU / HR) |
| `PUT` | `/api/requests/{ehcId}/bill` | — | Upload bill details |
| `PUT` | `/api/requests/{ehcId}/approve-bill` | — | Finance: approve bill |
| `PUT` | `/api/requests/{ehcId}/reject-bill` | — | Finance: reject bill |
| `PUT` | `/api/requests/{ehcId}/disburse` | — | Finance: disburse payment |

### Pagination Query Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | `0` | Zero-based page number |
| `size` | `10` | Records per page (max 100) |
| `search` | `""` | Searches: EHC ID, employee name, hospital name, status |

**Example calls:**

```http
GET /api/requests
GET /api/requests?page=1&size=5
GET /api/requests?search=Apollo
GET /api/requests?page=0&size=10&search=Pending SBU
```

**Response shape:**

```json
{
  "content": [ { "ehcId": "EHC-123456", "empName": "RAHUL KUMAR", ... } ],
  "page": 0,
  "size": 10,
  "totalElements": 47,
  "totalPages": 5,
  "last": false
}
```

### Master Data

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/cities` | State-city master |
| `GET` | `/api/hospitals` | Empanelled hospitals |
| `POST` | `/api/hospitals` | Add hospital |
| `PUT` | `/api/hospitals/{vendorCode}/rates` | Update rates |
| `GET` | `/api/employees/{empNo}` | Employee + dependents |

---

## 🗂️ Key Source Files

| File | Purpose |
|------|---------|
| `JdbcRepository.java` | All SQL — `listRequestsPaged()`, `countRequests()`, transactions |
| `service/RequestService.java` | Business logic + JSON response building |
| `controller/RequestController.java` | REST endpoints with `@RequestParam` pagination |
| `dto/PagedResponseDto.java` | Generic paged response wrapper |
| `dto/ApiResponseDto.java` | Uniform success response `{success, message, data}` |
| `dto/ErrorResponseDto.java` | Structured error response |
| `exception/GlobalExceptionHandler.java` | `@RestControllerAdvice` — clean JSON errors |
| `WebConfig.java` | Configurable CORS (no wildcards) |
| `SeedData.java` | Safe startup init — only runs when tables are empty |
| `sql/schema.sql` | DDL schema + non-clustered performance indexes |

---

## 📦 DTOs

```
dto/
├── ApiResponseDto.java       { success: bool, message: string, data: T }
├── DependentDto.java         { name, relation, dob, gender }
├── ErrorResponseDto.java     { timestamp, status, error, message, path }
├── PagedResponseDto.java     { content: List<T>, page, size, totalElements, totalPages, last }
└── RequestDto.java           { empNo, empName, designation, division, mobile, ... dependents[] }
```

---

## 🗄️ Database Tables

| Table | Description |
|-------|-------------|
| `ehc_requests` | Main request records |
| `ehc_request_dependents` | Family members per request |
| `ehc_employees` | Employee master (auto-registered on first request) |
| `ehc_employee_dependents` | Employee family members |
| `ehc_hospitals` | Empanelled hospital master |
| `ehc_cities` | State-city master |
| `ehc_status_history` | Full audit trail of every status change |
| `ehc_documents` | Bill/document records per request |
| `ehc_payment_recommendations` | Finance payment recommendations |
| `ehc_payments` | Actual payment disbursement records |
