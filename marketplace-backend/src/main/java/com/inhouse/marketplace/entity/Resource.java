package com.inhouse.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Abstract base class for marketplace items.
 * Uses SINGLE_TABLE inheritance so both Requirement and Offer are stored in one table
 * with a discriminator column "resource_type".
 */
@Entity
@Table(name = "resources")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "resource_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "res_id")
    private int resId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Category — e.g., "Accommodation", "Electronics", "Vehicle", "Services"
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * Type — e.g., "SELL", "RENT", "FREE", "HELP"
     */
    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "price")
    private double price;

    @Column(name = "posted_date")
    private LocalDate date;

    /**
     * The employee who owns this resource (requirement or offer).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee emp;
}
