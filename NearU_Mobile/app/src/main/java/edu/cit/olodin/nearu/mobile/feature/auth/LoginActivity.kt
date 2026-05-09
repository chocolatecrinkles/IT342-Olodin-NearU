package edu.cit.olodin.nearu.mobile.feature.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.feature.businessowner.BusinessOwnerMainActivity
import edu.cit.olodin.nearu.mobile.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("NearU", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        if (token != null) {

            val role =
                extractRole(token)

            if (role == "BUSINESS_OWNER") {

                startActivity(
                    Intent(
                        this,
                        BusinessOwnerMainActivity::class.java
                    )
                )

            } else {

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )
            }

            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val goRegisterBtn = findViewById<Button>(R.id.goRegisterBtn)


        loginBtn.setOnClickListener {

            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = LoginRequest(email, password)

            RetrofitClient.authApi.login(request)

                .enqueue(object : retrofit2.Callback<AuthResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<AuthResponse>,
                        response: retrofit2.Response<AuthResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            val token = response.body()!!.token

                            val role =
                                extractRole(token)

                            Toast.makeText(this@LoginActivity, "Login Success!", Toast.LENGTH_SHORT).show()

                            val prefs = getSharedPreferences("NearU", MODE_PRIVATE)
                            prefs.edit().putString("token", token).apply()

                            if (role == "BUSINESS_OWNER") {

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        BusinessOwnerMainActivity::class.java
                                    )
                                )

                            } else {

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        MainActivity::class.java
                                    )
                                )
                            }

                            finish()

                        } else {
                            Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<AuthResponse>, t: Throwable) {
                        println(t.message)
                        Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        goRegisterBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun extractRole(token: String): String? {

        try {

            val parts = token.split(".")

            if (parts.size < 2) return null

            val payload =
                android.util.Base64.decode(
                    parts[1],
                    android.util.Base64.URL_SAFE
                )

            val json =
                String(payload, Charsets.UTF_8)

            val obj =
                org.json.JSONObject(json)

            return obj.getString("role")

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return null
    }
}