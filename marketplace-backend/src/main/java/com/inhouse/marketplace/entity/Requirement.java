package com.inhouse.marketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents something an employee NEEDS from the marketplace.
 * Extends the abstract Resource base class.
 */
@Entity
@DiscriminatorValue("REQUIREMENT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Requirement extends Resource {

    /**
     * Whether this requirement has been fulfilled.
     */
    @Column(name = "is_fulfilled")
    private boolean isFulfilled;

    /**
     * Date the requirement was fulfilled.
     */
    @Column(name = "fulfilled_on")
    private LocalDate fulfilledOn;

    /**
     * Proposals submitted by other employees against this requirement.
     */
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties({"resource"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Proposal> proposals = new ArrayList<>();
}
