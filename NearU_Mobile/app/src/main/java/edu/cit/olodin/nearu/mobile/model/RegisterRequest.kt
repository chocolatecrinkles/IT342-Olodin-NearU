package edu.cit.olodin.nearu.mobile.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val role: String
)