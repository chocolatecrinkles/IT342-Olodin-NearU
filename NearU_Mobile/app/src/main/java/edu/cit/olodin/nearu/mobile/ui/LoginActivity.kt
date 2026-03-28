package edu.cit.olodin.nearu.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.model.AuthResponse
import edu.cit.olodin.nearu.mobile.model.LoginRequest

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

            RetrofitClient.instance.login(request)
                .enqueue(object : retrofit2.Callback<AuthResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<AuthResponse>,
                        response: retrofit2.Response<AuthResponse>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            val token = response.body()!!.token

                            Toast.makeText(this@LoginActivity, "Login Success!", Toast.LENGTH_SHORT).show()

                            val prefs = getSharedPreferences("NearU", MODE_PRIVATE)
                            prefs.edit().putString("token", token).apply()

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
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
}