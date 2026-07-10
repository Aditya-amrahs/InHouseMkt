package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Employee;
import com.inhouse.marketplace.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for IEmployeeRepository using the H2 in-memory database.
 */
@DataJpaTest
@DisplayName("EmployeeRepository Integration Tests")
class EmployeeRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IEmployeeRepository employeeRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().userId("emp@company.com").password("pass").build();
        em.persist(user);
        em.flush();
    }

    @Test
    @DisplayName("testAddEmployee_ValidData_ReturnsSavedEmployee")
    void testAddEmployee_ValidData_ReturnsSavedEmployee() {
        Employee emp = Employee.builder()
                .empName("Hank").deptName("IT").location("Pune").user(user).build();

        Employee saved = employeeRepository.save(emp);

        assertThat(saved.getEmpId()).isPositive();
        assertThat(saved.getUser().getUserId()).isEqualTo("emp@company.com");
    }

    @Test
    @DisplayName("testFindByUserId_ExistingUser_ReturnsEmployee")
    void testFindByUserId_ExistingUser_ReturnsEmployee() {
        Employee emp = Employee.builder()
                .empName("Hank").deptName("IT").location("Pune").user(user).build();
        employeeRepository.save(emp);

        Optional<Employee> result = employeeRepository.findByUserId("emp@company.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmpName()).isEqualTo("Hank");
    }

    @Test
    @DisplayName("testFindByUserId_NonExistingUser_ReturnsEmpty")
    void testFindByUserId_NonExistingUser_ReturnsEmpty() {
        Optional<Employee> result = employeeRepository.findByUserId("ghost@company.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("testEditEmployee_ValidData_UpdatesEmployee")
    void testEditEmployee_ValidData_UpdatesEmployee() {
        Employee saved = employeeRepository.save(Employee.builder()
                .empName("Hank").deptName("IT").location("Pune").user(user).build());

        saved.setDeptName("Product");
        saved.setLocation("Hyderabad");
        Employee updated = employeeRepository.save(saved);

        assertThat(updated.getDeptName()).isEqualTo("Product");
        assertThat(updated.getLocation()).isEqualTo("Hyderabad");
    }
}
