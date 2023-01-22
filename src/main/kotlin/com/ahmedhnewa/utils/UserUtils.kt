package com.ahmedhnewa.utils

import com.ahmedhnewa.data.user.User
import com.ahmedhnewa.data.user.UserDataSource
import com.ahmedhnewa.data.user.UserRole
import com.ahmedhnewa.utils.exceptions.NotAdminException
import com.ahmedhnewa.utils.exceptions.UserShouldAuthenticated
import com.ahmedhnewa.utils.exceptions.UserShouldUnAuthenticated
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

suspend fun ApplicationCall.getAuthenticatedUserNullable(): User? {
    val userDataSource = inject<UserDataSource>().value
    val principal = this.principal<JWTPrincipal>()
    val userId = principal?.getClaim("userId", String::class) ?: return null
    return userDataSource.getUserById(userId)
}

suspend fun ApplicationCall.requireAuthenticatedUser(errorMessage: String = ""): User {
    return getAuthenticatedUserNullable() ?: kotlin.run {
        this.respond(HttpStatusCode.Unauthorized, "You must be authenticated to access this route")
        throw UserShouldAuthenticated(errorMessage)
    }
}

suspend fun ApplicationCall.requireUnAuthenticated(errorMessage: String = "") {
    if (getAuthenticatedUserNullable() != null) {
        this.respond(HttpStatusCode.Forbidden, "You already authenticated, please logout first.\n$errorMessage")
        throw UserShouldUnAuthenticated(errorMessage)
    }
}

suspend fun ApplicationCall.isUserAdmin(): Boolean {
    return try {
        val user = getAuthenticatedUserNullable() ?: return false
        user.role == UserRole.Admin
    } catch (e: Exception) {
        // it could be InvalidFormatException, IllegalArgumentException
        // but for security reasons here will catch any kind of exception
        false
    }
}

suspend fun ApplicationCall.allowOnlyAdmin() {
    if (!this.isUserAdmin()) {
        this.respond(HttpStatusCode.Forbidden, "You can't access this route!")
        throw NotAdminException()
    }
}