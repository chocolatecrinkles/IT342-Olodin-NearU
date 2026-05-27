package edu.cit.olodin.nearu.mobile.feature.businessowner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.adapter.ListingAdapter
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.feature.listing.ListingRequest
import edu.cit.olodin.nearu.mobile.feature.listing.ListingDetailActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusinessOwnerListingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private var allListings = listOf<ListingRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_owner_listings)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.ownerListingsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("ALL"))
        tabLayout.addTab(tabLayout.newTab().setText("ACCOMMODATION"))
        tabLayout.addTab(tabLayout.newTab().setText("SERVICE"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                filterListings()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fetchListings()
    }

    private fun fetchListings() {
        RetrofitClient.listingApi.getMyListings()
            .enqueue(object : Callback<List<ListingRequest>> {
                override fun onResponse(call: Call<List<ListingRequest>>, response: Response<List<ListingRequest>>) {
                    if (response.isSuccessful) {
                        allListings = response.body() ?: emptyList()
                        filterListings()
                    } else {
                        Toast.makeText(
                            this@BusinessOwnerListingsActivity,
                            "Failed to load listings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                override fun onFailure(call: Call<List<ListingRequest>>, t: Throwable) {
                    Toast.makeText(
                        this@BusinessOwnerListingsActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun filterListings() {
        val selectedType = tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text.toString()

        val filteredListings = if (selectedType == "ALL") {
            allListings
        } else {
            allListings.filter { it.listingType == selectedType }
        }

        // Feature: Maintained navigation to detail activity
        val adapter = ListingAdapter(filteredListings.toMutableList()) { listing ->
            val intent = Intent(this, ListingDetailActivity::class.java).apply {
                putExtra("id", listing.id)
                putExtra("name", listing.name)
                putExtra("address", listing.address)
                putExtra("price", listing.price?.toString())
                putExtra("description", listing.description)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }
}