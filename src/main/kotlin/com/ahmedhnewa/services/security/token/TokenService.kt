package com.ahmedhnewa.services.security.token

import com.ahmedhnewa.data.user.User

interface TokenService {
    fun generate(
        config: TokenConfig,
        vararg claims: TokenClaim
    ): String
    fun generateUserToken(
        config: TokenConfig,
        user: User
    ): String
}