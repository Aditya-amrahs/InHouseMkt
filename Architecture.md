# Architecture \& Class Design Blueprint

## 1\. System Architecture (3-Tier)

┌─────────────────────┐
│   React UI           │  Sprint 3 — Presentation Layer
│ (Components/Services)│
└──────────┬───────────┘
│ REST/HTTP (JSON)
┌──────────▼───────────┐
│  Spring Boot          │  Sprint 2 — Application/Service Layer
│  REST Controllers      │
│  Service Interfaces    │
└──────────┬───────────┘
│ Java calls
┌──────────▼───────────┐
│  JPA + Hibernate       │  Sprint 1 — Data Access Layer
│  Repository Interfaces │
└──────────┬───────────┘
│ SQL
┌──────────▼───────────┐
│      Database          │
└───────────────────────┘

* **Presentation Layer (React):** UI components for Login, Requirement, Offer,
and Proposal screens; consumes REST APIs through the shared Axios service.
* **Application Layer (Spring Boot):** REST Controllers expose endpoints backed by
Service interfaces (`IProposalService`, `IUserService`, `IOfferService`,
`IResourceService`, `IEmployeeService`, `IRequirementService`) containing business logic.
* **Data Access Layer (JPA/Hibernate):** Repository interfaces
(`IProposalRepository`, `IUserRepository`, `IOfferRepository`,
`IResourceRepository`, `IEmployeeRepository`, `IRequirementRepository`) handle
persistence via Hibernate ORM.

## 2\. Entity Relationship Summary

* A `User` has a 1..1 relationship with `Employee` (each employee has one login).
* An `Employee` can raise 1..\* `Proposal`s and own 1..\* `Requirement`s.
* `Resource` is an abstract base class extended by `Requirement` and `Offer`
(inheritance relationship, shown via the hollow triangle in the diagram).
* A `Proposal` is linked to one `Resource` (`Requirement` or `Offer`) and one `Employee`.
* `Requirement` and `Offer` each hold a `List<Proposal>` of submitted proposals.

## 3\. Entity (POJO) Skeletons

### User

```java
@Entity
public class User {

    @Id
    private String userId;

    private String password;

    // getters / setters
}
```

### Employee

```java
@Entity
public class Employee {

    @Id
    private int empId;

    private String empName;
    private String deptName;
    private String location;

    @OneToOne
    @JoinColumn(name = "user\\\_id")
    private User user;

    // getters / setters
}
```

### Resource (Abstract Base Class)

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE\\\_TABLE)
@DiscriminatorColumn(name = "resource\\\_type")
public abstract class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int resId;

    private String title;
    private String description;
    private String category;
    private LocalDate date;
    private String type;
    private double price;

    @ManyToOne
    @JoinColumn(name = "emp\\\_id")
    private Employee emp;

    // getters / setters
}
```

### Requirement (extends Resource)

```java
@Entity
@DiscriminatorValue("REQUIREMENT")
public class Requirement extends Resource {

    @Id
    private int reqId;

    private boolean isFulfilled;
    private LocalDate fulfilledOn;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    // getters / setters
}
```

### Offer (extends Resource)

```java
@Entity
@DiscriminatorValue("OFFER")
public class Offer extends Resource {

    @Id
    private int offerId;

    private boolean isAvailable;
    private LocalDate availableUpto;

    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    // getters / setters
}
```

### Proposal

```java
@Entity
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int propId;

    private String proposal;
    private double amount;
    private LocalDate proposalDate;
    private boolean isAccepted;
    private LocalDate acceptedOn;

    @ManyToOne
    @JoinColumn(name = "emp\\\_id")
    private Employee emp;

    @ManyToOne
    @JoinColumn(name = "res\\\_id")
    private Resource resource;

    // getters / setters
}
```

## 4\. Service Layer Interfaces

```java
public interface IUserService {
    User login(User user);
    User logout(User user);
    User addUser(User user);
    User editUser(User user);
    User removeUser(String userId);
}

public interface IEmployeeService {
    Employee addEmployee(Employee emp);
    Employee editEmployee(Employee emp);
    Employee getEmployee(int empId);
    Offer updateIsAvailable(Offer offer);
    Requirement updateIsFulfilled(Requirement req);
    Proposal updateIsAccepted(Proposal prop);
    List<Offer> getAllOffers(int empId);
    List<Requirement> getAllRequirements(int empId);
}

public interface IRequirementService {
    Requirement addRequirement(Requirement req);
    Requirement editRequirement(Requirement req);
    Requirement getRequirement(int reqId);
    Requirement removeRequirement(int reqId);
    List<Requirement> getAllRequirements();
    List<Requirement> getAllRequirements(String category, String type);
}

public interface IOfferService {
    Offer addOffer(Offer offer);
    Offer editOffer(Offer offer);
    Offer getOffer(int offerId);
    Offer removeOffer(int offerId);
    List<Offer> getAllOffers();
    List<Offer> getAllOffers(String category, String type);
}

public interface IProposalService {
    Proposal addProposal(Proposal prop);
    Proposal editProposal(Proposal prop);
    Proposal getProposal(int propId);
    Proposal removeProposal(int propId);
    List<Proposal> getAllProposals();
}

public interface IResourceService {
    List<Resource> getAllResources(String category, String type);
    List<Resource> getAllResources(int empId);
}
```

## 5\. Repository Layer Interfaces

```java
public interface IUserRepository extends JpaRepository<User, String> {
    User login(User user);
    User logout(User user);
    User saveUser(User user);
    User updateUser(User user);
    User deleteUser(String userId);
}

public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {
    Employee saveEmployee(Employee emp);
    Employee updateEmployee(Employee emp);
    Employee fetchEmployee(int empId);
    Offer updateIsAvailable(Offer offer);
    Requirement updateIsFulfilled(Requirement req);
    Proposal updateIsAccepted(Proposal prop);
    List<Offer> fetchAllOffers(int empId);
    List<Requirement> fetchAllRequirements(int empId);
}

public interface IRequirementRepository extends JpaRepository<Requirement, Integer> {
    Requirement saveRequirement(Requirement req);
    Requirement updateRequirement(Requirement req);
    Requirement fetchRequirement(int reqId);
    Requirement deleteRequirement(int reqId);
    List<Requirement> fetchAllRequirements();
    List<Requirement> fetchAllRequirements(String category, String type);
}

public interface IOfferRepository extends JpaRepository<Offer, Integer> {
    Offer saveOffer(Offer offer);
    Offer updateOffer(Offer offer);
    Offer fetchOffer(int offerId);
    Offer deleteOffer(int offerId);
    List<Offer> fetchAllOffers();
    List<Offer> fetchAllOffers(String category, String type);
}

public interface IProposalRepository extends JpaRepository<Proposal, Integer> {
    Proposal saveProposal(Proposal prop);
    Proposal updateProposal(Proposal prop);
    Proposal fetchProposal(int propId);
    Proposal deleteProposal(int propId);
    List<Proposal> fetchAllProposals();
}

public interface IResourceRepository extends JpaRepository<Resource, Integer> {
    List<Resource> fetchAllResources(String category, String type);
    List<Resource> fetchAllResources(int empId);
}
```

