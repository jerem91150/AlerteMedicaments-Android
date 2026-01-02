package com.alertemedicaments.data.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val email: String,
    val name: String?,
    val image: String? = null
)

data class ErrorResponse(
    val error: String
)

data class RegisterPushTokenRequest(
    val token: String,
    val platform: String = "android"
)

data class PushTokenResponse(
    val success: Boolean
)
