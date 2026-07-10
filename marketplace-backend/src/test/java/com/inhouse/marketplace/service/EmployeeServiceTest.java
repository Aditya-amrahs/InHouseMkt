package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.*;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.*;
import com.inhouse.marketplace.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EmployeeServiceImpl — covers TEST MATRIX: Employee Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeService Unit Tests")
class EmployeeServiceTest {

    @Mock private IEmployeeRepository employeeRepository;
    @Mock private IOfferRepository offerRepository;
    @Mock private IRequirementRepository requirementRepository;
    @Mock private IProposalRepository proposalRepository;
    @Mock private IUserRepository userRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        User user = User.builder().userId("emp001@company.com").password("pass").build();
        employee = Employee.builder()
                .empId(1)
                .empName("Alice Smith")
                .deptName("Engineering")
                .location("Bangalore")
                .user(user)
                .build();
    }

    @Test
    @DisplayName("testAddEmployee_ValidData_ReturnsSavedEmployee")
    void testAddEmployee_ValidData_ReturnsSavedEmployee() {
        when(userRepository.findById("emp001@company.com")).thenReturn(Optional.empty());
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.addEmployee(employee);

        assertThat(result).isNotNull();
        assertThat(result.getEmpName()).isEqualTo("Alice Smith");
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("testAddEmployee_ExistingUser_ReattachesManagedUser")
    void testAddEmployee_ExistingUser_ReattachesManagedUser() {
        User managed = User.builder().userId("emp001@company.com").password("pass").build();
        when(userRepository.findById("emp001@company.com")).thenReturn(Optional.of(managed));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.addEmployee(employee);

        assertThat(result.getUser()).isSameAs(managed);
        verify(employeeRepository).save(employee);
    }

    @Test
    @DisplayName("testGetEmployeeByUserId_ExistingUser_ReturnsEmployee")
    void testGetEmployeeByUserId_ExistingUser_ReturnsEmployee() {
        when(employeeRepository.findByUserId("emp001@company.com")).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeByUserId("emp001@company.com");

        assertThat(result.getEmpId()).isEqualTo(1);
    }

    @Test
    @DisplayName("testGetEmployeeByUserId_NonExistingUser_ThrowsException")
    void testGetEmployeeByUserId_NonExistingUser_ThrowsException() {
        when(employeeRepository.findByUserId("ghost@company.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeByUserId("ghost@company.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("testUpdateIsFulfilled_ExistingRequirement_MarksFulfilledAndSetsDate")
    void testUpdateIsFulfilled_ExistingRequirement_MarksFulfilledAndSetsDate() {
        Requirement existing = Requirement.builder().resId(20).title("PG").emp(employee).isFulfilled(false).build();
        when(requirementRepository.findById(20)).thenReturn(Optional.of(existing));
        when(requirementRepository.save(any(Requirement.class))).thenAnswer(inv -> inv.getArgument(0));

        Requirement result = employeeService.updateIsFulfilled(Requirement.builder().resId(20).build());

        assertThat(result.isFulfilled()).isTrue();
        assertThat(result.getFulfilledOn()).isNotNull();
    }

    @Test
    @DisplayName("testUpdateIsAvailable_ExistingOffer_MarksUnavailable")
    void testUpdateIsAvailable_ExistingOffer_MarksUnavailable() {
        Offer existing = Offer.builder().resId(10).title("Laptop").emp(employee).isAvailable(true).build();
        when(offerRepository.findById(10)).thenReturn(Optional.of(existing));
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        Offer result = employeeService.updateIsAvailable(Offer.builder().resId(10).isAvailable(false).build());

        assertThat(result.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("testUpdateIsAccepted_ExistingProposal_MarksAcceptedAndSetsDate")
    void testUpdateIsAccepted_ExistingProposal_MarksAcceptedAndSetsDate() {
        Proposal existing = Proposal.builder().propId(5).proposal("deal").emp(employee).isAccepted(false).build();
        when(proposalRepository.findById(5)).thenReturn(Optional.of(existing));
        when(proposalRepository.save(any(Proposal.class))).thenAnswer(inv -> inv.getArgument(0));

        Proposal result = employeeService.updateIsAccepted(Proposal.builder().propId(5).build());

        assertThat(result.isAccepted()).isTrue();
        assertThat(result.getAcceptedOn()).isNotNull();
    }

    @Test
    @DisplayName("testEditEmployee_ValidData_UpdatesEmployee")
    void testEditEmployee_ValidData_UpdatesEmployee() {
        employee.setDeptName("Product");
        when(employeeRepository.existsById(employee.getEmpId())).thenReturn(true);
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.editEmployee(employee);

        assertThat(result.getDeptName()).isEqualTo("Product");
    }

    @Test
    @DisplayName("testGetEmployee_ExistingId_ReturnsEmployee")
    void testGetEmployee_ExistingId_ReturnsEmployee() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployee(1);

        assertThat(result.getEmpId()).isEqualTo(1);
        assertThat(result.getEmpName()).isEqualTo("Alice Smith");
    }

    @Test
    @DisplayName("testGetEmployee_NonExistingId_ThrowsException")
    void testGetEmployee_NonExistingId_ThrowsException() {
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployee(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("testGetAllOffers_ByEmpId_ReturnsOfferList")
    void testGetAllOffers_ByEmpId_ReturnsOfferList() {
        Offer offer = Offer.builder().resId(10).title("Used Laptop").emp(employee).build();
        when(offerRepository.findAllByEmpId(1)).thenReturn(List.of(offer));

        List<Offer> result = employeeService.getAllOffers(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Used Laptop");
    }

    @Test
    @DisplayName("testGetAllRequirements_ByEmpId_ReturnsRequirementList")
    void testGetAllRequirements_ByEmpId_ReturnsRequirementList() {
        Requirement req = Requirement.builder().resId(20).title("Looking for PG").emp(employee).build();
        when(requirementRepository.findAllByEmpId(1)).thenReturn(List.of(req));

        List<Requirement> result = employeeService.getAllRequirements(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Looking for PG");
    }
}
