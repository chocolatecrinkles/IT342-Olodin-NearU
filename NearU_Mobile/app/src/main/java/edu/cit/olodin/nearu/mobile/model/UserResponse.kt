package edu.cit.olodin.nearu.mobile.model

data class UserResponse(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val email: String,
    var role: String
)