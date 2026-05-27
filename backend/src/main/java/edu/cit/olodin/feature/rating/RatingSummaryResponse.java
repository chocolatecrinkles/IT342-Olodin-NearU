package edu.cit.olodin.feature.rating;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RatingSummaryResponse {
    private Long listingId;
    private Double averageRating;
    private Long ratingCount;
}