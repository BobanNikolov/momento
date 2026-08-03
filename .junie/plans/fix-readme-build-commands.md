---
sessionId: session-260803-213854-8ft0
---

# Requirements

### Overview & Goals
Fix incorrect build tool references in `README.md` — the project uses Gradle (`gradlew`) but the README references Maven (`mvnw`).

### Scope
- **In Scope:** Correct the backend service and worker build/run commands in `README.md`
- **Out of Scope:** Flyway migration conflict, any other documentation changes

# Technical Design

### Proposed Changes

Update `README.md` sections 2 (Backend Service) and 3 (Worker) to use Gradle commands:

**Section 2 — Backend Service** (lines 41–44):
```bash
cd momento-service
./gradlew clean build
./gradlew :application:bootRun --args='--spring.profiles.active=local'
```

**Section 3 — Worker** (lines 49–51):
```bash
cd momento-worker
./gradlew :application:bootRun --args='--spring.profiles.active=local'
```

On Windows, `gradlew.bat` is used automatically when running `./gradlew` in Git Bash, or users can run `gradlew.bat` directly in PowerShell/CMD.

# Delivery Steps

### ✓ Step 1: Fix backend service commands in README
README section 2 uses correct Gradle commands instead of Maven.

- Replace `./mvnw clean install` with `./gradlew clean build`
- Replace `./mvnw spring-boot:run -pl application -Dspring-boot.run.profiles=local` with `./gradlew :application:bootRun --args='--spring.profiles.active=local'`

### ✓ Step 2: Fix worker commands in README
README section 3 uses correct Gradle commands instead of Maven.

- Replace `./mvnw spring-boot:run -Dspring-boot.run.profiles=local` with `./gradlew :application:bootRun --args='--spring.profiles.active=local'`