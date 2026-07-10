package com.inhouse.marketplace.service.impl;

import com.inhouse.marketplace.entity.*;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.*;
import com.inhouse.marketplace.service.IEmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of IEmployeeService.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements IEmployeeService {

    private final IEmployeeRepository employeeRepository;
    private final IOfferRepository offerRepository;
    private final IRequirementRepository requirementRepository;
    private final IProposalRepository proposalRepository;
    private final IUserRepository userRepository;

    @Override
    public Employee addEmployee(Employee emp) {
        // If the linked User is already registered, re-attach the managed instance
        // so the CascadeType.ALL persist doesn't try to insert a duplicate row.
        if (emp.getUser() != null && emp.getUser().getUserId() != null) {
            userRepository.findById(emp.getUser().getUserId()).ifPresent(emp::setUser);
        }
        return employeeRepository.save(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeByUserId(String userId) {
        return employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", userId));
    }

    @Override
    public Employee editEmployee(Employee emp) {
        if (!employeeRepository.existsById(emp.getEmpId())) {
            throw new ResourceNotFoundException("Employee", emp.getEmpId());
        }
        return employeeRepository.save(emp);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployee(int empId) {
        return employeeRepository.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", empId));
    }

    @Override
    public Offer updateIsAvailable(Offer offer) {
        Offer existing = offerRepository.findById(offer.getResId())
                .orElseThrow(() -> new ResourceNotFoundException("Offer", offer.getResId()));
        existing.setAvailable(offer.isAvailable());
        existing.setAvailableUpto(offer.getAvailableUpto());
        return offerRepository.save(existing);
    }

    @Override
    public Requirement updateIsFulfilled(Requirement req) {
        Requirement existing = requirementRepository.findById(req.getResId())
                .orElseThrow(() -> new ResourceNotFoundException("Requirement", req.getResId()));
        existing.setFulfilled(true);
        existing.setFulfilledOn(LocalDate.now());
        return requirementRepository.save(existing);
    }

    @Override
    public Proposal updateIsAccepted(Proposal prop) {
        Proposal existing = proposalRepository.findById(prop.getPropId())
                .orElseThrow(() -> new ResourceNotFoundException("Proposal", prop.getPropId()));
        existing.setAccepted(true);
        existing.setAcceptedOn(LocalDate.now());
        return proposalRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offer> getAllOffers(int empId) {
        return offerRepository.findAllByEmpId(empId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Requirement> getAllRequirements(int empId) {
        return requirementRepository.findAllByEmpId(empId);
    }
}
