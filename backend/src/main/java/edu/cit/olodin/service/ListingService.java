package edu.cit.olodin.service;

import edu.cit.olodin.dto.ListingRequest;
import edu.cit.olodin.entity.*;
import edu.cit.olodin.exception.AuthException;
import edu.cit.olodin.repository.ListingImageRepository;
import edu.cit.olodin.repository.ListingRepository;
import edu.cit.olodin.repository.UserRepository;
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

    public Listing createListing(ListingRequest req) {

        User user = getCurrentUser();
        System.out.println("User: " + user.getId());

        Listing listing = new Listing();
        listing.setName(req.name);
        listing.setCategory(Category.valueOf(req.category));
        listing.setListingType(Type.valueOf(req.listingType));
        listing.setAddress(req.address);
        listing.setPrice(req.price);
        listing.setLatitude(req.latitude);
        listing.setLongitude(req.longitude);
        listing.setDescription(req.description);

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

    public List<String> uploadImages(Long listingId, List<MultipartFile> files) {

        if (files.size() > 15) {
            throw new RuntimeException("Maximum 15 images allowed");
        }

        List<String> uploadedPaths = new ArrayList<>();

        for (MultipartFile file : files) {

            if (!file.getContentType().startsWith("image/")) {
                throw new RuntimeException("Only image files allowed");
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
                throw new RuntimeException("Upload failed");
            }
        }

        return uploadedPaths;
    }

    public List<ListingImage> getImagesByListing(Long listingId) {
        return listingImageRepository.findByListingId(listingId);
    }

    public List<Listing> getFilteredListings(String category, Double minPrice, Double maxPrice) {
        List<Listing> listings = listingRepository.findAll();

        return listings.stream()
                .filter(l -> category == null || l.getCategory().name().equalsIgnoreCase(category))
                .filter(l -> minPrice == null || l.getPrice() >= minPrice)
                .filter(l -> maxPrice == null || l.getPrice() <= maxPrice)
                .toList();
    }
}
