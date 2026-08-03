---
sessionId: session-260703-140816-jp2p
---

# Requirements

### Overview & Goals
Finalize an execution-ready platform plan based on the existing architecture blueprint in `.junie/plans/bootstrap-momento-platform-architecture.md`, using the already-bootstrapped module structures in `momento-service`, `momento-worker`, and `momento-ui`.

### Scope
#### In Scope
- Use `momento-service` as the API service implementation target.
- Use `momento-worker` as the always-on background processing service implementation target.
- Use `momento-ui` as the frontend implementation target.
- Keep and follow current module boundaries already present in backend projects (`api`, `service`, `data`, `application`).
- Implement the previously agreed functional slices (auth, events, uploads, processing pipeline, guest search, retention/cleanup).

#### Out of Scope
- Re-architecting project/module layout.
- New platform-level features beyond the existing bootstrap plan.
- Dependency or stack changes not already reflected in Gradle/Vite baselines.

### Acceptance Criteria
- API, worker, and UI workstreams are clearly separated and sequenced.
- Each workstream maps to concrete project paths and conventions already present in the repo.
- Plan is implementation-ready for immediate execution without redefining architecture.

# Technical Design

### Current Implementation (Repository Findings)
- Architecture baseline already documented in `.junie/plans/bootstrap-momento-platform-architecture.md`.
- `momento-service` and `momento-worker` are both Spring Boot multi-module Gradle projects with identical module split from `settings.gradle.kts`:
  - `api`, `service`, `data`, `application`.
- Backend entry points and runtime configs already exist:
  - `momento-service/application/src/main/java/com/momento/Application.java`
  - `momento-service/application/src/main/resources/application.yml`
  - `momento-worker/application/src/main/java/com/momentoworker/Application.java`
  - `momento-worker/application/src/main/resources/application.yml`
- `momento-ui` currently has instructions and placeholder structure only (`README.md`, `.junie/AGENTS.md.txt`).
- Per-project guidance files exist and will be respected:
  - `momento-service/.junie/AGENTS.md.txt`
  - `momento-worker/.junie/AGENTS.md.txt`
  - `momento-ui/.junie/AGENTS.md.txt`

### Key Decisions
1. **Preserve existing service split as-is**
   - `momento-service` owns synchronous REST/API responsibilities.
   - `momento-worker` owns long-running async processing.
2. **Follow established backend layering**
   - Controllers/DTOs in `api`, business logic in `service`, persistence/migrations in `data`, runtime/config/security in `application`.
3. **Implement by vertical feature slices across all three projects**
   - Build end-to-end slices (API + worker + UI integration points) in a safe sequence instead of isolated random components.
4. **Keep contracts aligned to bootstrap architecture file**
   - Existing endpoint/domain definitions from `.junie/plans/bootstrap-momento-platform-architecture.md` are source-of-truth for first milestone.

### Proposed Changes
- **Backend foundation in both Spring projects**
  - Finalize shared domain baseline, persistence schema/migrations, and environment config alignment for PostgreSQL/Flyway.
- **API delivery in `momento-service`**
  - Implement auth, event management, photographer upload flow, guest public search endpoints, and pre-signed URL orchestration.
- **Worker delivery in `momento-worker`**
  - Implement queue consumer(s), photo processing state transitions, Rekognition indexing/matching jobs, thumbnail generation orchestration, and cleanup/retention jobs.
- **Frontend delivery in `momento-ui`**
  - Implement role-based surfaces (admin/photographer/public guest), direct upload and search flows, and status/result rendering mapped to API contracts.

### File/Module Targets
- `momento-service/{api,service,data,application}/**`
- `momento-worker/{api,service,data,application}/**`
- `momento-ui/src/**` (to be created/expanded per agreed frontend structure)
- Keep architecture contract reference in `.junie/plans/bootstrap-momento-platform-architecture.md`.

# Testing

### Validation Approach
- Validate each feature slice at API, worker, and UI integration boundaries as it is implemented.
- Prefer focused module-level tests in backend layers and critical-flow UI checks for frontend.

### Key Scenarios
- Organizer/admin creates event and gets guest route/QR target.
- Photographer gets upload URLs, confirms uploads, and sees status transitions.
- Worker consumes queued jobs and moves photo states through processing lifecycle.
- Guest accepts consent, uploads selfie, receives matches, and downloads via temporary URL.
- Retention/cleanup removes expired assets and related metadata.

### Edge Cases
- Duplicate queue delivery/idempotent worker handling.
- Partial failures in Rekognition/S3 interactions and retry behavior.
- Consent not accepted blocks guest search.
- Missing/expired pre-signed URLs and unauthorized access attempts.

# Delivery Steps

### ✓ Step 1: Establish backend domain and persistence baseline across service and worker
Core domain schema and shared persistence conventions are in place in both backend projects.
- Align `momento-service` and `momento-worker` module usage (`api/service/data/application`) to the same domain vocabulary from the bootstrap architecture plan.
- Implement core entities/repositories/migrations for users, events, photographer assignment, photos, collections/faces, searches, downloads, and agreements in `data` modules.
- Add foundational configuration wiring in `application` modules for PostgreSQL, Flyway, and environment-driven properties required by API/worker flows.
- Ensure this baseline supports subsequent API endpoints and worker jobs without rework.

### ✓ Step 2: Implement API feature slices in momento-service
`momento-service` exposes the first milestone REST contracts for auth, event management, upload, and guest discovery.
- Add controllers/DTOs in `api` for auth, events, photographer management, upload URL issuance, upload confirmation, guest event view/search, and download URL retrieval.
- Implement orchestration/business logic in `service` for role checks, consent enforcement, queue dispatch, S3 pre-signed URL handling, and event lifecycle operations.
- Integrate persistence operations in `data` repositories and map status transitions used by the async pipeline.
- Keep endpoint contracts aligned with `.junie/plans/bootstrap-momento-platform-architecture.md`.

### * Step 3: Implement continuous processing and cleanup flows in momento-worker
`momento-worker` continuously processes queued photo jobs and executes cleanup/retention logic.
- Implement queue listener/consumer flow in worker modules to pick photo jobs and orchestrate processing lifecycle.
- Add processing services for Rekognition indexing/matching metadata persistence and thumbnail generation/storage updates.
- Implement robust status transitions (`QUEUED`/`PROCESSING`/`PROCESSED`/`FAILED`) with retry-safe/idempotent handling.
- Add cleanup routines for selfie deletion fallback and event-expiration retention workflows that remove storage assets and face-collection data.

### ✓ Step 4: Implement momento-ui role-based flows integrated with API contracts
`momento-ui` provides functional admin, photographer, and guest experiences against the new API.
- Build frontend app structure and route-level feature modules for admin event management, photographer uploads/status, and public guest search/download.
- Implement typed API client/services for auth, events, uploads, search, and download endpoints.
- Wire direct-to-S3 upload UX (pre-signed URL flow), consent gating, and real-time processing/result states.
- Validate end-to-end behavior from UI actions through API responses and worker-driven status updates for the milestone scenarios.