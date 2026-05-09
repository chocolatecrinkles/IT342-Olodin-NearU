package edu.cit.olodin.nearu.mobile.feature.bookmark

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
import edu.cit.olodin.nearu.mobile.ui.MainActivity
import edu.cit.olodin.nearu.mobile.feature.auth.ProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookmarkActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: com.google.android.material.navigation.NavigationView

    private var allBookmarkedListings = listOf<ListingRequest>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarks)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        val btnMenu: android.widget.ImageButton = findViewById(R.id.btnMenu)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.END)
        }

        setupNavigation()

        recyclerView = findViewById(R.id.bookmarkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("ALL"))
        tabLayout.addTab(tabLayout.newTab().setText("ACCOMMODATION"))
        tabLayout.addTab(tabLayout.newTab().setText("SERVICE"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) { filterListings() }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        fetchBookmarks()
    }

    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
                R.id.nav_bookmarks -> drawerLayout.closeDrawers() // Already here
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.END)
            true
        }
    }

    private fun fetchBookmarks() {
        RetrofitClient.bookmarkApi.getBookmarks()
            .enqueue(object : Callback<List<BookmarkRequest>> {
                override fun onResponse(call: Call<List<BookmarkRequest>>, response: Response<List<BookmarkRequest>>) {
                    if (response.isSuccessful) {
                        val listingIds = response.body()?.map { it.listingId } ?: emptyList()
                        fetchListings(listingIds)
                    }
                }
                override fun onFailure(call: Call<List<BookmarkRequest>>, t: Throwable) {
                    Toast.makeText(this@BookmarkActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchListings(bookmarkIds: List<Long>) {
        RetrofitClient.listingApi.getListings(null, null, null, null)
            .enqueue(object : Callback<List<ListingRequest>> {
                override fun onResponse(call: Call<List<ListingRequest>>, response: Response<List<ListingRequest>>) {
                    if (response.isSuccessful) {
                        val listings = response.body() ?: emptyList()
                        allBookmarkedListings = listings.filter { bookmarkIds.contains(it.id) }
                        filterListings()
                    }
                }
                override fun onFailure(call: Call<List<ListingRequest>>, t: Throwable) {
                    Toast.makeText(this@BookmarkActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun filterListings() {
        val selectedType = tabLayout.getTabAt(tabLayout.selectedTabPosition)?.text.toString()
        val filteredListings = if (selectedType == "ALL") allBookmarkedListings
        else allBookmarkedListings.filter { it.listingType == selectedType }

        recyclerView.adapter = ListingAdapter(filteredListings.toMutableList()) { listing ->
            val intent = Intent(this, ListingDetailActivity::class.java).apply {
                putExtra("id", listing.id)
                putExtra("name", listing.name)
                putExtra("address", listing.address)
                putExtra("price", listing.price?.toString())
                putExtra("description", listing.description)
            }
            startActivity(intent)
        }
    }
}