package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.*;
import java.util.List;

/**
 * Service contract for Employee operations, including status management.
 */
public interface IEmployeeService {

    /** Create and save an employee (linked to a User). */
    Employee addEmployee(Employee emp);

    /** Update employee details (name, dept, location). */
    Employee editEmployee(Employee emp);

    /** Fetch an employee by empId. Throws ResourceNotFoundException if not found. */
    Employee getEmployee(int empId);

    /** Fetch an employee by their linked login userId. Throws ResourceNotFoundException if not found. */
    Employee getEmployeeByUserId(String userId);

    /** Mark an offer as unavailable. */
    Offer updateIsAvailable(Offer offer);

    /** Mark a requirement as fulfilled. */
    Requirement updateIsFulfilled(Requirement req);

    /** Mark a proposal as accepted. */
    Proposal updateIsAccepted(Proposal prop);

    /** Accept a proposal only when the acting employee owns its resource. */
    Proposal acceptProposal(int propId, int actingEmployeeId);

    /** Get all offers posted by a specific employee. */
    List<Offer> getAllOffers(int empId);

    /** Get all requirements posted by a specific employee. */
    List<Requirement> getAllRequirements(int empId);
}
