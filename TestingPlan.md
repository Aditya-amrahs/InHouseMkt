**Guidelines:**
- One test class per Service/Repository interface (e.g., `EmployeeServiceTest`).
- Use mocking (e.g., Mockito) for the Service layer so it can be tested independently
  of the Repository/DB layer.
- Use an in-memory database (e.g., H2) for Repository-layer integration tests.
- Target minimum 80% code coverage per the acceptance criteria in `TEST-001`.
- Name tests using the pattern:
  `test<Feature>_<Scenario>_<ExpectedResult>`.

## 2. Test Matrix by Module

### Login Module (`IUserService` / `IUserRepository`)

| Test Case | Scenario |
|---|---|
| `testUserRegistration_ValidData_ReturnsSuccess` | New user registers with valid userId/password |
| `testUserRegistration_DuplicateUserId_ThrowsException` | Registration fails on duplicate userId |
| `testLogin_ValidCredentials_ReturnsUser` | Login succeeds with correct credentials |
| `testLogin_InvalidCredentials_ThrowsException` | Login fails with wrong password |
| `testLogout_ValidUser_ReturnsSuccess` | Logout completes without error |
| `testEditUser_ValidData_UpdatesUser` | User details update successfully |
| `testRemoveUser_ExistingUserId_DeletesUser` | User is removed by userId |

### Employee Module (`IEmployeeService` / `IEmployeeRepository`)

| Test Case | Scenario |
|---|---|
| `testAddEmployee_ValidData_ReturnsSavedEmployee` | Employee is created and linked to a User (1:1) |
| `testEditEmployee_ValidData_UpdatesEmployee` | Employee details (dept, location) update correctly |
| `testGetEmployee_ExistingId_ReturnsEmployee` | Fetch employee by empId |
| `testGetAllOffers_ByEmpId_ReturnsOfferList` | All offers for an employee returned |
| `testGetAllRequirements_ByEmpId_ReturnsRequirementList` | All requirements for an employee returned |

### Requirement Module (`IRequirementService` / `IRequirementRepository`)

| Test Case | Scenario |
|---|---|
| `testAddRequirement_ValidData_ReturnsSavedRequirement` | Requirement saved and visible in marketplace |
| `testRequirementUpdate_MarkAsFulfilled` | `isFulfilled` set to true and `fulfilledOn` populated |
| `testDeleteRequirement_ExistingId_RemovesRequirement` | Requirement removed by reqId |
| `testGetAllRequirements_FilterByCategoryAndType_ReturnsFilteredList` | Filtered search returns correct subset |
| `testGetRequirement_NonExistingId_ReturnsNull` | Fetch on invalid ID returns null/empty |

### Offer Module (`IOfferService` / `IOfferRepository`)

| Test Case | Scenario |
|---|---|
| `testAddOffer_ValidData_ReturnsSavedOffer` | Offer saved successfully |
| `testOfferUpdate_MarkAsUnavailable` | `isAvailable` set to false |
| `testDeleteOffer_ExistingId_RemovesOffer` | Offer removed by offerId |
| `testGetAllOffers_FilterByCategoryAndType_ReturnsFilteredList` | Filtered search returns correct subset |
| `testGetOffer_NonExistingId_ReturnsNull` | Fetch on invalid ID returns null/empty |

### Proposal Module (`IProposalService` / `IProposalRepository`)

| Test Case | Scenario |
|---|---|
| `testAddProposal_ValidData_LinksToResource` | Proposal correctly linked to a Requirement or Offer |
| `testProposalAcceptance_MarkAsAccepted` | `isAccepted` set to true and `acceptedOn` populated |
| `testDeleteProposal_ExistingId_RemovesProposal` | Proposal removed by propId |
| `testGetAllProposals_ReturnsFullList` | All proposals retrieved |
| `testAddProposal_InvalidResourceReference_ThrowsException` | Proposal rejected if resource does not exist |

### Resource Module (`IResourceService` / `IResourceRepository`)

| Test Case | Scenario |
|---|---|
| `testGetAllResources_FilterByCategoryAndType_ReturnsFilteredList` | Returns combined Requirement + Offer resources matching filter |
| `testGetAllResources_ByEmpId_ReturnsEmployeeResources` | Returns resources owned by a specific employee |

## 3. Integration & End-to-End Tests (Sprint 4)

| Test Case | Scenario |
|---|---|
| `testE2E_EmployeeRegistersAndLogsIn` | Full registration → login flow via REST API |
| `testE2E_RequirementToProposalToAcceptance` | Employee posts requirement → another proposes → original accepts |
| `testE2E_OfferToProposalToAcceptance` | Employee posts offer → another proposes → original accepts |
| `testE2E_DeleteCascadesCorrectly` | Deleting a Requirement/Offer correctly handles linked Proposals |
| `testE2E_UIToAPIIntegration` | React UI actions correctly invoke and reflect REST API responses |

## 4. Coverage Goal

- **Minimum 80% line/branch coverage** across all Service and Repository
  implementations, per acceptance criteria for `TEST-001`.
- Coverage reports generated via JaCoCo (or equivalent) and reviewed each sprint.
