package com.inhouse.marketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents a proposal submitted by an employee against a Resource
 * (either a Requirement or an Offer).
 */
@Entity
@Table(name = "proposals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prop_id")
    private int propId;

    /**
     * The text of the proposal — what the proposing employee is offering/requesting.
     */
    @Column(name = "proposal_text", nullable = false, length = 2000)
    private String proposal;

    /**
     * The monetary amount being proposed.
     */
    @Column(name = "amount")
    private double amount;

    /**
     * Date the proposal was submitted.
     */
    @Column(name = "proposal_date")
    private LocalDate proposalDate;

    /**
     * Whether this proposal has been accepted by the resource owner.
     */
    @Column(name = "is_accepted")
    private boolean isAccepted;

    /**
     * Date the proposal was accepted.
     */
    @Column(name = "accepted_on")
    private LocalDate acceptedOn;

    /**
     * The employee who submitted this proposal.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee emp;

    /**
     * The resource (Requirement or Offer) this proposal is linked to.
     * Incoming JSON only carries {"resId": n}; it is bound to a placeholder
     * (Requirement) instance and the service re-fetches the real managed entity.
     * On serialization the resource's own proposals list is suppressed to
     * prevent infinite recursion.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "res_id", nullable = false)
    @JsonDeserialize(as = Requirement.class)
    @JsonIgnoreProperties({"proposals"})
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Resource resource;
}
