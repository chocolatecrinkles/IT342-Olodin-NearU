package edu.cit.olodin.feature.rating;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "http://localhost:3000")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/listings/{listingId}")
    public ResponseEntity<RatingResponse> createOrUpdateRating(
            @PathVariable Long listingId,
            @RequestBody RatingRequest request
    ) {
        return ResponseEntity.ok(ratingService.createOrUpdateRating(listingId, request));
    }

    @GetMapping("/listings/{listingId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByListing(
            @PathVariable Long listingId
    ) {
        return ResponseEntity.ok(ratingService.getRatingsByListing(listingId));
    }

    @GetMapping("/listings/{listingId}/summary")
    public ResponseEntity<RatingSummaryResponse> getSummary(
            @PathVariable Long listingId
    ) {
        return ResponseEntity.ok(ratingService.getSummary(listingId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<RatingResponse>> getMyRatings() {
        return ResponseEntity.ok(ratingService.getMyRatings());
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<?> deleteRating(@PathVariable Long ratingId) {
        ratingService.deleteRating(ratingId);
        return ResponseEntity.ok().build();
    }
}