package edu.cit.olodin.nearu.mobile.model

data class ListingRequest(
    val id: Long? = null,
    val name: String,
    val category: String,
    val listingType: String,
    val address: String,
    val price: Double?,
    val minPrice: Double?,
    val maxPrice: Double?,
    val pricingType: String,
    val latitude: Double?,
    val longitude: Double?,
    val description: String?
)