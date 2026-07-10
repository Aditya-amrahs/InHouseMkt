package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.Requirement;
import com.inhouse.marketplace.service.IRequirementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Requirement operations.
 * Base path: /api/requirements
 */
@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
@Tag(name = "Requirement", description = "Marketplace requirement CRUD and filtering")
public class RequirementController {

    private final IRequirementService requirementService;

    /** POST /api/requirements — Add a new requirement */
    @PostMapping
    @Operation(summary = "Add new requirement")
    public ResponseEntity<Requirement> add(@RequestBody Requirement req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(requirementService.addRequirement(req));
    }

    /** PUT /api/requirements — Update a requirement */
    @PutMapping
    @Operation(summary = "Update requirement")
    public ResponseEntity<Requirement> update(@RequestBody Requirement req) {
        return ResponseEntity.ok(requirementService.editRequirement(req));
    }

    /** GET /api/requirements/{reqId} — Get requirement by ID */
    @GetMapping("/{reqId}")
    @Operation(summary = "Get requirement by ID")
    public ResponseEntity<Requirement> get(@PathVariable int reqId) {
        return ResponseEntity.ok(requirementService.getRequirement(reqId));
    }

    /** DELETE /api/requirements/{reqId} — Delete requirement */
    @DeleteMapping("/{reqId}")
    @Operation(summary = "Delete requirement")
    public ResponseEntity<Void> delete(@PathVariable int reqId) {
        requirementService.removeRequirement(reqId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/requirements — Get all requirements, with optional category and type filters.
     * Example: GET /api/requirements?category=Accommodation&type=RENT
     */
    @GetMapping
    @Operation(summary = "Get all requirements (optionally filtered by category and type)")
    public ResponseEntity<List<Requirement>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        if (category != null || type != null) {
            return ResponseEntity.ok(requirementService.getAllRequirements(category, type));
        }
        return ResponseEntity.ok(requirementService.getAllRequirements());
    }
}
