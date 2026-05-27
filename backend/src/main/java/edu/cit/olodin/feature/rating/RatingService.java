package edu.cit.olodin.feature.rating;

import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.feature.listing.Listing;
import edu.cit.olodin.feature.listing.ListingRepository;
import edu.cit.olodin.feature.user.Role;
import edu.cit.olodin.feature.user.User;
import edu.cit.olodin.feature.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public RatingService(
            RatingRepository ratingRepository,
            ListingRepository listingRepository,
            UserRepository userRepository
    ) {
        this.ratingRepository = ratingRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));
    }

    public RatingResponse createOrUpdateRating(Long listingId, RatingRequest request) {
        User user = getCurrentUser();

        if (user.getRole() != Role.STUDENT) {
            throw new RuntimeException("Only students can rate listings");
        }

        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        Rating rating = ratingRepository.findByListingAndUser(listing, user)
                .orElse(new Rating());

        rating.setListing(listing);
        rating.setUser(user);
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());

        return toResponse(ratingRepository.save(rating));
    }

    public List<RatingResponse> getRatingsByListing(Long listingId) {
        return ratingRepository.findByListingId(listingId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RatingResponse> getMyRatings() {
        User user = getCurrentUser();

        return ratingRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public RatingSummaryResponse getSummary(Long listingId) {
        List<Rating> ratings = ratingRepository.findByListingId(listingId);

        double average = ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);

        return new RatingSummaryResponse(
                listingId,
                Math.round(average * 10.0) / 10.0,
                (long) ratings.size()
        );
    }

    public void deleteRating(Long ratingId) {
        User user = getCurrentUser();

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        if (!rating.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own rating");
        }

        ratingRepository.delete(rating);
    }

    private RatingResponse toResponse(Rating rating) {
        User user = rating.getUser();
        Listing listing = rating.getListing();

        return new RatingResponse(
                rating.getId(),
                listing.getId(),
                listing.getName(),
                user.getId(),
                user.getFirstname() + " " + user.getLastname(),
                rating.getRating(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}