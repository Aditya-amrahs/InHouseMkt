package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for IResourceRepository (combined Requirement + Offer view)
 * using the H2 in-memory database.
 */
@DataJpaTest
@DisplayName("ResourceRepository Integration Tests")
class ResourceRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IResourceRepository resourceRepository;

    private Employee employee;
    private Employee otherEmployee;

    @BeforeEach
    void setUp() {
        User u1 = User.builder().userId("res1@company.com").password("pass").build();
        User u2 = User.builder().userId("res2@company.com").password("pass").build();
        em.persist(u1);
        em.persist(u2);

        employee = Employee.builder().empName("Owner").deptName("IT").location("Pune").user(u1).build();
        otherEmployee = Employee.builder().empName("Other").deptName("HR").location("Mumbai").user(u2).build();
        em.persist(employee);
        em.persist(otherEmployee);

        // One matching Requirement + one matching Offer + one non-matching Offer
        em.persist(Requirement.builder()
                .title("Need PG").category("Accommodation").type("RENT")
                .date(LocalDate.now()).emp(employee).build());
        em.persist(Offer.builder()
                .title("PG room available").category("Accommodation").type("RENT")
                .isAvailable(true).date(LocalDate.now()).emp(employee).build());
        em.persist(Offer.builder()
                .title("Old phone").category("Electronics").type("SELL")
                .isAvailable(true).date(LocalDate.now()).emp(otherEmployee).build());
        em.flush();
    }

    @Test
    @DisplayName("testGetAllResources_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllResources_FilterByCategoryAndType_ReturnsFilteredList() {
        List<Resource> result = resourceRepository.findByCategoryAndType("Accommodation", "RENT");

        // Combined view: both the Requirement and the Offer match
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> "Accommodation".equals(r.getCategory()) && "RENT".equals(r.getType()));
        assertThat(result).anyMatch(r -> r instanceof Requirement);
        assertThat(result).anyMatch(r -> r instanceof Offer);
    }

    @Test
    @DisplayName("testGetAllResources_ByEmpId_ReturnsEmployeeResources")
    void testGetAllResources_ByEmpId_ReturnsEmployeeResources() {
        List<Resource> result = resourceRepository.findAllByEmpId(employee.getEmpId());

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.getEmp().getEmpId() == employee.getEmpId());
    }
}
