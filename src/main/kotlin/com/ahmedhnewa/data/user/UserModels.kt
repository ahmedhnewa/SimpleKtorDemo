package com.ahmedhnewa.data.user

import com.ahmedhnewa.routes.auth.AuthResponse
import com.ahmedhnewa.services.security.verification_token.AccountVerification
import com.ahmedhnewa.utils.Constants
import com.ahmedhnewa.utils.PatternsHelper
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.regex.Pattern

data class User(
    val email: String,
    val accountVerification: AccountVerification,
    val accountVerified: Boolean = false,
    val password: String,
    val role: UserRole = UserRole.User,
    val salt: String,
    val data: UserData,
    @BsonId val id: ObjectId = ObjectId()
) {
    fun toResponse(): UserResponse = UserResponse(
        email = email,
        accountVerified = accountVerified,
        role = role,
        data = data,
        id = id.toString(),
    )
}

enum class UserRole {
    Admin,
    User
}

@Serializable
data class UserData(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val address: String,
    val city: String
) {
    suspend fun validate(call: ApplicationCall, isLogin: Boolean = false): Boolean {
        return when {
            firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank() ||
                    address.isBlank() || city.isBlank() -> {
                call.respond(HttpStatusCode.BadRequest, AuthResponse("", "Please don't enter any blank field."))
                false
            }

            !Pattern.compile(Constants.PATTERNS.PHONE_NUMBER).matcher(phoneNumber).matches() -> {
                call.respond(HttpStatusCode.BadRequest, AuthResponse("", "Please enter strong password."))
                false
            }

            else -> true
        }
    }
}

@Serializable
data class UserResponse(
    val email: String,
    val accountVerified: Boolean = false,
    val role: UserRole = UserRole.User,
    val data: UserData,
    val id: String
)