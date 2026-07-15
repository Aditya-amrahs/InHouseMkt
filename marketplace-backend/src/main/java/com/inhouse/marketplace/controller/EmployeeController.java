package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.*;
import com.inhouse.marketplace.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.util.List;

/**
 * REST controller for Employee operations.
 * Base path: /api/employees
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee management and status updates")
public class EmployeeController {

    private final IEmployeeService employeeService;

    private int actingEmployeeId(HttpSession session) {
        Object userId = session.getAttribute("AUTH_USER_ID");
        if (userId == null) {
            throw new com.inhouse.marketplace.exception.ForbiddenOperationException("An authenticated session is required.");
        }
        return employeeService.getEmployeeByUserId(userId.toString()).getEmpId();
    }

    /** POST /api/employees — Add a new employee */
    @PostMapping
    @Operation(summary = "Add new employee")
    public ResponseEntity<Employee> add(@RequestBody Employee emp) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(emp));
    }

    /** PUT /api/employees — Update employee details */
    @PutMapping
    @Operation(summary = "Update employee")
    public ResponseEntity<Employee> update(@RequestBody Employee emp) {
        return ResponseEntity.ok(employeeService.editEmployee(emp));
    }

    /** GET /api/employees/{empId} — Get employee by ID */
    @GetMapping("/{empId}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<Employee> get(@PathVariable int empId) {
        return ResponseEntity.ok(employeeService.getEmployee(empId));
    }

    /** GET /api/employees/by-user?userId= — Get employee by linked login userId */
    @GetMapping("/by-user")
    @Operation(summary = "Get employee by linked user ID")
    public ResponseEntity<Employee> getByUserId(@RequestParam String userId) {
        return ResponseEntity.ok(employeeService.getEmployeeByUserId(userId));
    }

    /** GET /api/employees/{empId}/offers — All offers by employee */
    @GetMapping("/{empId}/offers")
    @Operation(summary = "Get all offers by employee")
    public ResponseEntity<List<Offer>> getOffers(@PathVariable int empId) {
        return ResponseEntity.ok(employeeService.getAllOffers(empId));
    }

    /** GET /api/employees/{empId}/requirements — All requirements by employee */
    @GetMapping("/{empId}/requirements")
    @Operation(summary = "Get all requirements by employee")
    public ResponseEntity<List<Requirement>> getRequirements(@PathVariable int empId) {
        return ResponseEntity.ok(employeeService.getAllRequirements(empId));
    }

    /** PATCH /api/employees/offers/availability — Mark offer available/unavailable */
    @PatchMapping("/offers/availability")
    @Operation(summary = "Update offer availability")
    public ResponseEntity<Offer> updateOfferAvailability(@RequestBody Offer offer) {
        return ResponseEntity.ok(employeeService.updateIsAvailable(offer));
    }

    /** PATCH /api/employees/requirements/fulfilled — Mark requirement as fulfilled */
    @PatchMapping("/requirements/fulfilled")
    @Operation(summary = "Mark requirement as fulfilled")
    public ResponseEntity<Requirement> updateRequirementFulfilled(@RequestBody Requirement req) {
        return ResponseEntity.ok(employeeService.updateIsFulfilled(req));
    }

    /** PATCH /api/employees/proposals/accepted — Accept a proposal */
    @PatchMapping("/proposals/accepted")
    @Operation(summary = "Accept a proposal")
    public ResponseEntity<Proposal> acceptProposal(@RequestBody Proposal prop, HttpSession session) {
        return ResponseEntity.ok(employeeService.acceptProposal(prop.getPropId(), actingEmployeeId(session)));
    }
}
