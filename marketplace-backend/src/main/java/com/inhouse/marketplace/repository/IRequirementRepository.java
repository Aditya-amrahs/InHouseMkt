package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Requirement entity operations.
 */
@Repository
public interface IRequirementRepository extends JpaRepository<Requirement, Integer> {

    /**
     * Filter requirements by category and type.
     */
    @Query("SELECT r FROM Requirement r WHERE r.category = :category AND r.type = :type")
    List<Requirement> findByCategoryAndType(@Param("category") String category,
                                             @Param("type") String type);

    /**
     * Get all requirements posted by a specific employee.
     */
    @Query("SELECT r FROM Requirement r WHERE r.emp.empId = :empId")
    List<Requirement> findAllByEmpId(@Param("empId") int empId);

    /**
     * Filter requirements by category only.
     */
    List<Requirement> findByCategory(String category);

    /**
     * Filter requirements by type only.
     */
    List<Requirement> findByType(String type);
}
