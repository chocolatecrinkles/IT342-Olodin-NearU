package edu.cit.olodin.nearu.mobile.feature.auth

import android.content.Context
import android.content.Intent
import android.util.Base64
import edu.cit.olodin.nearu.mobile.feature.businessowner.BusinessOwnerMainActivity
import edu.cit.olodin.nearu.mobile.feature.student.StudentMainActivity
import org.json.JSONObject

object AuthSession {
    private const val PREFS_NAME = "NearU"
    private const val TOKEN_KEY = "token"

    fun getToken(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(TOKEN_KEY, null)
    }

    fun saveToken(context: Context, token: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(TOKEN_KEY, token)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }

    fun extractRole(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = Base64.decode(parts[1], Base64.URL_SAFE)
            JSONObject(String(payload, Charsets.UTF_8)).getString("role")
        } catch (e: Exception) {
            null
        }
    }

    fun startHomeForRole(context: Context, role: String?) {
        val destination = if (role == "BUSINESS_OWNER") {
            BusinessOwnerMainActivity::class.java
        } else {
            StudentMainActivity::class.java
        }

        context.startActivity(
            Intent(context, destination).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}
