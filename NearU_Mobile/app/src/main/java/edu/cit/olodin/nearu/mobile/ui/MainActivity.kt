package edu.cit.olodin.nearu.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.adapter.ListingAdapter
import edu.cit.olodin.nearu.mobile.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.model.ListingImage
import edu.cit.olodin.nearu.mobile.model.ListingRequest
import edu.cit.olodin.nearu.mobile.model.BookmarkRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var mapRecyclerView: RecyclerView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var searchView: SearchView
    private lateinit var categorySpinner: Spinner
    private lateinit var minPriceSpinner: Spinner
    private lateinit var maxPriceSpinner: Spinner
    private lateinit var filterBtn: Button

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var detailImageView: ImageView
    private lateinit var selectedName: TextView
    private lateinit var selectedAddress: TextView
    private lateinit var selectedCategory: TextView
    private lateinit var selectedType: TextView
    private lateinit var selectedPrice: TextView
    private lateinit var descriptionText: TextView
    private lateinit var bookmarkBtn: ImageButton

    private var currentKeyword: String? = null
    private var currentListingId: Long = -1
    private var currentBookmarkId: Long = -1
    private var isBookmarked = false
    private val mapCenter = LatLng(10.3157, 123.88541)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapRecyclerView = findViewById(R.id.mapRecyclerView)
        mapRecyclerView.layoutManager = LinearLayoutManager(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        btnMenu.setOnClickListener { drawerLayout.openDrawer(androidx.core.view.GravityCompat.END) }

        categorySpinner = findViewById(R.id.categorySpinner)
        minPriceSpinner = findViewById(R.id.minPriceSpinner)
        maxPriceSpinner = findViewById(R.id.maxPriceSpinner)
        filterBtn = findViewById(R.id.filterBtn)
        searchView = findViewById(R.id.searchView)

        val bottomSheet: LinearLayout = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        detailImageView = findViewById(R.id.detailImageView)
        selectedName = findViewById(R.id.selectedName)
        selectedAddress = findViewById(R.id.selectedAddress)
        selectedCategory = findViewById(R.id.selectedCategory)
        selectedType = findViewById(R.id.selectedType)
        selectedPrice = findViewById(R.id.selectedPrice)
        descriptionText = findViewById(R.id.descriptionText)
        bookmarkBtn = findViewById(R.id.bookmarkBtn)

        bookmarkBtn.setOnClickListener { toggleBookmark() }

        setupSpinners()
        setupNavigation()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        filterBtn.setOnClickListener { applyFilters() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentKeyword = query
                applyFilters()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                currentKeyword = newText
                applyFilters()
                return true
            }
        })
    }

    private fun setupSpinners() {
        val categories = arrayOf("ALL", "BOARDING_HOUSE", "DORM", "RESTAURANT", "CAFE", "LAUNDROMAT", "OTHER")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)

        val priceOptions = arrayOf("Any") + (0..9).map { i -> (500 + i * 500).toString() }.toTypedArray()
        val priceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priceOptions)
        minPriceSpinner.adapter = priceAdapter
        maxPriceSpinner.adapter = priceAdapter
    }

    private fun setupNavigation() {
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> drawerLayout.closeDrawers()
                R.id.nav_bookmarks -> startActivity(Intent(this, BookmarkActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
                R.id.nav_logout -> {
                    getSharedPreferences("NearU", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        fetchListings(null, null, null, null)
    }

    private fun applyFilters() {
        val selectedCat = categorySpinner.selectedItem.toString()
        val category = if (selectedCat == "ALL") null else selectedCat
        val min = if (minPriceSpinner.selectedItem.toString() == "Any") null else minPriceSpinner.selectedItem.toString().toDoubleOrNull()
        val max = if (maxPriceSpinner.selectedItem.toString() == "Any") null else maxPriceSpinner.selectedItem.toString().toDoubleOrNull()

        fetchListings(currentKeyword, category, min, max)
    }

    private fun fetchListings(k: String?, c: String?, min: Double?, max: Double?) {
        RetrofitClient.listingApi.getListings(k, c, min, max).enqueue(object : Callback<List<ListingRequest>> {
            override fun onResponse(call: Call<List<ListingRequest>>, response: Response<List<ListingRequest>>) {
                if (response.isSuccessful) {
                    val listings = response.body() ?: emptyList()
                    setupMapMarkers(listings)
                    setupRecyclerView(listings)
                }
            }
            override fun onFailure(call: Call<List<ListingRequest>>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupMapMarkers(listings: List<ListingRequest>) {
        googleMap.clear()
        listings.forEach { listing ->
            if (listing.latitude != null && listing.longitude != null) {
                val marker = googleMap.addMarker(MarkerOptions()
                    .position(LatLng(listing.latitude, listing.longitude))
                    .title(listing.name))
                marker?.tag = listing
            }
        }
        googleMap.setOnMarkerClickListener { marker ->
            (marker.tag as? ListingRequest)?.let { navigateToDetail(it) }
            true
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapCenter, 13f))
    }

    private fun setupRecyclerView(listings: List<ListingRequest>) {
        mapRecyclerView.adapter = ListingAdapter(listings.toMutableList()) { navigateToDetail(it) }
    }

    private fun navigateToDetail(listing: ListingRequest) {
        val detailLayout: LinearLayout = findViewById(R.id.detailLayout)
        val listLayout: LinearLayout = findViewById(R.id.listLayout)

        detailLayout.visibility = android.view.View.VISIBLE
        listLayout.visibility = android.view.View.GONE

        selectedName.text = listing.name
        selectedAddress.text = listing.address
        selectedCategory.text = formatText(listing.category ?: "")
        selectedType.text = formatText(listing.listingType ?: "")
        selectedPrice.text = formatPrice(listing)
        descriptionText.text = listing.description ?: ""

        currentListingId = listing.id ?: -1
        currentBookmarkId = -1
        isBookmarked = false

        checkBookmarkStatus()

        listing.id?.let { fetchImages(it) }

        if (listing.latitude != null && listing.longitude != null) {
            val position = LatLng(listing.latitude, listing.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun formatText(text: String): String {
        return text.replace("_", " ")
            .lowercase()
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.titlecase() } }
    }

    private fun fetchImages(listingId: Long) {
        RetrofitClient.listingApi.getImages(listingId).enqueue(object : Callback<List<ListingImage>> {
            override fun onResponse(call: Call<List<ListingImage>>, response: Response<List<ListingImage>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val fullUrl = "http://10.0.2.2:8080" + response.body()!![0].imageUrl
                    Glide.with(this@MainActivity).load(fullUrl).into(detailImageView)
                }
            }
            override fun onFailure(call: Call<List<ListingImage>>, t: Throwable) {}
        })
    }

    private fun formatPrice(listing: ListingRequest): String {
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

    private fun checkBookmarkStatus() {
        if (currentListingId == -1L) return
        RetrofitClient.bookmarkApi.getBookmarks().enqueue(object : Callback<List<BookmarkRequest>> {
            override fun onResponse(call: Call<List<BookmarkRequest>>, response: Response<List<BookmarkRequest>>) {
                if (response.isSuccessful) {
                    val bookmarks = response.body() ?: emptyList()
                    val bookmark = bookmarks.find { it.listingId == currentListingId }
                    isBookmarked = bookmark != null
                    if (bookmark != null) {
                        currentBookmarkId = bookmark.id
                    }
                    updateBookmarkIcon()
                }
            }
            override fun onFailure(call: Call<List<BookmarkRequest>>, t: Throwable) {}
        })
    }

    private fun toggleBookmark() {
        if (currentListingId == -1L) return
        if (isBookmarked) {
            // Remove bookmark
            if (currentBookmarkId != -1L) {
                RetrofitClient.bookmarkApi.removeBookmark(currentBookmarkId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            isBookmarked = false
                            currentBookmarkId = -1
                            updateBookmarkIcon()
                        }
                    }
                    override fun onFailure(call: Call<Void>, t: Throwable) {}
                })
            }
        } else {
            // Add bookmark
            val body = mapOf("listingId" to currentListingId)
            RetrofitClient.bookmarkApi.addBookmark(body).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        isBookmarked = true
                        // We'll need to get the bookmark ID from response? The API returns void. But we need to store ID to delete later.
                        // Unfortunately the addBookmark returns Void. We could refetch or keep ID? But ListingDetailActivity also does similar: it fetches bookmarks again to get ID.
                        // We'll re-fetch bookmarks to get the ID.
                        checkBookmarkStatus()
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {}
            })
        }
    }

    private fun updateBookmarkIcon() {
        if (isBookmarked) {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark)
        } else {
            bookmarkBtn.setImageResource(R.drawable.ic_bookmark_border)
        }
    }
}