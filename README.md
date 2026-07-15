# RITES EHC Portal

Executive Health Checkup (EHC) Portal for RITES.

## Tech Stack
* **Frontend**: Vanilla HTML/CSS/JS (no build step needed)
* **Backend**: Java 11, Spring Boot 2.7.18, JDBC
* **Database**: Microsoft SQL Server

## Project Structure
* `frontend/`: UI source of truth. Open `index.html` in browser.
* `backend/`: Spring Boot REST API.
* `docs/`: API references.

## Setup Instructions

### Database
1. Create a SQL Server database named `ehc_rites`.
2. Ensure credentials are set up.

### Backend Configuration
1. Copy `.env.example` to `.env` (or configure system environment variables).
2. Set your SQL Server credentials in the `.env` file.

### Running Backend
```bash
cd backend
./mvnw spring-boot:run
# Backend runs on http://localhost:8080
```
Note: Ensure you have Maven or the wrapper installed.

### Running Frontend
Serve the `frontend/` directory with any static server:
```bash
npx serve frontend
# Or simply use VS Code Live Server extension on index.html
```
