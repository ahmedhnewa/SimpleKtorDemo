package com.ahmedhnewa.plugins

import com.ahmedhnewa.utils.GlobalAppObjects
import com.ahmedhnewa.services.security.token.TokenConfig
import com.ahmedhnewa.utils.getAuthenticatedUserNullable
import com.ahmedhnewa.utils.requireAuthenticatedUser
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*

fun Application.configureSecurity(tokenConfig: TokenConfig = GlobalAppObjects.tokenConfig) {

    authentication {
        jwt {
//                val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            verifier(
                JWT.require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(tokenConfig.audience))
                    JWTPrincipal(credential.payload) else null
            }
        }
    }

//    install(RequestValidation) {
//        validate<AuthRequest> {
//            if (it.username.isBlank())
//                ValidationResult.Invalid("Username should not be empty")
//            else if (it.password.isBlank()) {
//                ValidationResult.Invalid("Password should not be empty")
//            } else ValidationResult.Valid
//        }
//    }
//
//    install(StatusPages) {
//        exception<Throwable> { call, cause ->
//            if (cause is RequestValidationException) {
//                call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.reasons.joinToString()))
//            } else {
//                call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
//            }
//        }
//    }

}
