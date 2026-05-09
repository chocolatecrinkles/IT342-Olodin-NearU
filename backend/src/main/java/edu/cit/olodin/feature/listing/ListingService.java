package edu.cit.olodin.feature.listing;

import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.feature.user.User;
import edu.cit.olodin.feature.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingImageRepository listingImageRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository, ListingImageRepository listingImageRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.listingImageRepository = listingImageRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        System.out.println("User: " + authentication.getName());

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found", "AUTH_USER_NOT_FOUND"));
    }

    private void validateCategoryAndType(Type type, Category category) {
        if (type == Type.ACCOMMODATION) {
            if (!(category == Category.BOARDING_HOUSE || category == Category.DORM)) {
                throw new AuthException("Invalid category for ACCOMMODATION", "VALIDATION_ERROR");
            }
        }

        if (type == Type.SERVICE) {
            if (!(category == Category.RESTAURANT || category == Category.CAFE || category == Category.LAUNDROMAT)) {
                throw new AuthException("Invalid category for SERVICE", "VALIDATION_ERROR");
            }
        }

        if (type == Type.OTHER) {
            if (category != Category.OTHER) {
                throw new AuthException("OTHER type must use OTHER category", "VALIDATION_ERROR");
            }
        }
    }

    private void validatePricing(ListingRequest req) {
        PricingType pricingType = PricingType.valueOf(req.pricingType);

        if (pricingType == PricingType.RANGE) {
            if (req.minPrice == null || req.maxPrice == null) {
                throw new AuthException("Range pricing requires minPrice and maxPrice", "VALIDATION_ERROR");
            }

            if (req.minPrice > req.maxPrice) {
                throw new AuthException("minPrice cannot be greater than maxPrice", "VALIDATION_ERROR");
            }
        } else {
            if (req.price == null || req.price <= 0) {
                throw new RuntimeException("Price must be greater than 0");
            }
        }
    }

    public Listing createListing(ListingRequest req) {

        User user = getCurrentUser();
        System.out.println("User: " + user.getId());

        Type type = Type.valueOf(req.listingType);
        Category category = Category.valueOf(req.category);
        PricingType pricingType = PricingType.valueOf(req.pricingType);

        validateCategoryAndType(type, category);
        validatePricing(req);

        Listing listing = new Listing();
        listing.setName(req.name);
        listing.setListingType(type);
        listing.setCategory(category);
        listing.setAddress(req.address);
        listing.setPricingType(pricingType);
        listing.setPrice(req.price);
        listing.setMinPrice(req.minPrice);
        listing.setMaxPrice(req.maxPrice);
        listing.setLatitude(req.latitude);
        listing.setLongitude(req.longitude);
        listing.setDescription(req.description);

        listing.setOwnerId(user.getId());

        if (pricingType == PricingType.RANGE) {
            listing.setPrice(null);
        } else {
            listing.setMinPrice(null);
            listing.setMaxPrice(null);
        }
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

        Type type = Type.valueOf(req.listingType);
        Category category = Category.valueOf(req.category);
        PricingType pricingType = PricingType.valueOf(req.pricingType);

        validateCategoryAndType(type, category);
        validatePricing(req);

        listing.setName(req.name);
        listing.setListingType(type);
        listing.setCategory(category);
        listing.setAddress(req.address);
        listing.setPricingType(pricingType);
        listing.setPrice(req.price);
        listing.setMinPrice(req.minPrice);
        listing.setMaxPrice(req.maxPrice);
        listing.setLatitude(req.latitude);
        listing.setLongitude(req.longitude);
        listing.setDescription(req.description);

        if (pricingType == PricingType.RANGE) {
            listing.setPrice(null);
        } else {
            listing.setMinPrice(null);
            listing.setMaxPrice(null);
        }

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

    public List<String> uploadImages(Long listingId, List<MultipartFile> files) {

        if (files.size() > 15) {
            throw new AuthException("Maximum 15 images allowed", "VALIDATION_ERROR");
        }

        List<String> uploadedPaths = new ArrayList<>();

        for (MultipartFile file : files) {

            if (!file.getContentType().startsWith("image/")) {
                throw new AuthException("Only image files allowed", "VALIDATION_ERROR");
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(System.getProperty("user.dir"), "../uploads/", fileName);

            try {
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                ListingImage img = new ListingImage();
                img.setListingId(listingId);
                img.setImageUrl("/uploads/" + fileName);

                listingImageRepository.save(img);

                uploadedPaths.add(img.getImageUrl());

            } catch (IOException e) {
                throw new AuthException("Upload failed", "UPLOAD_ERROR");
            }
        }

        return uploadedPaths;
    }

    public List<ListingImage> getImagesByListing(Long listingId) {
        return listingImageRepository.findByListingId(listingId);
    }

    public List<Listing> getFilteredListings(String category, Double minPrice, Double maxPrice, String keyword) {
        List<Listing> listings = listingRepository.findAll();

        return listings.stream()
                .filter(l -> category == null || l.getCategory().name().equalsIgnoreCase(category))
                .filter(l -> {
                    if (keyword == null || keyword.isEmpty()) return true;
                    String kw = keyword.toLowerCase();
                    return (l.getName() != null && l.getName().toLowerCase().contains(kw)) ||
                           (l.getAddress() != null && l.getAddress().toLowerCase().contains(kw));
                })
                .filter(l -> {
                    if (minPrice == null) return true;

                    if (l.getPricingType() == PricingType.RANGE) {
                        return l.getMaxPrice() >= minPrice;
                    } else {
                        return l.getPrice() >= minPrice;
                    }
                })
                .filter(l -> {
                    if (maxPrice == null) return true;

                    if (l.getPricingType() == PricingType.RANGE) {
                        return l.getMinPrice() <= maxPrice;
                    } else {
                        return l.getPrice() <= maxPrice;
                    }
                })
                .toList();
    }
}
