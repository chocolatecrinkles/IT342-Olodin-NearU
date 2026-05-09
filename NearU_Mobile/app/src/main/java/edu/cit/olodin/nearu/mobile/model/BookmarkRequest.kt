package edu.cit.olodin.nearu.mobile.model

data class BookmarkRequest(
    val id: Long,
    val userId: Long,
    val listingId: Long
)