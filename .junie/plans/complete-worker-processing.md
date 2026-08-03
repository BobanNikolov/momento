---
sessionId: session-260803-140450-1fih
---

# Requirements

### Overview & Goals
Complete the momento-worker Step 3 from the existing plan: implement full photo processing pipeline with face metadata persistence, robust error handling, and cleanup/retention flows.

### Scope
#### In Scope
- Persist Rekognition face metadata (`RekognitionFace` records) after `IndexFaces` calls
- Add retry/idempotency safeguards to photo processing
- Complete `RetentionCleanupService` to delete related DB records during event expiration
- Implement selfie cleanup fallback for `GuestSearch` records with un-deleted selfies

#### Out of Scope
- Changes to `momento-service` or `momento-ui`
- New endpoints or API contracts
- Infrastructure/deployment changes

# Technical Design

### Current Implementation
The worker module already has:
- `PhotoProcessingWorker` — processes photos (thumbnail + face indexing) but **does not persist** `RekognitionFace` records from the Rekognition response
- `SqsQueueListener` (aws profile) and `LocalQueuePoller` (local profile) — queue consumption is functional
- `RekognitionFaceService` — calls `IndexFaces` but discards the response containing `FaceRecord` data
- `LocalFaceService` — logs only, no simulated persistence
- `RetentionCleanupService` — marks events as EXPIRED and deletes S3 assets/collections, but **does not clean up DB records** (photos, faces, searches, downloads). Selfie cleanup is a no-op stub.
- `RekognitionFaceRepository` exists in `data` module but is unused by service layer

### Key Files
- `service/src/main/java/com/momentoworker/service/PhotoProcessingWorker.java`
- `service/src/main/java/com/momentoworker/service/RetentionCleanupService.java`
- `service/src/main/java/com/momentoworker/service/integration/FaceService.java`
- `service/src/main/java/com/momentoworker/service/integration/impl/RekognitionFaceService.java`
- `service/src/main/java/com/momentoworker/service/integration/impl/LocalFaceService.java`
- `data/src/main/java/com/momentoworker/data/model/RekognitionFace.java`
- `data/src/main/java/com/momentoworker/data/repository/RekognitionFaceRepository.java`

### Proposed Changes

1. **Face metadata persistence in `FaceService`**
   - Change `FaceService.indexFace()` return type to return face metadata (faceId, boundingBox, confidence)
   - Update `RekognitionFaceService` to capture `IndexFacesResponse` and return face records
   - Update `LocalFaceService` to return simulated face data
   - In `PhotoProcessingWorker.processPhoto()`, persist returned face data as `RekognitionFace` entities via `RekognitionFaceRepository`

2. **Retry and idempotency improvements**
   - Add a `retryCount` / `lastError` field to `Photo` entity for tracking failures
   - In `PhotoProcessingWorker`, skip already-PROCESSED photos and handle re-processing of FAILED photos
   - In `SqsQueueListener`, add basic error classification to avoid infinite retries on permanent failures

3. **Complete retention cleanup**
   - In `RetentionCleanupService.cleanupExpiredEvents()`, delete related `RekognitionFace`, `Download`, `GuestSearch`, and `Photo` DB records before marking event EXPIRED
   - Implement `cleanupTempSelfies()` to find `GuestSearch` records where `selfie_deleted_at` is null and `created_at` is older than a threshold, then delete their S3 selfie assets and update `selfie_deleted_at`

# Delivery Steps

### ✓ Step 1: Persist face metadata from Rekognition indexing
PhotoProcessingWorker persists RekognitionFace records after indexing.

- Update `FaceService.indexFace()` interface to return a list of face metadata DTOs (faceId, externalImageId, boundingBox, confidence)
- Update `RekognitionFaceService.indexFace()` to capture `IndexFacesResponse` and map `FaceRecord` entries to the return type
- Update `LocalFaceService.indexFace()` to return simulated face metadata
- In `PhotoProcessingWorker.processPhoto()`, inject `RekognitionFaceRepository` and persist returned face data as `RekognitionFace` entities linked to the photo and event

### ✓ Step 2: Add retry tracking and idempotency safeguards
Photo processing handles retries gracefully and tracks failure details.

- Add `retry_count` (int, default 0) and `last_error` (text, nullable) columns to `Photo` via a new Flyway migration
- Update `Photo.java` entity with corresponding fields
- In `PhotoProcessingWorker.processPhoto()`, increment `retryCount` on failure and store truncated error message in `lastError`
- Allow re-processing of FAILED photos (reset to PROCESSING) while skipping PROCESSED ones
- In `SqsQueueListener`, add basic error classification to avoid infinite retries on permanent failures
- In `SqsQueueListener`, skip messages for photos that have exceeded a max retry threshold (configurable via `application.yml`)

### ✓ Step 3: Complete retention and selfie cleanup flows
RetentionCleanupService fully cleans up DB records and handles selfie deletion fallback.

- In `cleanupExpiredEvents()`, before marking event EXPIRED: delete `RekognitionFace` records by event, delete `Download` records by event, delete `GuestSearch` records by event, delete `Photo` records by event (use repository delete methods)
- Add required repository methods (e.g., `deleteByEventId`) to `RekognitionFaceRepository`, `DownloadRepository`, `GuestSearchRepository`, `PhotoRepository`
- Implement `cleanupTempSelfies()`: query `GuestSearchRepository` for records where `selfieDeletedAt` is null and `createdAt` is older than 1 hour, call `storageService.delete()` on their `selfieS3Key`, and set `selfieDeletedAt` to now