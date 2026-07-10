package com.inhouse.marketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents something an employee is OFFERING on the marketplace.
 * Extends the abstract Resource base class.
 */
@Entity
@DiscriminatorValue("OFFER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Offer extends Resource {

    /**
     * Whether the offer is currently available.
     */
    @Column(name = "is_available")
    private boolean isAvailable;

    /**
     * Date until which the offer is available.
     */
    @Column(name = "available_upto")
    private LocalDate availableUpto;

    /**
     * Proposals submitted against this offer.
     */
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties({"resource"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Proposal> proposals = new ArrayList<>();
}
