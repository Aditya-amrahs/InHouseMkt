package com.inhouse.marketplace.repository;

import com.inhouse.marketplace.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Offer entity operations.
 */
@Repository
public interface IOfferRepository extends JpaRepository<Offer, Integer> {

    /**
     * Filter offers by category and type.
     */
    @Query("SELECT o FROM Offer o WHERE o.category = :category AND o.type = :type")
    List<Offer> findByCategoryAndType(@Param("category") String category,
                                       @Param("type") String type);

    /**
     * Get all offers posted by a specific employee.
     */
    @Query("SELECT o FROM Offer o WHERE o.emp.empId = :empId")
    List<Offer> findAllByEmpId(@Param("empId") int empId);

    /**
     * Filter offers by category only.
     */
    List<Offer> findByCategory(String category);

    /**
     * Filter offers by type only.
     */
    List<Offer> findByType(String type);

    /**
     * Filter offers by availability.
     */
    List<Offer> findByIsAvailable(boolean isAvailable);
}
