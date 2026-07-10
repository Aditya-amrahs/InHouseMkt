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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for IRequirementRepository using H2 in-memory database.
 * @DataJpaTest spins up only the JPA slice, no web layer.
 */
@DataJpaTest
@DisplayName("RequirementRepository Integration Tests")
class RequirementRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IRequirementRepository requirementRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        User user = User.builder().userId("test@company.com").password("pass").build();
        em.persist(user);

        employee = Employee.builder()
                .empName("Test Employee")
                .deptName("IT")
                .location("Pune")
                .user(user)
                .build();
        em.persist(employee);
        em.flush();
    }

    @Test
    @DisplayName("testAddRequirement_ValidData_ReturnsSavedRequirement")
    void testAddRequirement_ValidData_ReturnsSavedRequirement() {
        Requirement req = Requirement.builder()
                .title("Looking for PG")
                .category("Accommodation")
                .type("RENT")
                .price(7000.0)
                .date(LocalDate.now())
                .emp(employee)
                .build();

        Requirement saved = requirementRepository.save(req);

        assertThat(saved.getResId()).isPositive();
        assertThat(saved.getTitle()).isEqualTo("Looking for PG");
    }

    @Test
    @DisplayName("testGetAllRequirements_FilterByCategoryAndType_ReturnsFilteredList")
    void testGetAllRequirements_FilterByCategoryAndType_ReturnsFilteredList() {
        Requirement r1 = Requirement.builder()
                .title("PG needed").category("Accommodation").type("RENT")
                .date(LocalDate.now()).emp(employee).build();
        Requirement r2 = Requirement.builder()
                .title("Bike needed").category("Vehicle").type("RENT")
                .date(LocalDate.now()).emp(employee).build();

        requirementRepository.save(r1);
        requirementRepository.save(r2);

        List<Requirement> result = requirementRepository.findByCategoryAndType("Accommodation", "RENT");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Accommodation");
    }

    @Test
    @DisplayName("testGetRequirement_NonExistingId_ReturnsEmpty")
    void testGetRequirement_NonExistingId_ReturnsEmpty() {
        Optional<Requirement> result = requirementRepository.findById(99999);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("testDeleteRequirement_ExistingId_RemovesRequirement")
    void testDeleteRequirement_ExistingId_RemovesRequirement() {
        Requirement req = Requirement.builder()
                .title("Temp requirement").category("Other").type("FREE")
                .date(LocalDate.now()).emp(employee).build();
        Requirement saved = requirementRepository.save(req);
        int id = saved.getResId();

        requirementRepository.deleteById(id);

        assertThat(requirementRepository.findById(id)).isEmpty();
    }
}
