package edu.cit.olodin.nearu.mobile.feature.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val role: String
)