package edu.cit.olodin.nearu.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.adapter.ListingAdapter
import edu.cit.olodin.nearu.mobile.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.model.ListingRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusinessOwnerMainActivity : AppCompatActivity(), OnMapReadyCallback {

    val mapCenter = LatLng(10.3157, 123.88541)

    private lateinit var googleMap: GoogleMap

    private lateinit var mapRecyclerView: RecyclerView
    private lateinit var emptyText: TextView

    private lateinit var selectedName: TextView
    private lateinit var selectedAddress: TextView
    private lateinit var selectedPrice: TextView

    private lateinit var viewDetailsBtn: Button
    private lateinit var createListingBtn: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private lateinit var bottomSheetBehavior:
            BottomSheetBehavior<LinearLayout>

    private var selectedListing: ListingRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_owner_main)

        val toolbar =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)

        toolbar.setNavigationIcon(R.drawable.ic_menu)

        toolbar.setNavigationOnClickListener {

            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        mapRecyclerView = findViewById(R.id.mapRecyclerView)
        emptyText = findViewById(R.id.emptyText)

        selectedName = findViewById(R.id.selectedName)
        selectedAddress = findViewById(R.id.selectedAddress)
        selectedPrice = findViewById(R.id.selectedPrice)

        viewDetailsBtn = findViewById(R.id.viewDetailsBtn)
        createListingBtn = findViewById(R.id.createListingBtn)

        mapRecyclerView.layoutManager =
            LinearLayoutManager(this)

        val bottomSheet =
            findViewById<LinearLayout>(R.id.bottomSheet)

        bottomSheetBehavior =
            BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.state =
            BottomSheetBehavior.STATE_EXPANDED

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        createListingBtn.setOnClickListener {

            startActivity(
                Intent(this, AddListingActivity::class.java)
            )
        }

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    drawerLayout.closeDrawer(GravityCompat.END)
                }

                R.id.nav_listings -> {

                    startActivity(
                        Intent(
                            this,
                            BusinessOwnerListingsActivity::class.java
                        )
                    )
                    drawerLayout.closeDrawer(GravityCompat.END)
                }

                R.id.nav_profile -> {

                    startActivity(
                        Intent(
                            this,
                            ProfileActivity::class.java
                        )
                    )
                }

                R.id.nav_logout -> {

                    val prefs =
                        getSharedPreferences("NearU", MODE_PRIVATE)

                    prefs.edit().clear().apply()

                    startActivity(
                        Intent(this, LoginActivity::class.java)
                    )

                    finish()
                }
            }

            true
        }
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        fetchListings()
    }

    private fun fetchListings() {

        RetrofitClient.listingApi
            .getMyListings()
            .enqueue(object : Callback<List<ListingRequest>> {

                override fun onResponse(
                    call: Call<List<ListingRequest>>,
                    response: Response<List<ListingRequest>>
                ) {

                    if (response.isSuccessful) {

                        val listings =
                            response.body() ?: emptyList()

                        setupMapMarkers(listings)
                        setupRecycler(listings)

                    }
                }

                override fun onFailure(
                    call: Call<List<ListingRequest>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@BusinessOwnerMainActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun setupRecycler(
        listings: List<ListingRequest>
    ) {

        if (listings.isEmpty()) {

            emptyText.visibility = android.view.View.VISIBLE
            mapRecyclerView.visibility = android.view.View.GONE

        } else {

            emptyText.visibility = android.view.View.GONE
            mapRecyclerView.visibility = android.view.View.VISIBLE
        }

        mapRecyclerView.adapter =
            ListingAdapter(listings.toMutableList()) { listing ->

                selectListing(listing)
            }
    }

    private fun setupMapMarkers(
        listings: List<ListingRequest>
    ) {

        googleMap.clear()

        listings.forEach { listing ->

            val lat = listing.latitude
            val lng = listing.longitude

            if (lat != null && lng != null) {

                val position = LatLng(lat, lng)

                val marker =
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(listing.name)
                    )

                marker?.tag = listing
            }
        }

        googleMap.setOnMarkerClickListener { marker ->

            val listing =
                marker.tag as? ListingRequest

            if (listing != null) {
                selectListing(listing)
            }

            false
        }

        if (listings.isNotEmpty()) {

            val first = listings.first()

            if (first.latitude != null &&
                first.longitude != null
            ) {

                googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        mapCenter,
                        14f
                    )
                )
            }
        }
    }

    private fun selectListing(listing: ListingRequest) {
        selectedListing = listing

        findViewById<LinearLayout>(R.id.selectedListingHeader).visibility = android.view.View.VISIBLE
        findViewById<LinearLayout>(R.id.allListingsContainer).visibility = android.view.View.GONE
        findViewById<Button>(R.id.createListingBtn).visibility = android.view.View.GONE

        selectedName.text = listing.name
        selectedAddress.text = listing.address
        selectedPrice.text = "₱ ${listing.price}"

        val imageView = findViewById<ImageView>(R.id.selectedImage)

        if (listing.latitude != null && listing.longitude != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(listing.latitude, listing.longitude), 16f))
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onBackPressed() {
        val detailView = findViewById<LinearLayout>(R.id.selectedListingHeader)
        if (detailView.visibility == android.view.View.VISIBLE) {
            detailView.visibility = android.view.View.GONE
            findViewById<LinearLayout>(R.id.allListingsContainer).visibility = android.view.View.VISIBLE
            findViewById<Button>(R.id.createListingBtn).visibility = android.view.View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}