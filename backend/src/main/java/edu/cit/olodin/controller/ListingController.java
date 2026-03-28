package edu.cit.olodin.controller;

import edu.cit.olodin.dto.ListingRequest;
import edu.cit.olodin.entity.Listing;
import edu.cit.olodin.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "http://localhost:3000")
public class ListingController {
    private final ListingService listingService;

    public ListingController(ListingService listingService){
        this.listingService = listingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<Listing> createListing(@RequestBody ListingRequest request) {
        System.out.println("create listing hit");
        return ResponseEntity.ok(listingService.createListing(request));
    }

    @GetMapping
    public List<Listing> getAllListings() {
        return listingService.getAllListings();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public List<Listing> getByOwner() {
        return listingService.getListingsForCurrentUser();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<Listing> updateListing(
            @PathVariable Long id,
            @RequestBody ListingRequest request
    ) {
        return ResponseEntity.ok(listingService.updateListing(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    public ResponseEntity<?> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
