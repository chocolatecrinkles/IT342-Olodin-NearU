package edu.cit.olodin.nearu.mobile.shared.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.feature.listing.ListingImage

class GalleryGridAdapter(
    private val images: List<ListingImage>,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<GalleryGridAdapter.ViewHolder>() {

    private val visibleImages = images.take(4)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.galleryImage)
        val moreText: TextView = view.findViewById(R.id.moreImagesText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gallery_image, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = visibleImages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fullUrl = "http://10.0.2.2:8080" + visibleImages[position].imageUrl

        Glide.with(holder.itemView.context)
            .load(fullUrl)
            .into(holder.image)

        if (position == 3 && images.size > 4) {
            holder.moreText.visibility = View.VISIBLE
            holder.moreText.text = "+${images.size - 4}"
        } else {
            holder.moreText.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onImageClick(position)
        }
    }
}