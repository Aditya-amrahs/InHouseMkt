# InHouse Marketplace — Employee-to-Employee Trading Platform

> **Capstone Project** | Full-Stack Java + React | NaviKenz Training Program 2026

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Repository](#repository)
3. [Tech Stack](#tech-stack)
4. [Microservices Architecture](#microservices-architecture)
5. [System Architecture Diagram](#system-architecture-diagram)
6. [Data Model](#data-model)
7. [REST API Reference](#rest-api-reference)
8. [Individual User Stories](#individual-user-stories)
9. [Challenges Faced & How We Solved Them](#challenges-faced--how-we-solved-them)
10. [Things We Learned](#things-we-learned)
11. [Running the Project](#running-the-project)
12. [Team](#team)

---

## Project Overview

**InHouse Marketplace** is an internal company platform that lets employees trade goods and services with each other — without leaving the organisation's network. A colleague looking for PG accommodation, selling a phone, offering car-pool rides, or seeking freelance help from a peer can post a **Requirement** or an **Offer**. Other employees respond with **Proposals**, and deals are negotiated inside the app.

The application enforces a full authentication flow: every marketplace action (viewing listings, posting, proposing) requires a valid session, keeping the platform private to company employees.

---

## Repository

| Resource | Link |
|---|---|
| **GitHub Repository** | [https://github.com/Aditya-amrahs/InHouseMkt](https://github.com/Aditya-amrahs/InHouseMkt) |
| **Backend entry point** | `marketplace-backend/src/main/java/com/inhouse/marketplace/` |
| **React frontend** | `marketplace-react/src/` |
| **Legacy Angular frontend** | `marketplace-frontend/src/` |
| **Docker Compose** | `docker-compose.yaml` |

---

## Tech Stack

### Backend
| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL (Docker) / H2 (tests) |
| Build tool | Maven |
| Testing | JUnit 5, Mockito, MockMvc |
| Coverage | JaCoCo (80% gate) |
| API Docs | Springdoc OpenAPI (Swagger UI) |

### Frontend (Production)
| Layer | Technology |
|---|---|
| Framework | React 19 + Vite 8 |
| Routing | React Router v7 |
| HTTP client | Axios |
| Icons | Lucide React |
| State | React Context + useReducer |
| Styling | Vanilla CSS (custom design system) |

### Legacy Frontend (Angular)
| Layer | Technology |
|---|---|
| Framework | Angular 16 |
| HTTP | Angular HttpClient |
| Styling | Angular component CSS |

### Infrastructure
| Tool | Purpose |
|---|---|
| Docker + Docker Compose | Containerised MySQL + app deployment |
| Git / GitHub | Version control & collaboration |

---

## Microservices Architecture

Although the backend is deployed as a single Spring Boot monolith for simplicity, the **internal design strictly follows a microservice-inspired layered architecture** with fully separated concerns per domain. Each domain (User, Employee, Requirement, Offer, Proposal, Resource) has its own:

- **Repository interface** — data access contract
- **Service interface** — business logic contract
- **Controller** — REST endpoint handler
- **Entity/POJO** — JPA-mapped domain object

```
+------------------------------------------------------------------+
|                    CLIENT LAYER                                   |
|          React 19 (Vite)  --------  Angular 16                   |
|               localhost:5173          localhost:4200              |
+---------------------------+-------------------------------------- +
                            |  HTTP/REST (JSON)
                            |  CORS: localhost:5173, localhost:4200
+---------------------------v-------------------------------------- +
|                  SPRING BOOT API GATEWAY                          |
|                     localhost:8080/api                            |
|                                                                   |
|  +---------------+  +--------------+  +----------------------+   |
|  | UserController|  | EmpController|  | RequirementController|   |
|  |  /users/**    |  | /employees/**|  |  /requirements/**    |   |
|  +-------+-------+  +------+-------+  +----------+-----------+   |
|          |                 |                     |               |
|  +-------v-------+  +------v-------+  +----------v-----------+   |
|  | IUserService  |  |IEmployeeSvc  |  |  IRequirementService |   |
|  +-------+-------+  +------+-------+  +----------+-----------+   |
|          |                 |                     |               |
|  +-------v-------+  +------v-------+  +----------v-----------+   |
|  |IUserRepository|  |IEmpRepository|  |  IRequirementRepo    |   |
|  +---------------+  +--------------+  +----------------------+   |
|                                                                   |
|  +---------------+  +--------------+  +----------------------+   |
|  | OfferController|  |PropController|  | ResourceController  |   |
|  |  /offers/**   |  |/proposals/** |  |   /resources/**     |   |
|  +-------+-------+  +------+-------+  +----------+-----------+   |
|          |                 |                     |               |
|  +-------v-------+  +------v-------+  +----------v-----------+   |
|  | IOfferService |  |IProposalSvc  |  |   IResourceService   |   |
|  +-------+-------+  +------+-------+  +----------+-----------+   |
|          |                 |                     |               |
|  +-------v-------+  +------v-------+  +----------v-----------+   |
|  |IOfferRepository|  |IProposalRepo |  |  IResourceRepository|   |
|  +---------------+  +--------------+  +----------------------+   |
+---------------------------+-------------------------------------- +
                            |  JPA / Hibernate / SQL
+---------------------------v-------------------------------------- +
|                      DATABASE LAYER                               |
|               MySQL 8  (Docker: port 3306)                        |
|               H2 in-memory  (tests only)                          |
|                                                                   |
|   Tables:  users | employees | resources (SINGLE_TABLE)           |
|            proposals                                              |
+-------------------------------------------------------------------+
```

### Domain Service Interfaces Summary

| Interface | Responsibilities |
|---|---|
| `IUserService` | `register`, `login`, `logout`, `editUser`, `removeUser` |
| `IEmployeeService` | `addEmployee`, `editEmployee`, `getEmployee`, `updateIsFulfilled`, `updateIsAvailable`, `updateIsAccepted`, `getAllOffers(empId)`, `getAllRequirements(empId)` |
| `IRequirementService` | `addRequirement`, `editRequirement`, `getRequirement`, `removeRequirement`, `getAllRequirements()`, `getAllRequirements(category, type)` |
| `IOfferService` | `addOffer`, `editOffer`, `getOffer`, `removeOffer`, `getAllOffers()`, `getAllOffers(category, type)` |
| `IProposalService` | `addProposal`, `editProposal`, `getProposal`, `removeProposal`, `getAllProposals` |
| `IResourceService` | `getAllResources(category, type)`, `getAllResources(empId)` — polymorphic query returning Requirements + Offers in one list |

---

## System Architecture Diagram

```
Presentation  +--------------------------------------------------+
              |  React 19 SPA  (marketplace-react)              |
              |                                                  |
              |  Pages:  Login · Register · Marketplace         |
              |          Dashboard · RequirementDetail           |
              |          OfferDetail · RequirementForm           |
              |          OfferForm                               |
              |                                                  |
              |  State:  AuthContext (sessionStorage)            |
              |  Auth:   ProtectedRoute — all marketplace views  |
              |          require a valid session                 |
              +------------------+-------------------------------+
                                 | Axios  /api/*
Application   +------------------v-------------------------------+
              |  Spring Boot 3  (marketplace-backend)           |
              |                                                  |
              |  Controllers -> Service Interfaces -> Impl       |
              |                                                  |
              |  Cross-cutting: CORS, Jackson, Swagger UI        |
              +------------------+-------------------------------+
                                 | Spring Data JPA
Data Access   +------------------v-------------------------------+
              |  Repository Interfaces  (JpaRepository)         |
              |  Hibernate ORM  ->  SINGLE_TABLE inheritance     |
              +------------------+-------------------------------+
                                 | SQL
Database      +------------------v-------------------------------+
              |  MySQL 8  (Docker)  /  H2 (test)                |
              +--------------------------------------------------+
```

---

## Data Model

```
User (userId PK, password)
  | 1:1
  v
Employee (empId PK, empName, deptName, location, userId FK)
  | 1:N
  v
Resource  <- abstract base  [SINGLE_TABLE, discriminator: resource_type]
  +- Requirement  (fulfilled, fulfilledOn,  proposals[])
  +- Offer        (available, availableUpto, proposals[])
       | 1:N
       v
    Proposal (propId PK, proposal text, amount,
              proposalDate, accepted, acceptedOn,
              empId FK, resId FK)
```

### Entity Relationships

| Relationship | Cardinality |
|---|---|
| User to Employee | One-to-One |
| Employee to Requirement/Offer | One-to-Many |
| Requirement/Offer to Proposal | One-to-Many (cascade ALL) |
| Proposal to Employee | Many-to-One |
| Proposal to Resource | Many-to-One |

---

## REST API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/users/register` | Register a new user |
| `POST` | `/api/users/login` | Authenticate user |
| `POST` | `/api/users/logout` | Invalidate session |
| `POST` | `/api/employees` | Create employee profile |
| `PUT` | `/api/employees` | Update employee profile |
| `GET` | `/api/employees/{empId}` | Fetch employee by ID |
| `GET` | `/api/employees/by-user?userId=` | Fetch employee linked to a user account |
| `GET` | `/api/employees/{empId}/requirements` | All requirements by employee |
| `GET` | `/api/employees/{empId}/offers` | All offers by employee |
| `PATCH` | `/api/employees/requirements/fulfilled` | Mark requirement as fulfilled |
| `PATCH` | `/api/employees/offers/availability` | Toggle offer availability |
| `PATCH` | `/api/employees/proposals/accepted` | Accept a proposal |
| `GET` | `/api/requirements` | List requirements (filter: category, type) |
| `POST` | `/api/requirements` | Post new requirement |
| `PUT` | `/api/requirements` | Update requirement |
| `GET` | `/api/requirements/{id}` | Fetch requirement by ID |
| `DELETE` | `/api/requirements/{id}` | Delete requirement |
| `GET` | `/api/offers` | List offers (filter: category, type) |
| `POST` | `/api/offers` | Post new offer |
| `PUT` | `/api/offers` | Update offer |
| `GET` | `/api/offers/{id}` | Fetch offer by ID |
| `DELETE` | `/api/offers/{id}` | Delete offer |
| `GET` | `/api/proposals` | List all proposals |
| `POST` | `/api/proposals` | Submit a proposal |
| `PUT` | `/api/proposals` | Update proposal |
| `DELETE` | `/api/proposals/{id}` | Delete proposal |
| `GET` | `/api/resources` | Combined list of requirements + offers (filter: category, type) |

---

## Individual User Stories

> Drawn from the project tracker spreadsheet (JAVAFullStack-Project Advance_Tracker)

---

### EMP-001 — Employee Registration
**As an employee,** I want to register with my company email and a password so that I can create a secure account to access the marketplace.

**Acceptance criteria:**
- Registration requires a unique `userId` (email format) and a password
- A linked `Employee` profile (name, department, location) is created in step 2
- Duplicate `userId` returns a clear error message
- On success, the user is automatically logged in and redirected to the marketplace

**Owner:** Srinidhi Ra | **Est:** 4 hrs | **Sub-task:** Create `User` entity + `POST /api/users/register`

---

### EMP-002 — Employee Login
**As an employee,** I want to log in with my credentials so that I can access the marketplace and my personal dashboard.

**Acceptance criteria:**
- Valid credentials return the user session and the linked employee profile
- Invalid credentials return a descriptive error
- Session persisted in `sessionStorage` for the browser tab lifetime
- All marketplace routes redirect to `/login` when no session exists

**Owner:** Srinidhi Ra | **Est:** 4 hrs | **Sub-task:** `POST /api/users/login` + React `AuthContext` + `ProtectedRoute`

---

### REQ-001 — Add Requirement
**As an employee,** I want to post a requirement (what I need) so that colleagues who can help will find it in the marketplace.

**Acceptance criteria:**
- Form fields: title (required), description, category (required), type, budget (₹)
- Categories: Accommodation, Electronics, Vehicle, Services, Other
- Types: SELL, RENT, FREE, HELP
- Requirement appears in the marketplace immediately after posting
- Only logged-in users can post

**Owner:** Srinidhi Ra | **Est:** 5 hrs | **Sub-task:** `Requirement` entity + `POST /api/requirements`

---

### REQ-002 — Update Requirement
**As an employee,** I want to edit my posted requirement so that I can correct or update the details as my needs change.

**Acceptance criteria:**
- Only the owner can edit their own requirement
- All fields pre-populated with existing values in the edit form
- Changes reflected immediately in the marketplace

**Owner:** Mehul Ag | **Est:** 4 hrs | **Sub-task:** `PUT /api/requirements` + React edit form

---

### REQ-003 — Delete Requirement / Mark Fulfilled
**As an employee,** I want to delete a requirement or mark it as fulfilled so that the marketplace stays clean and relevant.

**Acceptance criteria:**
- Delete permanently removes the requirement (cascade deletes linked proposals)
- Mark-fulfilled sets `fulfilled = true` and records `fulfilledOn` date
- Fulfilled requirements show a "Fulfilled" badge
- Only the owner can perform these actions

**Owner:** Srinidhi Ra | **Est:** 3 hrs | **Sub-task:** `DELETE /api/requirements/{id}` + `PATCH /employees/requirements/fulfilled`

---

### OFF-001 — Add Offer
**As an employee,** I want to post an offer (what I can provide) so that interested colleagues can propose to buy, rent, or receive it.

**Acceptance criteria:**
- Form fields: title (required), description, category (required), type, price (₹), available until (date)
- Offer appears in the marketplace immediately
- Only logged-in users can post

**Owner:** Srinidhi Ra (backend), Shea (frontend) | **Est:** 6 hrs | **Sub-task:** `POST /api/offers`

---

### OFF-002 — Update Offer
**As an employee,** I want to update my offer so that I can change the price, description, or availability date.

**Acceptance criteria:**
- All existing fields pre-filled in the edit form
- Only the owner can edit
- Changes reflected immediately

**Owner:** Aditya Sho | **Est:** 4 hrs | **Sub-task:** `PUT /api/offers`

---

### OFF-003 — Delete Offer / Mark Unavailable
**As an employee,** I want to delete my offer or mark it as unavailable when the item is no longer for offer.

**Acceptance criteria:**
- Delete removes the offer and cascade-deletes its proposals
- Mark-unavailable sets `available = false` — offer stays visible with "Unavailable" badge
- Only the owner can perform these actions

**Owner:** Aditya Sho | **Est:** 4 hrs | **Sub-task:** `DELETE /api/offers/{id}` + `PATCH /employees/offers/availability`

---

### PRO-001 — Add Proposal
**As an employee,** I want to submit a proposal against a requirement or offer so that I can express interest or negotiate terms with the poster.

**Acceptance criteria:**
- Proposal form includes free-text description and optional amount (₹)
- Logged-in users who do not own the listing can submit a proposal
- Owner sees all received proposals on the detail page
- Non-owner sees the submit form on the same detail page

**Owner:** Shea (backend), Aditya Sho (frontend) | **Est:** 6 hrs | **Sub-task:** `POST /api/proposals`

---

### PRO-002 — Accept Proposal
**As the listing owner,** I want to accept a proposal so that both parties know the deal is agreed.

**Acceptance criteria:**
- Only the listing owner can accept proposals
- Accepting sets `accepted = true` and records `acceptedOn` date
- Accepted proposals are visually distinguished in the dashboard

**Owner:** Aditya Sho | **Est:** 4 hrs | **Sub-task:** `PATCH /employees/proposals/accepted`

---

### PRO-003 — Delete Proposal
**As an employee,** I want to delete a proposal I submitted so that I can withdraw my interest.

**Acceptance criteria:**
- Only the proposal submitter can delete their own proposal
- Confirmed with clear success/error feedback in the UI

**Owner:** Aditya Sho | **Est:** 3 hrs | **Sub-task:** `DELETE /api/proposals/{id}`

---

### BE-001 — Repository Layer
**As a developer,** I want a clean repository interface for each entity so that data access is fully decoupled from business logic.

**Owner:** Mehul Ag | **Est:** 8 hrs | **Sub-task:** JPA `@Repository` interfaces — User, Employee, Requirement, Offer, Proposal, Resource

---

### BE-002 — Service Layer
**As a developer,** I want service interfaces with full business logic so that controllers remain thin and independently testable.

**Owner:** Mehul Ag | **Est:** 10 hrs | **Sub-task:** Service implementations + Mockito unit tests for all modules

---

### BE-003 — REST APIs
**As a developer,** I want REST controllers with proper HTTP semantics so that any frontend or third-party client can integrate cleanly.

**Owner:** Mehul Ag | **Est:** 8 hrs | **Sub-task:** All controllers + Swagger UI + CORS configuration

---

### TEST-001 — End-to-End Integration & Coverage
**As a developer,** I want integration tests covering all flows (register → post → propose → accept) and 80% line coverage enforced by JaCoCo so that regressions are caught automatically before every merge.

**Owner:** Mehul Ag | **Est:** 12 hrs | **Sub-task:** MockMvc E2E tests + JaCoCo gate

---

## Challenges Faced & How We Solved Them

---

### Challenge 1 — Jackson Infinite Recursion (500 on All GET Endpoints with Proposals)

**Problem:** `Requirement.proposals` → `Proposal.resource` → `Requirement.proposals` created an infinite JSON serialisation loop. Every GET endpoint that returned a resource with attached proposals threw a `StackOverflowError`.

**Root cause:** Bidirectional JPA relationships serialised without any cycle guard.

**Solution:**
- Applied `@JsonIgnoreProperties({"proposals"})` on the `resource` field of `Proposal`
- Applied `@JsonIgnoreProperties({"resource"})` on the `proposals` field of `Requirement` and `Offer`
- Added Lombok `@EqualsAndHashCode(exclude = ...)` and `@ToString(exclude = ...)` to break reference cycles at the object level as well

**Lesson:** Always guard bidirectional JPA relationships before writing any GET endpoint. Test with Postman as soon as relationships are wired up.

---

### Challenge 2 — Deserialising Abstract `Resource` in Proposal POST

**Problem:** `POST /api/proposals` body sent `{"resource": {"resId": 5}}`. Jackson could not instantiate the abstract `Resource` class and threw `InvalidDefinitionException`.

**Solution:**
- Added `@JsonDeserialize(as = Requirement.class)` as a temporary polymorphism hint on the `Resource` field
- Updated the service layer to **re-fetch** the managed `Resource` entity from the repository using the provided `resId`, discarding the partially-deserialised shell entirely
- This ensures the correct concrete subtype (Requirement or Offer) is always persisted as the foreign key

**Lesson:** Never trust the client to send full polymorphic payloads for owned relationships — always re-fetch the managed entity from the DB using the provided ID.

---

### Challenge 3 — Cascade Persist Error on Employee Creation

**Problem:** `POST /api/employees` sent the existing `User` object inside the request body. Hibernate tried to `INSERT` a `User` row that already existed, causing a `DataIntegrityViolationException`.

**Solution:**
- Updated the service implementation to call `userRepository.getReferenceById(userId)` instead of persisting the `User` from the request body
- This creates a Hibernate lazy proxy for the existing user row without triggering a new `INSERT`

**Lesson:** Use `getReferenceById()` (lazy proxy) when you only need a foreign key reference — never `save()` an entity you did not create in the same transaction.

---

### Challenge 4 — Boolean Naming Mismatch (Frontend Always Showed Wrong Status)

**Problem:** Java's Bean convention strips the `is` prefix from boolean getters — Lombok/Jackson serialised `isFulfilled` as `fulfilled` in the JSON wire format. The Angular and early React models used `isFulfilled`, so statuses always appeared wrong: offers showed as unavailable even when they were available; fulfilled requirements showed as open.

**Solution:**
- Audited every boolean field in the React service layer and component bindings
- Renamed all model references: `isFulfilled` → `fulfilled`, `isAvailable` → `available`, `isAccepted` → `accepted`
- Added a temporary `console.log` on raw API responses during debugging to verify field names before touching the UI

**Lesson:** Always inspect the raw JSON wire format in Postman or browser DevTools — do not assume Java field names match what Jackson serialises.

---

### Challenge 5 — Category / Type Filter Silently Ignored

**Problem:** Passing `?category=Electronics` to `GET /api/requirements` returned all requirements — the query parameter was accepted at the controller level but never forwarded to the repository. The marketplace filter bar had no visible effect.

**Solution:**
- Added `findByCategory`, `findByType`, and `findByCategoryAndType` derived query methods to the JPA repository interfaces
- Service implementations now branch: no params → `findAll()`, one param → single-field filter, both → combined filter

**Lesson:** Test every query-parameter combination in Postman or Swagger before marking any filter feature complete.

---

### Challenge 6 — Authentication Not Enforced on Marketplace Routes

**Problem:** The original routing sent unauthenticated users directly to `/marketplace`, violating the requirement that the platform is private to company employees only.

**Solution:**
- Created a `ProtectedRoute` higher-order component that checks `AuthContext.state.isLoggedIn`
- Wrapped every marketplace-related route (`/marketplace`, `/requirements/**`, `/offers/**`, `/dashboard`) in `ProtectedRoute`
- Changed the root redirect from `/marketplace` to `/login`
- Session stored in `sessionStorage` (tab-scoped — auto-clears on browser close)

**Lesson:** Design authentication guards at the router level from day one — retrofitting them later requires touching every route definition.

---

### Challenge 7 — CORS Configuration for Multiple Frontends

**Problem:** The backend needed to serve both the Angular frontend (port 4200) and the React frontend (port 5173). An overly permissive CORS config was a security risk; an overly strict one broke development.

**Solution:**
- Configured Spring's `CorsRegistry` to explicitly allowlist `http://localhost:4200` and `http://localhost:5173`
- Used Vite's `server.proxy` to forward `/api/*` to `http://localhost:8080` in the React dev environment, eliminating browser CORS preflight requests entirely for local development

---

### Challenge 8 — Migrating from Angular to React Mid-Project

**Problem:** The team started with Angular 16 as specified in the capstone plan. Midway, the decision was made to build a second polished React frontend for the final submission. Both frontends needed to work against the identical backend.

**Solution:**
- The Spring Boot REST API was left completely unchanged — it was frontend-agnostic by design
- The React project was bootstrapped with Vite and mirrored the Angular component structure (Login, Marketplace, Dashboard, forms, detail pages)
- Both frontends coexist in the monorepo for comparison

**Lesson:** A clean, well-documented REST API makes swapping or adding frontend frameworks trivial. The contract is the URL schema + JSON response shape — not the framework.

---

## Things We Learned

### Technical Learnings

**1. JPA Inheritance Strategies**
`SINGLE_TABLE` inheritance kept the database simple (one `resources` table) with a `resource_type` discriminator column. We learned that this trades schema cleanliness (many nullable columns) for query simplicity (no JOINs). For larger hierarchies with many subtype-specific fields, `JOINED` inheritance would be the better choice.

**2. Spring Data JPA Derived Queries**
Writing `findByCategoryAndType(String category, String type)` in a repository interface and having Hibernate auto-generate the SQL was a significant productivity insight. We also learned when to prefer `@Query` annotations (complex joins, aggregates) over derived queries (simple field filters).

**3. Mockito for Isolated Service Testing**
Mocking the repository layer with `@Mock` + `@InjectMocks` allowed service classes to be tested completely independently of the database — fast, deterministic tests that run in milliseconds. The Red-Green-Refactor TDD cycle became natural by Sprint 2.

**4. React Context + useReducer for Auth State**
Using `useReducer` inside `AuthContext` (instead of plain `useState`) made state transitions explicit — `LOGIN`, `LOGOUT`, `UPDATE_EMPLOYEE` action types act as a mini-Redux. Combining with `sessionStorage` gave free session persistence across page refreshes without any library overhead.

**5. Vite Proxy for Local API Calls**
Configuring `server.proxy` in `vite.config.js` to forward `/api/*` to `http://localhost:8080` eliminated browser CORS preflight checks in development. Every team member could run the frontend without touching backend CORS settings.

**6. Polymorphic JSON Serialisation**
`@JsonIgnoreProperties` is simpler than `@JsonManagedReference` / `@JsonBackReference` and works symmetrically from both sides of a bidirectional relationship. Always break serialisation cycles at the boundary, not deep inside the object graph.

**7. CSS Design Tokens and Design Systems**
Building a full CSS custom-property design system from scratch (neutral scale, semantic aliases, spacing scale, component primitives) taught us the architecture underlying professional systems like Tailwind and Radix. Using named tokens (`--indigo-500`, `--text-secondary`) instead of hardcoded hex values makes theme changes a one-line edit.

**8. Protected Routes and Auth UX Patterns**
The `ProtectedRoute` HOC with `Navigate to="/login" state={{ from: location }}` — redirecting back to the original URL after successful login — is the standard SPA authentication pattern used in production. Understanding it end-to-end was a key frontend milestone for the team.

---

### Soft-Skill and Process Learnings

**1. Incremental delivery beats big-bang integration**
Delivering Sprint 1 (data layer) before touching services, and services before touching controllers, meant every bug was isolated to the newest layer. Integration steps were small and fast to debug.

**2. Swagger UI saves hours of integration time**
Having Swagger UI live at `/swagger-ui.html` let the frontend team test every endpoint independently before writing Axios code. This eliminated an entire class of "is it the frontend or the backend?" debugging sessions.

**3. Write tests before they are asked for**
The JaCoCo 80% coverage gate forced us to write tests during development — not as a cleanup task at the end. Tests caught the cascade-persist bug and the boolean-naming mismatch before they reached integration testing.

**4. Document decisions as you make them**
`RUN_AND_TEST.md` accumulated a "Fixes applied" section that became invaluable when returning to a feature after a break or onboarding a teammate. A short bullet explaining why a fix was made saves hours of re-debugging.

**5. The framework is secondary to the API contract**
Migrating from Angular to React with zero backend changes proved that a well-designed REST API is the most valuable and durable artefact of the project. The URL schema + JSON response structure is the real product contract — the UI is just a consumer of it.

---

## Running the Project

### Prerequisites
- Java 17+
- Maven 3.9+
- Node.js 20+ and npm 10+
- Docker and Docker Compose

### 1. Start the database

```bash
docker-compose up -d
```

Starts MySQL 8 on port `3306`. Hibernate auto-creates the schema on first boot.

### 2. Start the backend

```bash
cd marketplace-backend
mvn spring-boot:run
```

- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

### 3. Start the React frontend

```bash
cd marketplace-react
npm install
npm run dev
```

App available at `http://localhost:5173`

### 4. (Optional) Start the Angular frontend

```bash
cd marketplace-frontend
npm install
ng serve
```

App available at `http://localhost:4200`

### 5. Run backend tests

```bash
cd marketplace-backend
mvn test          # unit + integration tests
mvn verify        # + JaCoCo 80% line-coverage gate
```

Coverage report: `marketplace-backend/target/site/jacoco/index.html`

---

## Team

| Name | Role | Key Contributions |
|---|---|---|
| **Mehul Agarwal** | Full-Stack / Lead | Backend architecture, all service + repository layers, REST controllers, JaCoCo coverage, React migration, UI design system |
| **Aditya Sharma** | Backend + Frontend | Offer CRUD, Proposal CRUD and acceptance flow, Angular integration, GitHub repository management |
| **Srinidhi Ra** | Backend | User authentication, Employee profile, Requirement CRUD, repository layer |
| **Shea** | Backend + Frontend | Proposal submission backend, Angular UI screens, test assistance |

---

> *Capstone project for the NaviKenz JavaFullStack training program, 2026.*  
> *Repository: [https://github.com/Aditya-amrahs/InHouseMkt](https://github.com/Aditya-amrahs/InHouseMkt)*
