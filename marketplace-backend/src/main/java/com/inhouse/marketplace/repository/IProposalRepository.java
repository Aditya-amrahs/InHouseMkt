package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Proposal entity operations.
 */
@Repository
public interface IProposalRepository extends JpaRepository<Proposal, Integer> {

    /**
     * Get all proposals submitted by a specific employee.
     */
    @Query("SELECT p FROM Proposal p WHERE p.emp.empId = :empId")
    List<Proposal> findAllByEmpId(@Param("empId") int empId);

    /**
     * Get all proposals linked to a specific resource.
     */
    @Query("SELECT p FROM Proposal p WHERE p.resource.resId = :resId")
    List<Proposal> findAllByResourceId(@Param("resId") int resId);
}
