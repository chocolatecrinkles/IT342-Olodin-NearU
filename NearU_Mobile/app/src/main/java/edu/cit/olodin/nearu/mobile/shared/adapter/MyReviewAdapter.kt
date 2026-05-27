package edu.cit.olodin.nearu.mobile.shared.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.feature.rating.RatingResponse
import edu.cit.olodin.nearu.mobile.shared.util.ListingFormatUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyReviewAdapter(
    private var reviews: List<RatingResponse>,
    private val onDeleteClick: (Long) -> Unit
) : RecyclerView.Adapter<MyReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val listingNameText: TextView = view.findViewById(R.id.listingNameText)
        val ratingText: TextView = view.findViewById(R.id.ratingText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val commentText: TextView = view.findViewById(R.id.commentText)
        val deleteBtn: Button = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_review, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = reviews.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]

        holder.listingNameText.text = review.listingName
        holder.ratingText.text = ListingFormatUtils.formatRatingValue(review.rating)
        holder.dateText.text = formatDate(review.createdAt)
        holder.commentText.text = review.comment?.takeIf { it.isNotBlank() } ?: "No comment"
        holder.deleteBtn.setOnClickListener {
            onDeleteClick(review.id)
        }
    }

    fun updateReviews(newReviews: List<RatingResponse>) {
        reviews = newReviews
        notifyDataSetChanged()
    }

    private fun formatDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) return "Date unavailable"

        return try {
            val parsedDate = LocalDateTime.parse(rawDate)
            parsedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
        } catch (e: Exception) {
            rawDate.substringBefore("T")
        }
    }
}
