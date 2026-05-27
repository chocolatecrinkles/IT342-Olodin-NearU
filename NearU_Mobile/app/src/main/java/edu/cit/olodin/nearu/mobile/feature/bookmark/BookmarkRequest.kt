package edu.cit.olodin.nearu.mobile.feature.bookmark

data class BookmarkRequest(
    val id: Long,
    val userId: Long,
    val listingId: Long
)