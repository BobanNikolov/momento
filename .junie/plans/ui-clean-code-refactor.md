---
sessionId: session-260803-143344-19rl
---

# Requirements

### Overview & Goals
Switch all database ID generation from `IDENTITY` (PostgreSQL `bigserial`) to `SEQUENCE` strategy with explicit sequences, enabling Hibernate batch inserts and better bulk performance.

### Scope
**In Scope:**
- Add a new Flyway migration creating sequences for all 8 tables in both `momento-service` and `momento-worker`
- Alter existing `id` columns to use the new sequences as defaults
- Update all 9 JPA entity classes in each module to use `GenerationType.SEQUENCE` with `@SequenceGenerator`

**Out of Scope:**
- Changing any business logic
- Adding Hibernate batch configuration (can be done separately)
- Tests

# Technical Design

### Current State
- 8 tables use `bigserial primary key`: `user_account`, `rekognition_collection`, `event`, `event_photographer`, `photo`, `rekognition_face`, `guest_search`, `download`, `organizer_agreement`
- All 9 entity classes in `momento-service/data` and `momento-worker/data` use `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- `bigserial` = implicit sequence + IDENTITY column, but Hibernate treats IDENTITY strategy as requiring immediate INSERT (no batching)
- Service has `V1__Initial_Schema.sql`; worker has `V1` + `V2__Add_Retry_Tracking_To_Photo.sql`

### Proposed Changes

**New migration (service):** `V2__Switch_To_Sequences.sql`
- Create 9 sequences (e.g., `CREATE SEQUENCE user_account_seq START WITH 1 INCREMENT BY 50`)
- `ALTER TABLE ... ALTER COLUMN id SET DEFAULT nextval('..._seq')` for each table
- `SELECT setval('..._seq', COALESCE((SELECT MAX(id) FROM ...), 0) + 1)` to sync with existing data
- `allocationSize=50` on sequences for Hibernate hi-lo optimization

**New migration (worker):** `V3__Switch_To_Sequences.sql`
- Identical sequence definitions (both modules share the same database)

**Entity updates (both modules):** Change all 9 entities from:
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
to:
```java
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "<table>_seq")
@SequenceGenerator(name = "<table>_seq", sequenceName = "<table>_seq", allocationSize = 50)
```

### Key Decisions
1. **`allocationSize=50`** — matches Hibernate default for SEQUENCE, allows batching up to 50 inserts before needing a new sequence fetch
2. **Sync existing data with `setval()`** — prevents ID collisions if tables already have rows
3. **Both modules get migrations** — they share the same DB, but each module manages its own Flyway history; the worker migration is `V3` since `V2` already exists

### Tables & Sequences
 Table | Sequence Name |
---|---|
 user_account | user_account_seq |
 rekognition_collection | rekognition_collection_seq |
 event | event_seq |
 event_photographer | event_photographer_seq |
 photo | photo_seq |
 rekognition_face | rekognition_face_seq |
 guest_search | guest_search_seq |
 download | download_seq |
 organizer_agreement | organizer_agreement_seq |

# Delivery Plan

### Step 1: Create Flyway migrations for sequences
All tables use explicit sequences instead of bigserial identity columns.

- Create `V2__Switch_To_Sequences.sql` in `momento-service/data/src/main/resources/db/migration/`
- Create `V3__Switch_To_Sequences.sql` in `momento-worker/data/src/main/resources/db/migration/`
- Each migration: creates 9 sequences with `INCREMENT BY 50`, alters column defaults, syncs with `setval()`

### Step 2: Update JPA entities to use SEQUENCE strategy
All entity classes use `GenerationType.SEQUENCE` with `@SequenceGenerator`.

- Update 9 entities in `momento-service/data/src/main/java/com/momento/data/model/`
- Update 9 entities in `momento-worker/data/src/main/java/com/momentoworker/data/model/`
- Change `@GeneratedValue(strategy = GenerationType.IDENTITY)` to `@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "<table>_seq")` with matching `@SequenceGenerator`
- Verify both modules compile successfully