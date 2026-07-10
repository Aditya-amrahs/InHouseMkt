package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.Employee;
import com.inhouse.marketplace.entity.Requirement;
import com.inhouse.marketplace.exception.ResourceNotFoundException;
import com.inhouse.marketplace.repository.IRequirementRepository;
import com.inhouse.marketplace.service.impl.RequirementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RequirementServiceImpl — covers TEST MATRIX: Requirement Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RequirementService Unit Tests")
class RequirementServiceTest {

    @Mock
    private IRequirementRepository requirementRepository;

    @InjectMocks
    private RequirementServiceImpl requirementService;

    private Requirement requirement;
    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().empId(1).empName("Bob").build();
        requirement = Requirement.builder()
                .resId(1)
                .title("Need PG accommodation")
                .category("Accommodation")
                .type("RENT")
                .price(5000.0)
                .emp(employee)
                .build();
    }

    @Test
    @DisplayName("testAddRequirement_ValidData_ReturnsSavedRequirement")
    void testAddRequirement_ValidData_ReturnsSavedRequirement() {
        when(requirementRepository.save(any(Requirement.class))).thenReturn(requirement);

        Requirement result = requirementService.addRequirement(requirement);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Need PG accommodation");
        // Date should be set automatically
        verify(requirementRepository).save(any(Requirement.class));
    }

    @Test
    @DisplayName("testRequirementUpdate_MarkAsFulfilled")
    void testRequirementUpdate_MarkAsFulfilled() {
        // Simulate the update by returning an updated requirement
        Requirement fulfilled = Requirement.builder()
                .resId(1).title("Need PG accommodation").isFulfilled(true)
                .fulfilledOn(LocalDate.now()).emp(employee).build();

        when(requirementRepository.existsById(1)).thenReturn(true);
        when(requirementRepository.save(any())).thenReturn(fulfilled);

        Requirement result = requirementService.editRequirement(requirement);

        verify(requirementRepository).save(any());
    }

    @Test
    @DisplayName("testDeleteRequirement_ExistingId_RemovesRequirement")
    void testDeleteRequirement_ExistingId_RemovesRequirement() {
        when(requirementRepository.existsById(1)).thenReturn(true);
        doNothing().when(requirementRepository).deleteById(1);

        assertThatCode(() -> requirementService.removeRequirement(1))
                .doesNotThrowAnyException();

        verify(requirementRepository).deleteById(1);
    }

    @Test
    @DisplayName("testGetAllRequirements_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllRequirements_FilterByCategoryAndType_ReturnsFilteredList() {
        when(requirementRepository.findByCategoryAndType("Accommodation", "RENT"))
                .thenReturn(List.of(requirement));

        List<Requirement> result = requirementService.getAllRequirements("Accommodation", "RENT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Accommodation");
    }

    @Test
    @DisplayName("testGetRequirement_NonExistingId_ReturnsNull")
    void testGetRequirement_NonExistingId_ReturnsNull() {
        when(requirementRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requirementService.getRequirement(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }
}
