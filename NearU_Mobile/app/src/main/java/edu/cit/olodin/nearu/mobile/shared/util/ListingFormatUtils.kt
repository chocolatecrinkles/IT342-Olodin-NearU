package edu.cit.olodin.nearu.mobile.shared.util

import edu.cit.olodin.nearu.mobile.feature.listing.ListingRequest

object ListingFormatUtils {
    private const val PESO = "\u20B1"

    fun formatText(text: String?): String {
        if (text.isNullOrBlank()) return "Not specified"

        return text.replace("_", " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") {
                it.replaceFirstChar { char -> char.titlecase() }
            }
    }

    fun formatPrice(listing: ListingRequest): String {
        fun Double?.toCleanString(): String = this?.let {
            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
        } ?: ""

        return when (listing.pricingType) {
            "RANGE" -> {
                val min = listing.minPrice.toCleanString()
                val max = listing.maxPrice.toCleanString()

                if (min.isNotEmpty() && max.isNotEmpty()) {
                    "$PESO$min - $PESO$max"
                } else {
                    "Price on request"
                }
            }

            else -> {
                val price = listing.price.toCleanString()

                if (price.isNotEmpty()) {
                    "$PESO$price"
                } else {
                    "Price not set"
                }
            }
        }
    }

    fun formatRating(averageRating: Double?, ratingCount: Long?): String {
        val rating = averageRating?.let {
            if (it % 1.0 == 0.0) it.toInt().toString() else String.format("%.1f", it)
        } ?: "0"
        val count = ratingCount ?: 0

        return "$rating / 5 rating ($count reviews)"
    }

    fun formatRatingValue(rating: Int?): String {
        return "${rating ?: 0} / 5"
    }
}
