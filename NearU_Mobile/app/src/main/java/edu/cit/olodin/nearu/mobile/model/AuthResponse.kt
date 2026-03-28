package edu.cit.olodin.nearu.mobile.model

data class AuthResponse(
    val token: String
)

data class AuthData(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)

data class UserDto(
    val email: String,
    val firstname: String,
    val lastname: String,
    val role: String
)

data class ErrorResponse(
    val code: String,
    val message: String
)