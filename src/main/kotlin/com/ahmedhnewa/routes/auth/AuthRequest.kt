package com.ahmedhnewa.routes.auth

import com.ahmedhnewa.utils.Constants
import com.ahmedhnewa.utils.PatternsHelper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import java.util.regex.Pattern

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
) {
        suspend fun validate(call: ApplicationCall, isLogin: Boolean = false): Boolean {
        return when {
            email.isBlank() || password.isBlank() -> {
                call.respond(HttpStatusCode.BadRequest, AuthResponse("", "Please don't enter any blank field."))
                false
            }
            !Pattern.compile(Constants.PATTERNS.PASSWORD).matcher(password).matches() && !isLogin -> {
                call.respond(HttpStatusCode.BadRequest, AuthResponse("", "Please enter strong password."))
                false
            }
            !PatternsHelper.EMAIL_ADDRESS.matcher(email).matches() -> {
                call.respond(HttpStatusCode.BadRequest, AuthResponse("", "Please enter valid email address."))
                false
            }

            else -> true
        }
    }
}
