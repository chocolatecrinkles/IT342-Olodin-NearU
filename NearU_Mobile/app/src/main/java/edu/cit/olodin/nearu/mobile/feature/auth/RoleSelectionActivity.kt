package edu.cit.olodin.nearu.mobile.feature.auth

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import edu.cit.olodin.nearu.mobile.R
import edu.cit.olodin.nearu.mobile.shared.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoleSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_selection)

        findViewById<Button>(R.id.studentRoleBtn).setOnClickListener {
            setRole("STUDENT")
        }

        findViewById<Button>(R.id.businessOwnerRoleBtn).setOnClickListener {
            setRole("BUSINESS_OWNER")
        }
    }

    private fun setRole(role: String) {
        RetrofitClient.authApi.setRole(SetRoleRequest(role))
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        AuthSession.saveToken(this@RoleSelectionActivity, body.token)
                        AuthSession.startHomeForRole(this@RoleSelectionActivity, body.role ?: role)
                        finish()
                    } else {
                        showToast("Unable to set role")
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    showToast("Unable to set role: ${t.message ?: "Connection failed"}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
