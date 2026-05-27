package edu.cit.olodin.nearu.mobile.feature.listing

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import edu.cit.olodin.nearu.mobile.R

class GalleryOverlayActivity : AppCompatActivity() {

    private lateinit var galleryImageView: ImageView
    private lateinit var closeBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var counterText: TextView

    private var images: ArrayList<ListingImage> = arrayListOf()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_overlay)

        galleryImageView = findViewById(R.id.galleryImageView)
        closeBtn = findViewById(R.id.closeBtn)
        prevBtn = findViewById(R.id.prevBtn)
        nextBtn = findViewById(R.id.nextBtn)
        counterText = findViewById(R.id.counterText)

        currentIndex = intent.getIntExtra("startIndex", 0)
        images = intent.getSerializableExtra("images") as? ArrayList<ListingImage> ?: arrayListOf()

        closeBtn.setOnClickListener { finish() }
        prevBtn.setOnClickListener { showPreviousImage() }
        nextBtn.setOnClickListener { showNextImage() }

        showImage()
    }

    private fun showImage() {
        if (images.isEmpty()) return

        val fullUrl = "http://10.0.2.2:8080" + images[currentIndex].imageUrl

        Glide.with(this)
            .load(fullUrl)
            .into(galleryImageView)

        counterText.text = "${currentIndex + 1} / ${images.size}"
    }

    private fun showPreviousImage() {
        if (images.isEmpty()) return

        currentIndex = if (currentIndex == 0) {
            images.size - 1
        } else {
            currentIndex - 1
        }

        showImage()
    }

    private fun showNextImage() {
        if (images.isEmpty()) return

        currentIndex = if (currentIndex == images.size - 1) {
            0
        } else {
            currentIndex + 1
        }

        showImage()
    }
}