package com.ahmedhnewa.routes.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val message: String?
)
