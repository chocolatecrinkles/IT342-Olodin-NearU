package edu.cit.olodin.nearu.mobile.feature.listing

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.feature.bookmark.BookmarkRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListingDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var imageView: ImageView
    private lateinit var nameText: TextView
    private lateinit var addressText: TextView
    private lateinit var priceText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var bookmarkBtn: ImageButton

    private var isBookmarked = false
    private var bookmarkId: Long = -1
    private var listingId: Long = -1

    private lateinit var googleMap: GoogleMap
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_detail)

        imageView = findViewById(R.id.imageView)
        nameText = findViewById(R.id.nameText)
        addressText = findViewById(R.id.addressText)
        priceText = findViewById(R.id.priceText)
        descriptionText = findViewById(R.id.descriptionText)
        bookmarkBtn = findViewById(R.id.bookmarkBtn)

        listingId = intent.getLongExtra("id", -1)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.detailMapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (isBookmarked) {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark)
        } else {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border)
        }

        checkBookmarkStatus()
        fetchListing()
        fetchImages()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // If lat/lng are already set (from fetchListing), show them; otherwise show default
        val location = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(location).title(nameText.text.toString()))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
        googleMap.uiSettings.isScrollGesturesEnabled = false
    }

    private fun fetchListing() {
        RetrofitClient.listingApi.getListings(null, null, null, null).enqueue(object : Callback<List<ListingRequest>> {
            override fun onResponse(call: Call<List<ListingRequest>>, response: Response<List<ListingRequest>>) {
                if (response.isSuccessful) {
                    val listings = response.body() ?: emptyList()
                    val found = listings.find { it.id == listingId }
                    found?.let { listing ->
                        nameText.text = listing.name
                        addressText.text = listing.address
                        priceText.text = formatPrice(listing)
                        descriptionText.text = listing.description ?: ""
                        listing.latitude?.let { lat = it }
                        listing.longitude?.let { lng = it }
                        // Update map with actual location
                        if (::googleMap.isInitialized) {
                            val location = LatLng(lat, lng)
                            googleMap.clear()
                            googleMap.addMarker(MarkerOptions().position(location).title(listing.name))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<ListingRequest>>, t: Throwable) {}
        })
    }

    private fun fetchImages() {
        RetrofitClient.listingApi.getImages(listingId).enqueue(object : Callback<List<ListingImage>> {
            override fun onResponse(call: Call<List<ListingImage>>, response: Response<List<ListingImage>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val image = response.body()!![0]
                    val fullUrl = "http://10.0.2.2:8080" + image.imageUrl
                    Glide.with(this@ListingDetailActivity).load(fullUrl).into(imageView)
                }
            }
            override fun onFailure(call: Call<List<ListingImage>>, t: Throwable) {
                Toast.makeText(this@ListingDetailActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addBookmark() {

        val body = mapOf(
            "listingId" to listingId
        )

        RetrofitClient.bookmarkApi.addBookmark(body)
            .enqueue(object : Callback<Void> {

                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                    if (response.isSuccessful) {

                        isBookmarked = true

                        Toast.makeText(
                            this@ListingDetailActivity,
                            "Bookmarked",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ListingDetailActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun removeBookmark() {
        RetrofitClient.bookmarkApi.removeBookmark(bookmarkId)
            .enqueue(object : Callback<Void> {

                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        isBookmarked = false
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ListingDetailActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun checkBookmarkStatus() {

        RetrofitClient.bookmarkApi.getBookmarks()
            .enqueue(object : Callback<List<BookmarkRequest>> {

                override fun onResponse(
                    call: Call<List<BookmarkRequest>>,
                    response: Response<List<BookmarkRequest>>
                ) {

                    if (response.isSuccessful) {

                        val bookmarks = response.body() ?: emptyList()

                        val bookmark = bookmarks.find {
                            it.listingId == listingId
                        }

                        isBookmarked = bookmark != null

                        if (bookmark != null) {
                            bookmarkId = bookmark.id
                        }
                    }
                }

                override fun onFailure(call: Call<List<BookmarkRequest>>, t: Throwable) {}
            })
    }

    fun formatPrice(listing: ListingRequest): String {
        fun Double?.toCleanIntString(): String = this?.toInt()?.toString() ?: ""

        return when (listing.pricingType) {
            "RANGE" -> {
                val min = listing.minPrice.toCleanIntString()
                val max = listing.maxPrice.toCleanIntString()
                if (min.isNotEmpty() && max.isNotEmpty()) "₱$min - ₱$max" else "Price on Request"
            }
            else -> {
                val price = listing.price.toCleanIntString()
                if (price.isNotEmpty()) "₱$price" else "Free / TBD"
            }
        }
    }
}