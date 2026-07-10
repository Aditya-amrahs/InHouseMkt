package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.Proposal;
import com.inhouse.marketplace.service.IProposalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Proposal operations.
 * Base path: /api/proposals
 */
@RestController
@RequestMapping("/api/proposals")
@RequiredArgsConstructor
@Tag(name = "Proposal", description = "Proposal submission, retrieval and management")
public class ProposalController {

    private final IProposalService proposalService;

    /** POST /api/proposals — Submit a new proposal */
    @PostMapping
    @Operation(summary = "Submit a new proposal")
    public ResponseEntity<Proposal> add(@RequestBody Proposal prop) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proposalService.addProposal(prop));
    }

    /** PUT /api/proposals — Update a proposal */
    @PutMapping
    @Operation(summary = "Update a proposal")
    public ResponseEntity<Proposal> update(@RequestBody Proposal prop) {
        return ResponseEntity.ok(proposalService.editProposal(prop));
    }

    /** GET /api/proposals/{propId} — Get proposal by ID */
    @GetMapping("/{propId}")
    @Operation(summary = "Get proposal by ID")
    public ResponseEntity<Proposal> get(@PathVariable int propId) {
        return ResponseEntity.ok(proposalService.getProposal(propId));
    }

    /** DELETE /api/proposals/{propId} — Delete proposal */
    @DeleteMapping("/{propId}")
    @Operation(summary = "Delete proposal")
    public ResponseEntity<Void> delete(@PathVariable int propId) {
        proposalService.removeProposal(propId);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/proposals — Get all proposals */
    @GetMapping
    @Operation(summary = "Get all proposals")
    public ResponseEntity<List<Proposal>> getAll() {
        return ResponseEntity.ok(proposalService.getAllProposals());
    }
}
