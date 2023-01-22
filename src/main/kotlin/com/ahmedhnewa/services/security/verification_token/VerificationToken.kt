package com.ahmedhnewa.services.security.verification_token

import java.math.BigInteger
import java.security.SecureRandom

object VerificationToken {
    fun generate(): AccountVerification {
        val random = SecureRandom()
        val token =  BigInteger(130, random).toString(32)
        return AccountVerification(
            token = token,
            expiresAt = System.currentTimeMillis() + 24 * 60 * 1000
        )
    }
}