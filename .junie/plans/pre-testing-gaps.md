---
sessionId: session-260803-211307-1ozq
---

# Requirements

### Overview & Goals
Address 7 identified gaps in the Momento platform before moving to testing, ensuring all core flows are functional end-to-end.

### Scope
#### In Scope
1. **Registration Page** — Add a registration form in the UI so admins/photographers can create accounts
2. **Event Edit UI** — Add edit functionality to `EventDetailModal` using the existing `PUT /api/event/{id}` endpoint
3. **QR Code Display** — Show the event QR code in the admin event detail view so organizers can share it with guests
4. **Photographer Event Filtering** — Add a backend endpoint to list only events assigned to the current photographer, and use it in the photographer upload page
5. **Local Storage Auth Fix** — Permit `/api/local/storage/**` in `SecurityConfig` so guest selfie uploads work in local profile
6. **Nginx Production Config** — Add `nginx.conf` for serving the built UI and proxying `/api` to the backend
7. **README** — Add setup instructions, architecture overview, and how-to-run guide

#### Out of Scope
- New backend features beyond photographer filtering endpoint
- Automated tests (deferred to testing phase)
- AWS deployment configuration

### Functional Requirements
- Registration page accessible from login screen with email, password, and role selection (ADMIN/PHOTOGRAPHER)
- Event detail modal has an "Edit" mode toggling inline form fields for name, location, date, retention days
- QR code rendered as an image/SVG in event detail, with a copy-link button for the guest URL (`/e/{slug}`)
- `GET /api/event/my-events` returns events where the authenticated photographer is assigned; photographer upload page uses this instead of all events
- Local storage endpoints are accessible without authentication (local profile only)
- Nginx config serves static UI files and reverse-proxies `/api` to `localhost:8080`
- README documents: project overview, prerequisites, how to run (local profile), module structure, key endpoints

# Technical Design

### Current Implementation
- **Login page** (`momento-ui/src/pages/Login.tsx`): login-only form, no registration link
- **Auth API** (`api.ts`): `authApi.register()` already exists but is unused in UI
- **EventDetailModal** (`components/admin/EventDetailModal.tsx`): read-only view with expire/delete actions
- **EventResponse**: has `qrCodeUrl` field but no UI renders it
- **PhotographerUpload** (`pages/PhotographerUpload.tsx`): calls `useEvents()` which fetches ALL events
- **SecurityConfig**: `/api/local/storage/**` not in `permitAll()` list
- **No `nginx.conf`** exists anywhere in the project
- **README.md**: contains only `# momento`

### Proposed Changes

#### 1. Registration Page
- New file: `momento-ui/src/pages/Register.tsx` — form with email, password, role dropdown
- Update `App.tsx`: add `/register` route
- Update `Login.tsx`: add "Create account" link to `/register`
- New hook: `momento-ui/src/hooks/useRegister.ts` — calls `authApi.register()`, redirects to login on success

#### 2. Event Edit UI
- Modify `momento-ui/src/components/admin/EventDetailModal.tsx`: add edit mode toggle, inline form fields, save button calling `eventApi.update()`
- Update `momento-ui/src/hooks/useEventDetail.ts`: add `updateEvent` function

#### 3. QR Code Display
- Install `qrcode.react` package (or use a lightweight QR SVG generator)
- Add QR code component to `EventDetailModal` showing the guest URL `/e/{slug}`
- Add copy-to-clipboard button for the guest URL

#### 4. Photographer Event Filtering
- **Backend**: Add `GET /api/event/my-events` endpoint in `EventController.java` that queries `EventPhotographerRepository` for the authenticated user's assignments and returns their events
- Add `EventService.getMyEvents(Long photographerId)` method
- **SecurityConfig**: add `.requestMatchers("/api/event/my-events").hasAnyRole("ADMIN", "PHOTOGRAPHER")` 
- **UI**: Add `eventApi.myEvents()` in `api.ts`
- Update `PhotographerUpload.tsx` / `useEvents.ts` to call `myEvents()` when user role is PHOTOGRAPHER

#### 5. Local Storage Auth Fix
- Modify `SecurityConfig.java`: add `.requestMatchers("/api/local/storage/**").permitAll()` (this controller only exists under `local` profile anyway)

#### 6. Nginx Config
- New file: `momento-ui/nginx.conf`
- Serves `/` from `/usr/share/nginx/html` with `try_files $uri /index.html` for SPA routing
- Proxies `/api` to `http://momento-service:8080`

#### 7. README
- Rewrite `README.md` with: project description, architecture diagram (3 modules), prerequisites (Java 21, Node 18+, PostgreSQL/Docker), local setup steps, module descriptions, key API endpoints summary

# Delivery Steps

### ✓ Step 1: Add registration page and route
Users can register new accounts from the UI.

- Create `momento-ui/src/pages/Register.tsx` with email, password, confirm password, and role selector (ADMIN/PHOTOGRAPHER)
- Create `momento-ui/src/hooks/useRegister.ts` calling `authApi.register()` with success redirect to `/login`
- Add `/register` route in `App.tsx`
- Add "Create account" link in `Login.tsx`

### ✓ Step 2: Add event edit UI and QR code display
Admins can edit event details and view/share the QR code for guest access.

- Update `EventDetailModal.tsx` to support an edit mode with inline form fields (name, location, date, retention days) and a save button
- Add `updateEvent` method to `useEventDetail.ts` hook calling `eventApi.update()`
- Install `qrcode.react` (or equivalent lightweight library)
- Add QR code rendering in `EventDetailModal` showing the guest URL `/e/{slug}`
- Add a copy-to-clipboard button for the guest URL

### ✓ Step 3: Add photographer event filtering endpoint and wire UI
Photographers only see events they are assigned to.

- Add `GET /api/event/my-events` endpoint in `EventController.java`
- Add `EventService.getMyEvents(Long photographerId)` querying `EventPhotographerRepository.findByPhotographer()` and mapping to `EventResponse`
- Add security rule in `SecurityConfig.java` for the new endpoint
- Add `eventApi.myEvents()` in `momento-ui/src/api.ts`
- Update `useEvents.ts` or `PhotographerUpload.tsx` to call `myEvents()` when user role is PHOTOGRAPHER

### ✓ Step 4: Fix local storage auth, add nginx config, and write README
Local profile works end-to-end, production UI serving is configured, and the project is documented.

- Add `.requestMatchers("/api/local/storage/**").permitAll()` to `SecurityConfig.java`
- Create `momento-ui/nginx.conf` with SPA routing (`try_files`) and `/api` reverse proxy
- Rewrite `README.md` with project overview, architecture description (service/worker/UI modules), prerequisites, local setup instructions, and key API endpoints summary