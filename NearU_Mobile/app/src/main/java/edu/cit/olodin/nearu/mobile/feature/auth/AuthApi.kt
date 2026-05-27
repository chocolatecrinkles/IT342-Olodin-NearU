package edu.cit.olodin.nearu.mobile.feature.auth

import retrofit2.Call
import edu.cit.olodin.nearu.mobile.feature.user.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("/api/auth/register")
    fun register(@Body request: RegisterRequest): Call<Void>

    @GET("/api/auth/me")
    fun getCurrentUser(): Call<UserResponse>
}