package edu.cit.olodin.nearu.mobile.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.api.RetrofitClient
import edu.cit.olodin.nearu.mobile.model.RegisterRequest

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val firstNameInput = findViewById<EditText>(R.id.firstNameInput)
        val lastNameInput = findViewById<EditText>(R.id.lastNameInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)

        val roles = arrayOf("Student", "Business Owner")

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        roleSpinner.adapter = adapter

        val selectedRole = roleSpinner.selectedItem.toString()

        val role = when (selectedRole) {
            "Student" -> "STUDENT"
            "Business Owner" -> "BUSINESS_OWNER"
            else -> "STUDENT"
        }

        registerBtn.setOnClickListener {

            val firstname = firstNameInput.text.toString().trim()
            val lastname = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = RegisterRequest(
                email = email,
                password = password,
                firstname = firstname,
                lastname = lastname,
                role = role
            )

            RetrofitClient.instance.register(request)
                .enqueue(object : retrofit2.Callback<Void> {

                    override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()

                            // Redirect to login
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            println("ERROR BODY: $errorBody")

                            Toast.makeText(this@RegisterActivity, "Error: $errorBody", Toast.LENGTH_LONG).show()
                        }

                    }

                    override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}