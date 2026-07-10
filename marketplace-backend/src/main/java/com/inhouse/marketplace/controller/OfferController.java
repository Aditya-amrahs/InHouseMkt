package com.inhouse.marketplace.controller;

import com.inhouse.marketplace.entity.Offer;
import com.inhouse.marketplace.service.IOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Offer operations.
 * Base path: /api/offers
 */
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Offer", description = "Marketplace offer CRUD and filtering")
public class OfferController {

    private final IOfferService offerService;

    /** POST /api/offers — Add a new offer */
    @PostMapping
    @Operation(summary = "Add new offer")
    public ResponseEntity<Offer> add(@RequestBody Offer offer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.addOffer(offer));
    }

    /** PUT /api/offers — Update an offer */
    @PutMapping
    @Operation(summary = "Update offer")
    public ResponseEntity<Offer> update(@RequestBody Offer offer) {
        return ResponseEntity.ok(offerService.editOffer(offer));
    }

    /** GET /api/offers/{offerId} — Get offer by ID */
    @GetMapping("/{offerId}")
    @Operation(summary = "Get offer by ID")
    public ResponseEntity<Offer> get(@PathVariable int offerId) {
        return ResponseEntity.ok(offerService.getOffer(offerId));
    }

    /** DELETE /api/offers/{offerId} — Delete offer */
    @DeleteMapping("/{offerId}")
    @Operation(summary = "Delete offer")
    public ResponseEntity<Void> delete(@PathVariable int offerId) {
        offerService.removeOffer(offerId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/offers — Get all offers, with optional category and type filters.
     * Example: GET /api/offers?category=Electronics&type=SELL
     */
    @GetMapping
    @Operation(summary = "Get all offers (optionally filtered by category and type)")
    public ResponseEntity<List<Offer>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type) {
        if (category != null || type != null) {
            return ResponseEntity.ok(offerService.getAllOffers(category, type));
        }
        return ResponseEntity.ok(offerService.getAllOffers());
    }
}
