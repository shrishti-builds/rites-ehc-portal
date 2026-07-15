# RITES EHC Backend

Separate backend for the existing frontend at:

`C:\Users\BEENA\Downloads\EHC PROJECT\frontend`

Frontend URL currently running:

`http://localhost:8005`

Backend API base URL:

`http://localhost:8081/api`

## Endpoints Implemented

- `GET /api/cities`
- `GET /api/hospitals`
- `POST /api/hospitals`
- `GET /api/employees/{empNo}`
- `GET /api/requests`
- `POST /api/requests`
- `PUT /api/requests/{ehcId}`
- `GET /api/sbu/requests`
- `POST /api/sbu/requests/{ehcId}/approve`
- `POST /api/sbu/requests/{ehcId}/reject`
- `GET /api/hr/requests`
- `POST /api/hr/requests/{ehcId}/approve`
- `POST /api/hr/requests/{ehcId}/reject`
- `POST /api/hr/requests/{ehcId}/issue-letter`
- `POST /api/requests/{ehcId}/documents/upload-bill`
- `POST /api/requests/{ehcId}/documents/upload-ack`
- `GET /api/finance/requests`
- `POST /api/finance/requests/{ehcId}/recommend`
- `POST /api/finance/requests/{ehcId}/process`

These match the current frontend `api.js` contract.

## Run

Open PowerShell in this folder and run:

```powershell
.\run-backend.ps1
```

Or manually:

```powershell
javac SimpleEhcBackend.java
java SimpleEhcBackend 8081
```

If you want a separate server window, run:

```powershell
.\start-backend-visible.ps1
```

Then in the frontend settings:

- Active Mode: `Live Mode`
- Backend REST Base URL: `http://localhost:8081/api`

## SQL Server Setup

Set these environment variables before starting the backend:

- `EHC_DB_URL=jdbc:sqlserver://localhost:1433;databaseName=ehc;encrypt=true;trustServerCertificate=true`
- `EHC_DB_USER=ehc_rites_user
- `EHC_DB_PASSWORD=Ehc@12345

Make sure the Microsoft SQL Server JDBC driver is available at runtime. The code already expects `com.microsoft.sqlserver.jdbc.SQLServerDriver`.

## Current Storage

The backend now uses SQL Server through JDBC helpers. Use `sql/schema.sql` to create the tables, then start the app with the JDBC driver on the classpath.
