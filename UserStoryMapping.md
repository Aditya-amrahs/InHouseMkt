# User Story → Code Architecture Mapping

**Project:** InHouseMkt (Spring Boot backend `marketplace-backend` + React frontend `marketplace-react`)
**Purpose:** Maps each capstone User Story to its responsible code location, explains the structural role of those files, and the architectural reasoning behind the placement.

---

## 1. Employee Registration
**CODE LOCATION:** `controller/UserController.java` / `controller/EmployeeController.java`; `entity/Employee.java`, `entity/User.java`; `repository/IEmployeeRepository.java` / `IUserRepository.java`; `service/IEmployeeService.java` + `impl/EmployeeServiceImpl.java`; Frontend: `src/components/Register/Register.jsx` + `Register.module.css`

**STRUCTURAL "WHAT":** Register.jsx is the UI form component collecting registration data; UserController/EmployeeController exposes the REST endpoint; Employee/User entities model the persisted data; the repository/service pair handles persistence and business rules.

**ARCHITECTURAL "WHY":** Registration cuts across the full stack, so each layer only owns its concern — UI collects input, controller handles HTTP, service enforces rules (e.g., duplicate checks via `DuplicateResourceException`), repository persists. This is classic layered/N-tier separation.

---

## 2. Employee Login
**CODE LOCATION:** `controller/UserController.java`; `context/AuthContext.jsx`; `src/components/Login/Login.jsx` + `.module.css`; `exception/InvalidCredentialsException.java`; `components/shared/ProtectedRoute.jsx`

**STRUCTURAL "WHAT":** Login.jsx is the UI form; AuthContext.jsx manages client-side auth state/session; UserController handles the login REST call; InvalidCredentialsException signals auth failures; ProtectedRoute gatekeeps authenticated routes.

**ARCHITECTURAL "WHY":** Auth state is centralized in a React Context so any component can read login status without prop-drilling; ProtectedRoute implements route-guarding as a cross-cutting concern, keeping individual page components unaware of auth logic.

---

## 3–5. Add / Update / Delete Requirement
**CODE LOCATION:** `controller/RequirementController.java`; `entity/Requirement.java`; `repository/IRequirementRepository.java`; `service/IRequirementService.java` + `impl/RequirementServiceImpl.java`; Frontend: `components/RequirementForm/RequirementForm.jsx`, `components/RequirementDetail/RequirementDetail.jsx`

**STRUCTURAL "WHAT":** RequirementForm.jsx handles create/update input; RequirementDetail.jsx displays/deletes a single requirement; the controller exposes CRUD endpoints; service/repository implement business logic and persistence.

**ARCHITECTURAL "WHY":** One form component reused for both add/update (common React pattern) avoids duplicate UI code; CRUD operations for a single entity are grouped into one controller/service/repository trio, keeping the codebase organized by domain resource rather than by verb.

---

## 6–8. Add / Update / Delete Offer
**CODE LOCATION:** `controller/OfferController.java`; `entity/Offer.java`; `repository/IOfferRepository.java`; `service/IOfferService.java` + `impl/OfferServiceImpl.java`; Frontend: `components/OfferForm/OfferForm.jsx`, `components/OfferDetail/OfferDetail.jsx`

**STRUCTURAL "WHAT":** Same pattern as Requirement — form for create/update, detail view for read/delete, controller/service/repository trio for backend logic.

**ARCHITECTURAL "WHY":** Mirrors the Requirement structure exactly, showing a consistent, predictable convention across domain entities — easing maintainability and onboarding.

---

## 9–11. Add / Accept / Delete Proposal
**CODE LOCATION:** `controller/ProposalController.java`; `entity/Proposal.java`; `repository/IProposalRepository.java`; `service/IProposalService.java` + `impl/ProposalServiceImpl.java`; `exception/ForbiddenOperationException.java`; Frontend: likely embedded within `OfferDetail.jsx` (no dedicated Proposal component found)

**STRUCTURAL "WHAT":** Backend trio manages proposal lifecycle; ForbiddenOperationException likely guards "Accept Proposal" (e.g., only offer owner can accept); frontend proposal actions appear embedded in OfferDetail rather than a standalone component.

**ARCHITECTURAL "WHY":** Reusing ForbiddenOperationException centralizes authorization-failure handling instead of duplicating permission checks per-action; embedding proposal actions in OfferDetail suggests proposals are treated as a sub-resource of Offer in the UI, matching their relationship in the domain model.

---

## 12. Repository Layer
**CODE LOCATION:** `repository/IEmployeeRepository.java`, `IOfferRepository.java`, `IProposalRepository.java`, `IRequirementRepository.java`, `IResourceRepository.java`, `IUserRepository.java`

**STRUCTURAL "WHAT":** Data-access interfaces (Spring Data JPA style), one per entity, abstracting database operations.

**ARCHITECTURAL "WHY":** The Repository pattern isolates persistence/database concerns from business logic (Service layer), so the data-access technology (e.g., JPA/Hibernate) can change without touching services or controllers — a core tenet of layered architecture and testability (mockable interfaces).

---

## 13. Service Layer
**CODE LOCATION:** `service/I*Service.java` (interfaces) + `service/impl/*ServiceImpl.java` (implementations)

**STRUCTURAL "WHAT":** Business logic layer sitting between controllers and repositories, implementing interfaces for each domain entity.

**ARCHITECTURAL "WHY":** Interface + impl split (`IEmployeeService` / `EmployeeServiceImpl`) enables dependency inversion — controllers depend on abstractions, not concrete classes, supporting mocking in unit tests and easier swapping of implementations.

---

## 14. REST APIs
**CODE LOCATION:** `controller/EmployeeController.java`, `OfferController.java`, `ProposalController.java`, `RequirementController.java`, `ResourceController.java`, `UserController.java`; `exception/GlobalExceptionHandler.java`; `config/CorsConfig.java`

**STRUCTURAL "WHAT":** Controllers expose HTTP endpoints per resource; GlobalExceptionHandler centralizes error-to-HTTP-status translation; CorsConfig configures cross-origin access for the frontend.

**ARCHITECTURAL "WHY":** One controller per resource follows REST conventions (resource-oriented routing); a single global exception handler avoids repetitive try/catch in every controller, keeping error handling consistent (e.g., mapping `ResourceNotFoundException` → 404 uniformly).

---

## 15. React UI *(updated from "Angular UI")*
**CODE LOCATION:** `marketplace-react/src/components/**`, `src/App.jsx`, `src/main.jsx`, `src/services/api.js`

**STRUCTURAL "WHAT":** Component-based UI layer built with React + Vite; `api.js` centralizes HTTP calls to the backend; `App.jsx`/`main.jsx` bootstrap routing and app entry.

**ARCHITECTURAL "WHY":** Component folders organized by feature (Login, Register, Dashboard, Marketplace, OfferForm, etc.) rather than by file type, keeping related JSX/CSS colocated; a single `api.js` service module centralizes all API calls so components don't duplicate fetch logic.

> Note: original User Story list said "Angular UI" — confirmed by user to be a naming correction; actual implementation is React.

---

## 16. Unit Testing
**CODE LOCATION:** `src/test/java/com/inhouse/marketplace/service/*ServiceTest.java`, `src/test/java/.../repository/*RepositoryTest.java`

**STRUCTURAL "WHAT":** Per-entity/per-layer test classes mirroring the main source structure (Service tests, Repository tests).

**ARCHITECTURAL "WHY":** Maven's standard convention (`src/test/java` mirroring `src/main/java` package structure) lets build tools auto-discover tests and keeps test scope aligned 1:1 with the layer/class under test — isolating service logic tests from data-access tests.

---

## 17. End-to-End Integration
**CODE LOCATION:** `src/test/java/com/inhouse/marketplace/e2e/MarketplaceE2ETest.java`; `docker-compose.yaml`; `application-test.properties`

**STRUCTURAL "WHAT":** A dedicated E2E test class separate from unit tests; docker-compose likely spins up dependent services (DB, backend, frontend) for full-stack integration testing.

**ARCHITECTURAL "WHY":** Separating E2E tests into their own package (`e2e/`) signals a different test scope/lifecycle (slower, full-stack, possibly using Testcontainers or a real DB via `application-test.properties`) from fast, isolated unit tests — a common practice to keep CI fast while still validating full request flows.
