package edu.cit.olodin.nearu.mobile.api

import edu.cit.olodin.nearu.mobile.model.BookmarkRequest
import retrofit2.Call
import retrofit2.http.*

interface BookmarkApi {

    @POST("/api/bookmarks")
    fun addBookmark(
        @Body body: Map<String, Long>
    ): Call<Void>

    @GET("/api/bookmarks")
    fun getBookmarks(): Call<List<BookmarkRequest>>

    @DELETE("/api/bookmarks/{id}")
    fun removeBookmark(
        @Path("id") id: Long
    ): Call<Void>
}