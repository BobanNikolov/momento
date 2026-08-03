# Momento

Momento is an automated photo-sharing platform for events. Guests can find their photos instantly by taking a selfie, which the platform uses to match faces across all uploaded event photos.

## Architecture

The platform consists of three main modules:

1.  **`momento-service`**: Spring Boot REST API that handles:
    *   User authentication (JWT) and RBAC (Admin/Photographer).
    *   Event management and photographer assignments.
    *   Photo metadata and upload coordination (Presigned URLs).
    *   Guest search and photo delivery.
2.  **`momento-worker`**: Spring Boot background worker that handles:
    *   Photo processing (resizing, thumbnail generation).
    *   Face detection and metadata extraction.
    *   Automated cleanup of expired events and temporary data.
3.  **`momento-ui`**: React SPA (Vite + Tailwind CSS) providing:
    *   **Admin Dashboard**: Event CRUD, photographer management, processing monitoring.
    *   **Photographer Portal**: Multi-file upload flow with status tracking.
    *   **Guest Flow**: Selfie-based search, photo gallery, and downloads.

## Prerequisites

*   **Java 21** (Amazon Corretto or OpenJDK)
*   **Node.js 18+** & npm
*   **Docker & Docker Compose** (for PostgreSQL)
*   **PostgreSQL 16** (if running without Docker)

## Getting Started

### 1. Database
Start the database using Docker Compose:
```bash
cd momento-service/docker
docker-compose up -d
```

### 2. Backend Service
```bash
cd momento-service
./gradlew clean build
./gradlew :application:bootRun --args='--spring.profiles.active=local'
```
*API will be available at `http://localhost:8080`*

### 3. Worker
```bash
cd momento-worker
./gradlew :application:bootRun --args='--spring.profiles.active=local'
```

### 4. Frontend
```bash
cd momento-ui
npm install
npm run dev
```
*UI will be available at `http://localhost:5173`*

## Profiles

*   **`local`**: Uses local file storage (`momento-service/storage`) and local database.
*   **`aws`**: (Configuration ready) Uses AWS S3 for storage and AWS Rekognition for face matching.

## Key API Endpoints

| Endpoint | Method | Role | Description |
| :--- | :--- | :--- | :--- |
| `/api/auth/register` | `POST` | Public | Create a new Admin or Photographer account |
| `/api/auth/login` | `POST` | Public | Authenticate and get JWT token |
| `/api/event` | `GET/POST` | Admin | List all events or create a new one |
| `/api/event/my-events` | `GET` | User | List events assigned to the current photographer |
| `/api/event/{id}/photos/upload-urls` | `POST` | User | Get presigned URLs for photo uploads |
| `/api/public/event/{slug}/search` | `POST` | Guest | Search photos using a selfie image (Base64) |
