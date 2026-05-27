package edu.cit.olodin.nearu.mobile.feature.rating

data class RatingResponse(
    val id: Long,
    val listingId: Long,
    val listingName: String,
    val userId: Long,
    val userName: String,
    val rating: Int,
    val comment: String?,
    val createdAt: String?
)