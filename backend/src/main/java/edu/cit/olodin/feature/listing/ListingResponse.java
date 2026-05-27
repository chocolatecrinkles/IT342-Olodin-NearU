package edu.cit.olodin.feature.listing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListingResponse {
    private Long id;
    private String name;
    private Category category;
    private Type listingType;
    private String address;
    private Double price;
    private Double minPrice;
    private Double maxPrice;
    private PricingType pricingType;
    private Double latitude;
    private Double longitude;
    private String description;
    private Long ownerId;

    private Double averageRating;
    private Long ratingCount;
}