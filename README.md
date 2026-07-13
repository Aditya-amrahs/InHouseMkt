# Employee In-House Marketplace

## Overview

The Employee In-House Marketplace is an internal application that allows employees
within an organization to seek or offer help, products, or services to one another.
Common use cases include finding a paying-guest (PG) accommodation, selling a phone
or bike, or requesting assistance from colleagues. The application matches employees
who **require** something with employees who can **offer** something, using a
proposal-based negotiation workflow.

**Target Audience:** All registered employees of the organization.

## Core Features

- 🔐 **Secure Registration & Login** — employees create an account and authenticate
  before accessing the marketplace.
- 📋 **Requirement Management** — post a requirement, update it once fulfilled, or
  delete it when no longer needed.
- 📦 **Offer Management** — post an offer (product/service), mark it unavailable, or
  delete it.
- 🤝 **Proposal Management** — submit a proposal against a requirement or offer,
  accept a proposal, or delete one.
- 👥 **Employee Profiles** — track employee details (name, department, location) tied
  to every offer, requirement, and resource.

## Roadmap

| Phase | Sprint | Focus | Key Deliverable |
|---|---|---|---|
| 1 | Sprint 1 | Core Java + JPA with Hibernate | Entity model & Repository (data) layer |
| 2 | Sprint 2 | Spring Boot + REST Controllers | Service layer & REST APIs |
| 3 | Sprint 3 | Angular UI | Frontend components for Login/Requirement/Offer/Proposal |
| 4 | Sprint 4 | Integration & Testing | End-to-end integration, JUnit test coverage |


Sprint 1 ──▶ Sprint 2 ──▶ Sprint 3 ──▶ Sprint 4
(Data)      (API)         (UI)        (E2E + QA)

## Development Methodology

The team follows a **Test-Driven Development (TDD)** approach: write failing test
cases first (Red), implement the minimum code to pass (Green), then refactor —
applied incrementally, module by module.

## Modules

1. Login Module
2. Employee Module
3. Requirement Module
4. Offer Module
5. Proposal Module
