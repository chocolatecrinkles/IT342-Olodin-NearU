package edu.cit.olodin.nearu.mobile.feature.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.feature.user.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var roleText: TextView

    private lateinit var logoutBtn: Button
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        fullNameText = findViewById(R.id.fullNameText)
        emailText = findViewById(R.id.emailText)
        roleText = findViewById(R.id.roleText)

        logoutBtn = findViewById(R.id.logoutBtn)
        tabLayout = findViewById(R.id.tabLayout)

        tabLayout.addTab(
            tabLayout.newTab().setText("Profile")
        )

        tabLayout.addTab(
            tabLayout.newTab().setText("Settings")
        )

        fetchUser()

        logoutBtn.setOnClickListener {

            val prefs =
                getSharedPreferences("NearU", MODE_PRIVATE)

            prefs.edit().clear().apply()

            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )

            finishAffinity()
        }
    }

    private fun fetchUser() {

        RetrofitClient.authApi
            .getCurrentUser()
            .enqueue(object : Callback<UserResponse> {

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {

                         if (response.isSuccessful &&
                             response.body() != null
                         ) {

                             var user = response.body()!!

                             fullNameText.text =
                                 "${user.firstname} ${user.lastname}"

                             emailText.text = user.email

                             roleText.text = when (user.role) {
                                 "BUSINESS_OWNER" -> "Business Owner"
                                 else -> "Student"
                             }
                         }
                }

                override fun onFailure(
                    call: Call<UserResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}