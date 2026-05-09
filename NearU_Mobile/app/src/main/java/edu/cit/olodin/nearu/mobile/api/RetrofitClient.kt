package edu.cit.olodin.nearu.mobile.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.OkHttpClient

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val listingApi: ListingApi by lazy {
        retrofit.create(ListingApi::class.java)
    }

    val bookmarkApi: BookmarkApi by lazy {
        retrofit.create(BookmarkApi::class.java)
    }
}