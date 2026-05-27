package edu.cit.olodin.nearu.mobile.feature.rating

import retrofit2.Call
import retrofit2.http.*

interface RatingApi {

    @POST("api/ratings/listings/{listingId}")
    fun createOrUpdateRating(
        @Path("listingId") listingId: Long,
        @Body request: RatingRequest
    ): Call<RatingResponse>

    @GET("api/ratings/listings/{listingId}")
    fun getRatingsByListing(
        @Path("listingId") listingId: Long
    ): Call<List<RatingResponse>>

    @GET("api/ratings/listings/{listingId}/summary")
    fun getRatingSummary(
        @Path("listingId") listingId: Long
    ): Call<RatingSummaryResponse>

    @GET("api/ratings/me")
    fun getMyRatings(): Call<List<RatingResponse>>

    @DELETE("api/ratings/{ratingId}")
    fun deleteRating(
        @Path("ratingId") ratingId: Long
    ): Call<Void>
}