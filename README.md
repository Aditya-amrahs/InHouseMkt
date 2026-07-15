# InHouse Marketplace

An internal employee-to-employee marketplace for posting requirements, publishing offers, and exchanging proposals within the organisation.

## What It Contains

- Employee registration and session-based login/logout
- Requirement and offer CRUD with status tracking
- Combined marketplace browsing with category/type filters
- Proposal submission, withdrawal, and owner acceptance
- React single-page UI backed by a Spring Boot REST API
- H2 in-memory persistence for local development and tests

## Project Structure

```text
CAPSTONE/
├── marketplace-backend/   Spring Boot 3, Java 17, JPA, REST API
├── marketplace-react/     React 19 + Vite client
├── Architecture.md        Layer and entity design
├── Documentation.md       Detailed project documentation
├── TestingPlan.md         Test matrix and coverage goals
└── docker-compose.yaml    Container configuration reference
```

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+ and npm 10+

## Run Locally

```bash
cd marketplace-backend
mvn spring-boot:run
```

In a second terminal:

```bash
cd marketplace-react
npm install
npm run dev
```

Open `http://localhost:5173`. The API runs at `http://localhost:8080/api` and Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

## Test and Build

```bash
cd marketplace-backend
mvn test
mvn verify

cd ../marketplace-react
npm run lint
npm run build
```

H2 is the default local/test database. Persistent MySQL or Neo4j integration is intentionally deferred until the application contract and end-to-end behavior are stable.

## API Areas

| Area | Base path | Purpose |
|---|---|---|
| Users | `/api/users` | Registration, login, logout |
| Employees | `/api/employees` | Profiles and status actions |
| Requirements | `/api/requirements` | Requirement lifecycle |
| Offers | `/api/offers` | Offer lifecycle |
| Proposals | `/api/proposals` | Proposal lifecycle |
| Resources | `/api/resources` | Combined marketplace feed |

## Team

- Srinidhi Rao
- Aditya Sharma
- Mehul Agarwal

See [Documentation.md](Documentation.md) for architecture, domain model, user-story mapping, and detailed decisions.
