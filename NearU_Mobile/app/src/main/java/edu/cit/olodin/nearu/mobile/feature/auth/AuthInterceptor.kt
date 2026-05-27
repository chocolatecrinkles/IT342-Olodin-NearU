package edu.cit.olodin.nearu.mobile.feature.auth

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val prefs = context.getSharedPreferences("NearU", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val request = chain.request().newBuilder()

        if (token != null) {
            request.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(request.build())
    }
}