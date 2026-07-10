package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Employee entity operations.
 */
@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {

    /**
     * Finds an employee by their linked user's userId.
     */
    @Query("SELECT e FROM Employee e WHERE e.user.userId = :userId")
    Optional<Employee> findByUserId(@Param("userId") String userId);
}
