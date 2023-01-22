package com.ahmedhnewa.services.security.verification_token

data class AccountVerification(
    val token: String,
    val expiresAt: Long
)