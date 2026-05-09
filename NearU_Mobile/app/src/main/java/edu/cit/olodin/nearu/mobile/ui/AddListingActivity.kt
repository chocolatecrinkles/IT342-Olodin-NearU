package edu.cit.olodin.nearu.mobile.ui

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
import edu.cit.olodin.nearu.mobile.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.model.ListingRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddListingActivity :
    AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var nameInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var descriptionInput: EditText

    private lateinit var typeSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var pricingSpinner: Spinner

    private lateinit var priceInput: EditText
    private lateinit var minPriceInput: EditText
    private lateinit var maxPriceInput: EditText

    private lateinit var createBtn: Button
    private lateinit var selectImagesBtn: ImageButton

    private lateinit var imageCountText: TextView

    private val imageUris = mutableListOf<Uri>()

    private lateinit var googleMap: GoogleMap

    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private val categoryOptions = mapOf(
        "ACCOMMODATION" to listOf(
            "BOARDING_HOUSE",
            "DORM"
        ),
        "SERVICE" to listOf(
            "RESTAURANT",
            "CAFE",
            "LAUNDROMAT"
        ),
        "OTHER" to listOf("OTHER")
    )

    private val pricingOptions = mapOf(
        "ACCOMMODATION" to listOf(
            "MONTHLY",
            "WEEKLY"
        ),
        "SERVICE" to listOf("RANGE"),
        "OTHER" to listOf("RANGE")
    )

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { uris ->

            imageUris.clear()
            imageUris.addAll(uris)

            imageCountText.text =
                "${imageUris.size} images selected"
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_listing)

        nameInput = findViewById(R.id.nameInput)
        addressInput = findViewById(R.id.addressInput)
        descriptionInput = findViewById(R.id.descriptionInput)

        typeSpinner = findViewById(R.id.typeSpinner)
        categorySpinner = findViewById(R.id.categorySpinner)
        pricingSpinner = findViewById(R.id.pricingSpinner)

        priceInput = findViewById(R.id.priceInput)
        minPriceInput = findViewById(R.id.minPriceInput)
        maxPriceInput = findViewById(R.id.maxPriceInput)

        createBtn = findViewById(R.id.createBtn)
        selectImagesBtn = findViewById(R.id.selectImagesBtn)

        imageCountText = findViewById(R.id.imageCountText)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment)
                    as SupportMapFragment

        mapFragment.getMapAsync(this)

        setupSpinners()

        selectImagesBtn.setOnClickListener {

            imagePicker.launch("image/*")
        }

        createBtn.setOnClickListener {

            createListing()
        }
    }

    private fun setupSpinners() {

        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf(
                "ACCOMMODATION",
                "SERVICE",
                "OTHER"
            )
        )

        typeSpinner.adapter = typeAdapter

        typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    val selectedType =
                        typeSpinner.selectedItem.toString()

                    val categories =
                        categoryOptions[selectedType]
                            ?: emptyList()

                    val pricing =
                        pricingOptions[selectedType]
                            ?: emptyList()

                    categorySpinner.adapter =
                        ArrayAdapter(
                            this@AddListingActivity,
                            android.R.layout.simple_spinner_item,
                            categories
                        )

                    pricingSpinner.adapter =
                        ArrayAdapter(
                            this@AddListingActivity,
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

                    val pricing =
                        pricingSpinner.selectedItem.toString()

                    if (pricing == "RANGE") {

                        priceInput.visibility = View.GONE

                        minPriceInput.visibility = View.VISIBLE
                        maxPriceInput.visibility = View.VISIBLE

                    } else {

                        priceInput.visibility = View.VISIBLE

                        minPriceInput.visibility = View.GONE
                        maxPriceInput.visibility = View.GONE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun createListing() {

        if (selectedLatitude == null || selectedLongitude == null) {

            Toast.makeText(
                this,
                "Please select a location on the map",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val request =
            ListingRequest(

                name = nameInput.text.toString(),

                category =
                categorySpinner.selectedItem.toString(),

                listingType =
                typeSpinner.selectedItem.toString(),

                address =
                addressInput.text.toString(),

                price =
                priceInput.text.toString()
                    .toDoubleOrNull(),

                minPrice =
                minPriceInput.text.toString()
                    .toDoubleOrNull(),

                maxPrice =
                maxPriceInput.text.toString()
                    .toDoubleOrNull(),

                pricingType =
                pricingSpinner.selectedItem.toString(),

                latitude = selectedLatitude,

                longitude = selectedLongitude,

                description =
                descriptionInput.text.toString()
            )


        RetrofitClient.listingApi
            .createListing(request)
            .enqueue(object : Callback<ListingRequest> {

                override fun onResponse(
                    call: Call<ListingRequest>,
                    response: Response<ListingRequest>
                ) {

                    if (response.isSuccessful &&
                        response.body() != null
                    ) {

                        Toast.makeText(
                            this@AddListingActivity,
                            "Listing created",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@AddListingActivity,
                            "Create failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ListingRequest>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@AddListingActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        val defaultLocation = LatLng(10.3157, 123.8854)

        googleMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(defaultLocation, 13f)
        )

        googleMap.setOnMapClickListener { latLng ->

            googleMap.clear()

            googleMap.addMarker(
                MarkerOptions().position(latLng)
            )

            selectedLatitude = latLng.latitude
            selectedLongitude = latLng.longitude

            findViewById<TextView>(R.id.selectedCoordinates).text =
                "Lat: ${latLng.latitude}, Lng: ${latLng.longitude}"


        }
    }
}