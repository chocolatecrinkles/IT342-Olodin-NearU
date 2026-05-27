package edu.cit.olodin.nearu.mobile.feature.rating

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.shared.util.ListingFormatUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewsActivity : AppCompatActivity() {

    private var listingId: Long = -1
    private var listingName: String = ""
    private var isOwnerView: Boolean = false

    private lateinit var titleText: TextView
    private lateinit var summaryText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var commentInput: EditText
    private lateinit var submitBtn: Button
    private lateinit var reviewsContainer: LinearLayout
    private lateinit var backBtn: ImageButton
    private lateinit var studentReviewSection: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        readIntentExtras()
        setupViews()
        setupListeners()
        fetchSummary()
        fetchReviews()
    }

    private fun readIntentExtras() {
        listingId = intent.getLongExtra("listingId", -1)
        listingName = intent.getStringExtra("listingName") ?: ""
        isOwnerView = intent.getBooleanExtra("isOwnerView", false)
    }

    private fun setupViews() {
        titleText = findViewById(R.id.titleText)
        summaryText = findViewById(R.id.summaryText)
        ratingBar = findViewById(R.id.ratingBar)
        commentInput = findViewById(R.id.commentInput)
        submitBtn = findViewById(R.id.submitBtn)
        reviewsContainer = findViewById(R.id.reviewsContainer)
        backBtn = findViewById(R.id.backBtn)
        studentReviewSection = findViewById(R.id.studentReviewSection)

        titleText.text = listingName
        studentReviewSection.visibility = if (isOwnerView) View.GONE else View.VISIBLE
    }

    private fun setupListeners() {
        backBtn.setOnClickListener { finish() }
        submitBtn.setOnClickListener { submitRating() }
    }

    private fun fetchSummary() {
        RetrofitClient.ratingApi.getRatingSummary(listingId)
            .enqueue(object : Callback<RatingSummaryResponse> {
                override fun onResponse(
                    call: Call<RatingSummaryResponse>,
                    response: Response<RatingSummaryResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val summary = response.body()!!
                        summaryText.text = ListingFormatUtils.formatRating(
                            summary.averageRating,
                            summary.ratingCount
                        )
                    }
                }

                override fun onFailure(call: Call<RatingSummaryResponse>, t: Throwable) {
                    Toast.makeText(this@ReviewsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun fetchReviews() {
        RetrofitClient.ratingApi.getRatingsByListing(listingId)
            .enqueue(object : Callback<List<RatingResponse>> {
                override fun onResponse(
                    call: Call<List<RatingResponse>>,
                    response: Response<List<RatingResponse>>
                ) {
                    if (response.isSuccessful) {
                        displayReviews(response.body() ?: emptyList())
                    }
                }

                override fun onFailure(call: Call<List<RatingResponse>>, t: Throwable) {
                    Toast.makeText(this@ReviewsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun displayReviews(reviews: List<RatingResponse>) {
        reviewsContainer.removeAllViews()

        if (reviews.isEmpty()) {
            reviewsContainer.addView(TextView(this).apply {
                text = "No reviews yet."
                textSize = 14f
                setTextColor(ContextCompat.getColor(this@ReviewsActivity, R.color.nearu_text_muted))
            })
            return
        }

        reviews.forEach { review ->
            reviewsContainer.addView(createReviewCard(review))
        }
    }

    private fun createReviewCard(review: RatingResponse): CardView {
        val card = CardView(this).apply {
            radius = resources.getDimension(R.dimen.nearu_card_radius)
            cardElevation = resources.getDimension(R.dimen.nearu_card_elevation)
            setCardBackgroundColor(ContextCompat.getColor(this@ReviewsActivity, R.color.nearu_surface))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(R.dimen.nearu_section_gap)
            }
        }

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val padding = resources.getDimensionPixelSize(R.dimen.nearu_card_padding)
            setPadding(padding, padding, padding, padding)
        }

        val ratingText = TextView(this).apply {
            text = ListingFormatUtils.formatRatingValue(review.rating)
            textSize = 13f
            setTextColor(ContextCompat.getColor(this@ReviewsActivity, R.color.nearu_secondary))
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0)
            compoundDrawablePadding = 6
        }

        val userText = TextView(this).apply {
            text = review.userName
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@ReviewsActivity, R.color.nearu_text_main))
            setTypeface(typeface, Typeface.BOLD)
        }

        val commentText = TextView(this).apply {
            text = review.comment?.takeIf { it.isNotBlank() } ?: "No comment"
            textSize = 14f
            setTextColor(ContextCompat.getColor(this@ReviewsActivity, R.color.nearu_text_muted))
        }

        content.addView(ratingText)
        content.addView(userText)
        content.addView(commentText)
        card.addView(content)
        return card
    }

    private fun submitRating() {
        val ratingValue = ratingBar.rating.toInt()
        val comment = commentInput.text.toString().trim()

        if (ratingValue < 1) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show()
            return
        }

        val request = RatingRequest(
            rating = ratingValue,
            comment = comment.ifEmpty { null }
        )

        RetrofitClient.ratingApi.createOrUpdateRating(listingId, request)
            .enqueue(object : Callback<RatingResponse> {
                override fun onResponse(
                    call: Call<RatingResponse>,
                    response: Response<RatingResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ReviewsActivity, "Review submitted", Toast.LENGTH_SHORT).show()
                        commentInput.text.clear()
                        ratingBar.rating = 0f
                        fetchSummary()
                        fetchReviews()
                    } else {
                        Toast.makeText(this@ReviewsActivity, "Failed to submit review", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RatingResponse>, t: Throwable) {
                    Toast.makeText(this@ReviewsActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
