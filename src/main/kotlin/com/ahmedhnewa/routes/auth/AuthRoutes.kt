package com.ahmedhnewa.routes.auth

import com.ahmedhnewa.data.user.User
import com.ahmedhnewa.data.user.UserData
import com.ahmedhnewa.data.user.UserDataSource
import com.ahmedhnewa.services.mail.EmailMessage
import com.ahmedhnewa.services.mail.MailSenderService
import com.ahmedhnewa.services.security.hashing.HashingService
import com.ahmedhnewa.services.security.hashing.SaltedHash
import com.ahmedhnewa.services.security.token.TokenConfig
import com.ahmedhnewa.services.security.token.TokenService
import com.ahmedhnewa.services.security.verification_token.VerificationToken
import com.ahmedhnewa.utils.*
import com.ahmedhnewa.utils.helpers.ktor.getServerUrl
import com.ahmedhnewa.utils.helpers.ktor.receiveNullableAs
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import java.util.regex.Pattern

class AuthRoutes(
    private val userDataSource: UserDataSource,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
    private val mailSenderService: MailSenderService,
    private val routeManager: Route,
) {
    fun signup(
        tokenConfig: TokenConfig = GlobalAppObjects.tokenConfig,
    ) = routeManager.post("signup") {
        call.requireUnAuthenticated()
        val authRequest = call.receiveNullableAs<AuthRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest, AuthResponse(
                    "", "Please enter a valid request body"
                )
            )
            return@post
        }
        if (!authRequest.validate(call)) {
            return@post
        }
        val parameters = call.parameters
        val phoneNumber = parameters["phoneNumber"] ?: ""
        val firstName = parameters["firstName"] ?: ""
        val lastName = parameters["firstName"] ?: ""
        val city = parameters["city"] ?: ""
        val address = parameters["firstName"] ?: ""
        val userData = UserData(
            firstName, lastName, phoneNumber, address, city
        )
        if (userData.validate(call))  {
            return@post
        }
        if () {
            call.respond(HttpStatusCode.BadRequest, "Please enter a valid phone number.")
            return@post
        }
        val saltedHash = hashingService.generateSaltedHash(authRequest.password)
        val user = User(
            email = authRequest.email.lowercase(),
            password = saltedHash.hash,
            salt = saltedHash.salt,
            accountVerification = VerificationToken.generate(),
            data = UserData(
                firstName, lastName, phoneNumber
            )
        )
        val isEmailExists = userDataSource.getUserByEmail(authRequest.email) != null
        if (isEmailExists) {
            call.respond(
                HttpStatusCode.Conflict, AuthResponse(
                    "", "Please use another email"
                )
            )
            return@post
        }

        if (!userDataSource.insertUser(user)) {
            call.respond(
                HttpStatusCode.Conflict, AuthResponse(
                    "", "Unknown error while create the user"
                )
            )
            return@post
        }
        val token = tokenService.generateUserToken(
            tokenConfig, user
        )

        val verificationLink = "${call.getServerUrl()}/authentication/activate?token=${user.accountVerification.token}&email=${user.email}"
        val sendEmailSuccess = mailSenderService.sendEmail(
            EmailMessage(
                to = authRequest.email,
                subject = "Email account verification link",
                body = "Hi, you have sign up on our platform,\n" +
                        " to confirm your email, we need you to open this link\n" +
                        verificationLink + "\n\nif you didn't do that, please ignore this message"
            )
        )

        call.respond(
            HttpStatusCode.Created, AuthResponse(
                token, if (sendEmailSuccess) "If your email address is valid, then your email should have receive" +
                        " a email message that contains" +
                        " link to verify your account."
                else "Account created but we couldn't send you account verification" +
                        "to your email, please try again later."
            )
        )
    }

    fun signIn(
        tokenConfig: TokenConfig = GlobalAppObjects.tokenConfig,
    ) = routeManager.post("signin") {
        call.requireUnAuthenticated()
        val authRequest = call.receiveNullableAs<AuthRequest>() ?: kotlin.run {
            call.respond(
                HttpStatusCode.BadRequest, AuthResponse(
                    "", "Please enter a valid request body"
                )
            )
            return@post
        }
        if (!authRequest.validate(call, isLogin = true)) {
            return@post
        }
        val user = userDataSource.getUserByEmail(authRequest.email.lowercase())
        if (user == null) {
            call.respond(
                HttpStatusCode.Conflict, AuthResponse(
                    "", "Incorrect email"
                )
            )
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = authRequest.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )
        if (!isValidPassword) {
            call.respond(
                HttpStatusCode.Conflict, AuthResponse(
                    "", "Incorrect password"
                )
            )
            return@post
        }
        val token = tokenService.generateUserToken(
            tokenConfig,
            user,
        )
        call.respond(
            HttpStatusCode.OK, AuthResponse(
                token, null
            )
        )
    }

    fun getUserInfo() = routeManager.authenticate {
        get("info") {
            val user = call.getAuthenticatedUserNullable()
            if (user == null) {
                call.respondText("Can't authenticate you, please sign in again")
                return@get
            }
            val userDataResponse = user.toResponse()
            call.respond(HttpStatusCode.OK, userDataResponse)
        }
    }
    
    fun activeUserAccount() = routeManager.get("activate") {
        val email = call.parameters["email"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Please enter your email address.")
            return@get
        }
        val user = userDataSource.getUserByEmail(email) ?: kotlin.run {
            call.respond(HttpStatusCode.Conflict, "Sorry, we can't find this account.")
            return@get
        }

        if (user.accountVerified) {
            call.respond(HttpStatusCode.Conflict, "Account is already activated.")
            return@get
        }

        val enteredToken = call.parameters["token"] ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Please enter a valid token in the parameters")
            return@get
        }
        val expiresAt = Date(user.accountVerification.expiresAt)
        val currentDate = Date(System.currentTimeMillis())
        if (currentDate.after(expiresAt)) {
            call.respond(HttpStatusCode.Conflict, "Token has been expired, please send another code")
            return@get
        }

        val databaseToken = user.accountVerification.token
        if (databaseToken != enteredToken) {
            call.respond(HttpStatusCode.Conflict, "Token is not correct.")
            return@get
        }

        val isSuccess = userDataSource.verifyEmailAccount(user.email)
        if (!isSuccess) {
            call.respond(HttpStatusCode.InternalServerError, "Internal error while activating the account.")
            return@get
        }
        call.respond(HttpStatusCode.OK, "Account has been activated.")
    }
}
