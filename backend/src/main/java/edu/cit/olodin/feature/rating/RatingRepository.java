package edu.cit.olodin.feature.rating;

import edu.cit.olodin.feature.listing.Listing;
import edu.cit.olodin.feature.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByListingId(Long listingId);

    List<Rating> findByUserId(Long userId);

    Optional<Rating> findByListingAndUser(Listing listing, User user);

    long countByListingId(Long listingId);
}