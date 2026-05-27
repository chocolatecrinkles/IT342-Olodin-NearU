package edu.cit.olodin.nearu.mobile.feature.listing

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream

class EditListingActivity : AppCompatActivity(), OnMapReadyCallback {

    private var listingId: Long = -1

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var descriptionInput: EditText

    private lateinit var typeSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var pricingSpinner: Spinner

    private lateinit var priceInput: EditText
    private lateinit var minPriceInput: EditText
    private lateinit var maxPriceInput: EditText

    private lateinit var saveBtn: Button

    private lateinit var priceContainer: LinearLayout
    private lateinit var rangeContainer: LinearLayout

    private lateinit var googleMap: GoogleMap

    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private lateinit var selectImagesBtn: ImageButton
    private lateinit var imageCountText: TextView
    private val imageUris = mutableListOf<Uri>()

    private val typeOptions = listOf("ACCOMMODATION", "SERVICE", "OTHER")

    private val categoryOptions = mapOf(
        "ACCOMMODATION" to listOf("BOARDING_HOUSE", "DORM"),
        "SERVICE" to listOf("RESTAURANT", "CAFE", "LAUNDROMAT"),
        "OTHER" to listOf("OTHER")
    )

    private val pricingOptions = mapOf(
        "ACCOMMODATION" to listOf("MONTHLY", "WEEKLY"),
        "SERVICE" to listOf("RANGE"),
        "OTHER" to listOf("RANGE")
    )

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            imageUris.clear()
            imageUris.addAll(uris)
            imageCountText.text = "${imageUris.size} new images selected"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_listing)

        listingId = intent.getLongExtra("id", -1)
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
            finish()
        }

        nameInput = findViewById(R.id.nameInput)
        addressInput = findViewById(R.id.addressInput)
        descriptionInput = findViewById(R.id.descriptionInput)

        typeSpinner = findViewById(R.id.typeSpinner)
        categorySpinner = findViewById(R.id.categorySpinner)
        pricingSpinner = findViewById(R.id.pricingSpinner)

        priceContainer = findViewById(R.id.priceContainer)
        rangeContainer = findViewById(R.id.rangeContainer)

        priceInput = findViewById(R.id.priceInput)
        minPriceInput = findViewById(R.id.minPriceInput)
        maxPriceInput = findViewById(R.id.maxPriceInput)

        selectImagesBtn = findViewById(R.id.selectImagesBtn)
        imageCountText = findViewById(R.id.imageCountText)

        imageCountText.text = "Tap image box to add new photos"

        selectImagesBtn.setOnClickListener {
            imagePicker.launch("image/*")
        }

        saveBtn = findViewById(R.id.createBtn)

        saveBtn.text = "SAVE CHANGES"

        selectedLatitude = intent.getDoubleExtra("latitude", 0.0)
        selectedLongitude = intent.getDoubleExtra("longitude", 0.0)

        setupSpinners()
        populateFields()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        mapFragment.getMapAsync(this)

        saveBtn.setOnClickListener {
            updateListing()
        }
    }

    private fun setupSpinners() {
        typeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            typeOptions
        )

        typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedType = typeSpinner.selectedItem.toString()

                    val categories = categoryOptions[selectedType] ?: emptyList()
                    val pricing = pricingOptions[selectedType] ?: emptyList()

                    categorySpinner.adapter = ArrayAdapter(
                        this@EditListingActivity,
                        android.R.layout.simple_spinner_item,
                        categories
                    )

                    pricingSpinner.adapter = ArrayAdapter(
                        this@EditListingActivity,
                        android.R.layout.simple_spinner_item,
                        pricing
                    )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        pricingSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val pricing = pricingSpinner.selectedItem.toString()

                    if (pricing == "RANGE") {
                        priceContainer.visibility = View.GONE
                        rangeContainer.visibility = View.VISIBLE
                    } else {
                        priceContainer.visibility = View.VISIBLE
                        rangeContainer.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun populateFields() {
        val listingType = intent.getStringExtra("listingType") ?: "ACCOMMODATION"
        val category = intent.getStringExtra("category") ?: "BOARDING_HOUSE"
        val pricingType = intent.getStringExtra("pricingType") ?: "MONTHLY"

        nameInput.setText(intent.getStringExtra("name") ?: "")
        addressInput.setText(intent.getStringExtra("address") ?: "")
        descriptionInput.setText(intent.getStringExtra("description") ?: "")

        val price = intent.getDoubleExtra("price", -1.0)
        val minPrice = intent.getDoubleExtra("minPrice", -1.0)
        val maxPrice = intent.getDoubleExtra("maxPrice", -1.0)

        if (price > 0) priceInput.setText(price.toInt().toString())
        if (minPrice > 0) minPriceInput.setText(minPrice.toInt().toString())
        if (maxPrice > 0) maxPriceInput.setText(maxPrice.toInt().toString())

        typeSpinner.post {
            typeSpinner.setSelection(typeOptions.indexOf(listingType).coerceAtLeast(0))

            categorySpinner.post {
                val categories = categoryOptions[listingType] ?: emptyList()
                categorySpinner.setSelection(categories.indexOf(category).coerceAtLeast(0))

                pricingSpinner.post {
                    val pricing = pricingOptions[listingType] ?: emptyList()
                    pricingSpinner.setSelection(pricing.indexOf(pricingType).coerceAtLeast(0))
                }
            }
        }
    }

    private fun updateListing() {
        if (listingId == -1L) {
            Toast.makeText(this, "Invalid listing", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedLatitude == null || selectedLongitude == null) {
            Toast.makeText(this, "Please select a location on the map", Toast.LENGTH_SHORT).show()
            return
        }

        val pricingType = pricingSpinner.selectedItem.toString()

        val request = ListingRequest(
            id = listingId,
            name = nameInput.text.toString().trim(),
            category = categorySpinner.selectedItem.toString(),
            listingType = typeSpinner.selectedItem.toString(),
            address = addressInput.text.toString().trim(),
            price = if (pricingType == "RANGE") null else priceInput.text.toString().toDoubleOrNull(),
            minPrice = if (pricingType == "RANGE") minPriceInput.text.toString().toDoubleOrNull() else null,
            maxPrice = if (pricingType == "RANGE") maxPriceInput.text.toString().toDoubleOrNull() else null,
            pricingType = pricingType,
            latitude = selectedLatitude,
            longitude = selectedLongitude,
            description = descriptionInput.text.toString().trim()
        )

        RetrofitClient.listingApi
            .updateListing(listingId, request)
            .enqueue(object : Callback<ListingRequest> {
                override fun onResponse(
                    call: Call<ListingRequest>,
                    response: Response<ListingRequest>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditListingActivity,
                            "Listing updated",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (imageUris.isNotEmpty()) {
                            uploadNewImages()
                        } else {
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@EditListingActivity,
                            "Update failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ListingRequest>, t: Throwable) {
                    Toast.makeText(
                        this@EditListingActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val lat = selectedLatitude ?: 10.3157
        val lng = selectedLongitude ?: 123.8854
        val location = LatLng(lat, lng)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        googleMap.addMarker(MarkerOptions().position(location))

        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng))

            selectedLatitude = latLng.latitude
            selectedLongitude = latLng.longitude

            findViewById<TextView>(R.id.selectedCoordinates).text =
                "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"
        }
    }

    private fun uploadNewImages() {
        val parts = imageUris.mapNotNull { uri ->
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()

            if (bytes != null) {
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "files",
                    "image_${System.currentTimeMillis()}.jpg",
                    requestBody
                )
            } else {
                null
            }
        }

        RetrofitClient.listingApi
            .uploadImages(listingId, parts)
            .enqueue(object : Callback<List<String>> {
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    Toast.makeText(
                        this@EditListingActivity,
                        "Listing and photos updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Toast.makeText(
                        this@EditListingActivity,
                        "Listing updated, but image upload failed",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            })
    }
}
