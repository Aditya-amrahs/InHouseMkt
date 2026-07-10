package com.inhouse.marketplace.service;

import com.inhouse.marketplace.entity.*;
import com.inhouse.marketplace.repository.IResourceRepository;
import com.inhouse.marketplace.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ResourceServiceImpl — covers TEST MATRIX: Resource Module.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ResourceService Unit Tests")
class ResourceServiceTest {

    @Mock private IResourceRepository resourceRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Employee employee;
    private Requirement requirement;
    private Offer offer;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().empId(1).empName("Grace").build();
        requirement = Requirement.builder()
                .resId(1).title("Need PG").category("Accommodation").type("RENT").emp(employee).build();
        offer = Offer.builder()
                .resId(2).title("PG room free").category("Accommodation").type("RENT").emp(employee).build();
    }

    @Test
    @DisplayName("testGetAllResources_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllResources_FilterByCategoryAndType_ReturnsFilteredList() {
        when(resourceRepository.findByCategoryAndType("Accommodation", "RENT"))
                .thenReturn(List.of(requirement, offer));

        List<Resource> result = resourceService.getAllResources("Accommodation", "RENT");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> "Accommodation".equals(r.getCategory()));
        verify(resourceRepository).findByCategoryAndType("Accommodation", "RENT");
    }

    @Test
    @DisplayName("testGetAllResources_ByEmpId_ReturnsEmployeeResources")
    void testGetAllResources_ByEmpId_ReturnsEmployeeResources() {
        when(resourceRepository.findAllByEmpId(1)).thenReturn(List.of(requirement, offer));

        List<Resource> result = resourceService.getAllResources(1);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getEmp().getEmpId() == 1);
    }

    @Test
    @DisplayName("testGetAllResources_CategoryOnly_ReturnsCategoryMatches")
    void testGetAllResources_CategoryOnly_ReturnsCategoryMatches() {
        when(resourceRepository.findByCategory("Accommodation")).thenReturn(List.of(requirement, offer));

        List<Resource> result = resourceService.getAllResources("Accommodation", null);

        assertThat(result).hasSize(2);
        verify(resourceRepository).findByCategory("Accommodation");
    }

    @Test
    @DisplayName("testGetAllResources_TypeOnly_ReturnsTypeMatches")
    void testGetAllResources_TypeOnly_ReturnsTypeMatches() {
        when(resourceRepository.findByType("RENT")).thenReturn(List.of(requirement));

        List<Resource> result = resourceService.getAllResources(null, "RENT");

        assertThat(result).hasSize(1);
        verify(resourceRepository).findByType("RENT");
    }

    @Test
    @DisplayName("testGetAllResources_NoFilters_ReturnsAll")
    void testGetAllResources_NoFilters_ReturnsAll() {
        when(resourceRepository.findAll()).thenReturn(List.of(requirement, offer));

        List<Resource> result = resourceService.getAllResources(null, null);

        assertThat(result).hasSize(2);
        verify(resourceRepository).findAll();
    }
}
