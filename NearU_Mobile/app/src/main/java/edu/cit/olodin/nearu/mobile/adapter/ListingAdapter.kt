package edu.cit.olodin.nearu.mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.model.ListingRequest

class ListingAdapter(
    private var listings: MutableList<ListingRequest>,
    private val onClick: (ListingRequest) -> Unit
) : RecyclerView.Adapter<ListingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameText)
        val price: TextView = view.findViewById(R.id.priceText)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listings.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val listing = listings[position]

        holder.name.text = listing.name

        holder.price.text = formatPrice(listing)

        holder.itemView.setOnClickListener {
            onClick(listing)
        }
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

    fun updateList(newListings: List<ListingRequest>) {

        listings.clear()
        listings.addAll(newListings)

        notifyDataSetChanged()
    }
}