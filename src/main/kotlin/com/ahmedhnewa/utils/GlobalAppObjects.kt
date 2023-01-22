package com.ahmedhnewa.utils

import com.ahmedhnewa.services.security.token.TokenConfig
import io.ktor.server.config.*

object GlobalAppObjects {
    lateinit var jwtConfig: ApplicationConfig
    val tokenConfig by lazy {
        TokenConfig(
            issuer = jwtConfig.property("issuer").getString(),
            audience = jwtConfig.property("audience").getString(),
            expiresIn = 365L * 1000L * 60L * 60L * 24L,
            secret = System.getenv("JWT_SECRET") ?: "",
        )
    }
}