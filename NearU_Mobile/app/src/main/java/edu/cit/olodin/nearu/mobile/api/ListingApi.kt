package edu.cit.olodin.nearu.mobile.api

import edu.cit.olodin.nearu.mobile.model.ListingImage
import edu.cit.olodin.nearu.mobile.model.ListingRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ListingApi {

    @GET("/api/listings")
    fun getListings(
        @Query("keyword") keyword: String?,
        @Query("category") category: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?
    ): Call<List<ListingRequest>>

    @GET("api/listings/my")
    fun getMyListings(): Call<List<ListingRequest>>


    @POST("api/listings")
    fun createListing(
        @Body request: ListingRequest
    ): Call<ListingRequest>

    @GET("/api/listings/{id}/images")
    fun getImages(@Path("id") id: Long): Call<List<ListingImage>>
}