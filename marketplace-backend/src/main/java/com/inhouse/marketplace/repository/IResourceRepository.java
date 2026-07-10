package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the abstract Resource entity (combined Requirement + Offer view).
 */
@Repository
public interface IResourceRepository extends JpaRepository<Resource, Integer> {

    /**
     * Fetch all resources (any type) filtered by category and type.
     */
    @Query("SELECT r FROM Resource r WHERE r.category = :category AND r.type = :type")
    List<Resource> findByCategoryAndType(@Param("category") String category,
                                          @Param("type") String type);

    /**
     * Fetch all resources owned by a specific employee.
     */
    @Query("SELECT r FROM Resource r WHERE r.emp.empId = :empId")
    List<Resource> findAllByEmpId(@Param("empId") int empId);

    /**
     * Filter resources by category only.
     */
    List<Resource> findByCategory(String category);

    /**
     * Filter resources by type only.
     */
    List<Resource> findByType(String type);
}
