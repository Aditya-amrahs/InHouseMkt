# Run & Test Guide

## Backend (Spring Boot, Java 17, Maven)

```bash
cd marketplace-backend
mvn test          # compiles + runs all unit, repository, and E2E tests
mvn verify        # additionally enforces the JaCoCo 80% line-coverage gate
mvn spring-boot:run   # starts API on http://localhost:8080 (Swagger: /swagger-ui.html)
```

Coverage report after `mvn test`: `target/site/jacoco/index.html`

## Frontend (React 19 + Vite)

```bash
cd marketplace-react
npm install
npm run dev       # http://localhost:5173 (backend must be running on :8080)
npm run build     # production build check
```

## Test suite contents (per TestingPlan.md)

| Layer | Classes |
|---|---|
| Service (Mockito) | User, Employee (13 tests incl. updateIsFulfilled/Available/Accepted), Requirement, Offer, Proposal, **Resource (new)** |
| Repository (H2 @DataJpaTest) | Requirement, Offer, Proposal, **Resource (new)**, **User (new)**, **Employee (new)** |
| E2E (MockMvc + H2) | RegistersAndLogsIn, RequirementToProposalToAcceptance, **OfferToProposalToAcceptance (new)**, DeleteCascadesCorrectly, **UIToAPIIntegration (CORS contract, new)** |

## Fixes applied (2026-07-10)

**Backend**
1. `Resource.java` — missing `lombok.experimental.SuperBuilder` import (hard compile error; nothing built before this fix).
2. Jackson infinite recursion between `Proposal.resource` and `Requirement/Offer.proposals` — broken with `@JsonIgnoreProperties` both ways + Lombok `toString/equals` excludes (GET endpoints would have 500'd once proposals existed).
3. `POST /api/proposals` couldn't deserialize the abstract `Resource` from `{"resId": n}` — `@JsonDeserialize(as = Requirement.class)` placeholder + service now re-fetches the managed entity.
4. `POST /api/employees` with an already-registered User crashed (cascade persist on existing row) — service re-attaches the managed User.
5. Category-only / type-only filters were silently ignored on `/api/requirements`, `/api/offers`, `/api/resources` — now supported (`findByCategory` / `findByType`).
6. New endpoint `GET /api/employees/by-user?userId=` for login profile resolution.
7. E2E test asserted wrong JSON property `$.isAccepted` (Lombok/Jackson emit `accepted`).

**Frontend**
8. Model booleans renamed `isFulfilled/isAvailable/isAccepted` → `fulfilled/available/accepted` to match the backend wire format (statuses always showed wrong; offers could never receive proposals).
9. Login now resolves the real Employee (with `empId`) via the by-user endpoint, with auto-create fallback — dashboard and requirement/offer creation work for logged-in users.

## Known notes
- `mvn verify` enforces 80% bundle line coverage (JaCoCo). Controllers are covered mainly by E2E tests; if the gate fails, review `target/site/jacoco` and add `@WebMvcTest` slices, or lower the threshold in `pom.xml`.
- The React client is the supported presentation layer.
- Passwords are stored/serialized in plain text (`User` entity) — acceptable for a capstone, flag for production.
