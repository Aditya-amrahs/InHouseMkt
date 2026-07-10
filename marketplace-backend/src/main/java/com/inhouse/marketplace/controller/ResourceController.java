package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.Resource;
import com.inhouse.marketplace.service.IResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller providing a unified view of all marketplace resources (Requirements + Offers).
 * Base path: /api/resources
 */
@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
@Tag(name = "Resource", description = "Unified marketplace resource browsing")
public class ResourceController {

    private final IResourceService resourceService;

    /**
     * GET /api/resources?category=&type= — Get all resources filtered by category and type.
     * GET /api/resources?empId= — Get all resources owned by a specific employee.
     */
    @GetMapping
    @Operation(summary = "Browse all resources with optional filters")
    public ResponseEntity<List<Resource>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer empId) {
        if (empId != null) {
            return ResponseEntity.ok(resourceService.getAllResources(empId));
        }
        // Handles both-filters, single-filter, and no-filter cases
        return ResponseEntity.ok(resourceService.getAllResources(category, type));
    }
}
