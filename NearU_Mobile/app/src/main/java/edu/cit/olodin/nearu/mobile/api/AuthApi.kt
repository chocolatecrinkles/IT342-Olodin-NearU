package edu.cit.olodin.nearu.mobile.api

import retrofit2.Call
import edu.cit.olodin.nearu.mobile.model.AuthResponse
import edu.cit.olodin.nearu.mobile.model.LoginRequest
import edu.cit.olodin.nearu.mobile.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("/api/auth/register")
    fun register(@Body request: RegisterRequest): Call<Void>
}