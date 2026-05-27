package edu.cit.olodin.nearu.mobile.feature.rating

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.feature.auth.LoginActivity
import edu.cit.olodin.nearu.mobile.feature.auth.ProfileActivity
import edu.cit.olodin.nearu.mobile.feature.bookmark.BookmarkActivity
import edu.cit.olodin.nearu.mobile.feature.student.StudentMainActivity
import edu.cit.olodin.nearu.mobile.shared.adapter.MyReviewAdapter
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyReviewsActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: MyReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_reviews)

        setupViews()
        setupNavigation()
        fetchMyReviews()
    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)
        emptyText = findViewById(R.id.emptyText)

        val btnMenu: ImageButton = findViewById(R.id.btnMenu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        adapter = MyReviewAdapter(emptyList()) { ratingId ->
            deleteReview(ratingId)
        }

        reviewsRecyclerView.layoutManager = LinearLayoutManager(this)
        reviewsRecyclerView.adapter = adapter
    }

    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> openStudentDrawerDestination(StudentMainActivity::class.java)
                R.id.nav_bookmarks -> openStudentDrawerDestination(BookmarkActivity::class.java)
                R.id.nav_reviews -> drawerLayout.closeDrawers()
                R.id.nav_profile -> openStudentDrawerDestination(ProfileActivity::class.java)
                R.id.nav_logout -> {
                    getSharedPreferences("NearU", MODE_PRIVATE).edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            true
        }
    }

    private fun openStudentDrawerDestination(destination: Class<*>) {
        drawerLayout.closeDrawer(GravityCompat.END)
        drawerLayout.post {
            startActivity(Intent(this, destination))
            finish()
        }
    }

    private fun fetchMyReviews() {
        RetrofitClient.ratingApi.getMyRatings()
            .enqueue(object : Callback<List<RatingResponse>> {
                override fun onResponse(
                    call: Call<List<RatingResponse>>,
                    response: Response<List<RatingResponse>>
                ) {
                    if (response.isSuccessful) {
                        val reviews = response.body() ?: emptyList()
                        showReviews(reviews)
                    } else {
                        Toast.makeText(
                            this@MyReviewsActivity,
                            "Failed to load reviews",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<RatingResponse>>, t: Throwable) {
                    Toast.makeText(this@MyReviewsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showReviews(reviews: List<RatingResponse>) {
        emptyText.visibility = if (reviews.isEmpty()) View.VISIBLE else View.GONE
        reviewsRecyclerView.visibility = if (reviews.isEmpty()) View.GONE else View.VISIBLE
        adapter.updateReviews(reviews)
    }

    private fun deleteReview(ratingId: Long) {
        RetrofitClient.ratingApi.deleteRating(ratingId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MyReviewsActivity,
                            "Review deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        fetchMyReviews()
                    } else {
                        Toast.makeText(
                            this@MyReviewsActivity,
                            "Delete failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@MyReviewsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
