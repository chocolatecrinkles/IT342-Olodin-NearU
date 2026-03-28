package edu.cit.olodin.service;

import edu.cit.olodin.dto.ListingRequest;
import edu.cit.olodin.entity.Category;
import edu.cit.olodin.entity.Listing;
import edu.cit.olodin.entity.User;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.repository.ListingRepository;
import edu.cit.olodin.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println("User: " + authentication.getName());

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));
    }

    public Listing createListing(ListingRequest req) {

        User user = getCurrentUser();
        System.out.println("User: " + user.getId());

        Listing listing = new Listing();
        listing.setName(req.name);
        listing.setCategory(Category.valueOf(req.category));
        listing.setAddress(req.address);
        listing.setPrice(req.price);
        listing.setLatitude(req.latitude);
        listing.setLongitude(req.longitude);

        listing.setOwnerId(user.getId());
        return listingRepository.save(listing);
    }

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public List<Listing> getListingsForCurrentUser() {
        User user = getCurrentUser();

        return listingRepository.findByOwnerId(user.getId());
    }

    public Listing updateListing(Long id, ListingRequest req) {
        User user = getCurrentUser();

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new AuthException(
                        "Listing not found",
                        "LISTING_NOT_FOUND"
                ));

        if (!listing.getOwnerId().equals(user.getId())) {
            throw new AuthException(
                    "You are not allowed to edit this listing",
                    "AUTH_UNAUTHORIZED"
            );
        }

        listing.setName(req.name);
        listing.setCategory(Category.valueOf(req.category));
        listing.setAddress(req.address);
        listing.setPrice(req.price);
        listing.setLatitude(req.latitude);
        listing.setLongitude(req.longitude);

        return listingRepository.save(listing);
    }

    public void deleteListing(Long id) {
        User user = getCurrentUser();

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new AuthException(
                        "Listing not found",
                        "LISTING_NOT_FOUND"
                ));

        if (!listing.getOwnerId().equals(user.getId())) {
            throw new AuthException(
                    "You are not allowed to delete this listing",
                    "AUTH_UNAUTHORIZED"
            );
        }

        listingRepository.delete(listing);
    }
}
