package edu.cit.olodin.nearu.mobile.feature.rating

data class RatingSummaryResponse(
    val listingId: Long,
    val averageRating: Double,
    val ratingCount: Long
)