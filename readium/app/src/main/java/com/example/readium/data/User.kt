package com.example.readium.data

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val profilePhotoUrl: String? = null,
    val biography: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val biography: String = "",
    val profilePhotoUrl: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)