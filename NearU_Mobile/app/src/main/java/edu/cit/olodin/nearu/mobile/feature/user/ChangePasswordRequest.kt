package edu.cit.olodin.nearu.mobile.feature.user

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)