package com.ahmedhnewa.utils.helpers.ktor

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.receiveNullableAs(): T? = try {
    this.receiveNullable<T>()
} catch (e: BadRequestException) {
    null
} catch (e: CannotTransformContentToTypeException) {
    null
} catch (e: Exception) {
    e.printStackTrace()
    println("Unhandled error ${e.message}")
    null
}

class RequestBodyMustValid(errorMessage: String): Exception("Request body must valid: $errorMessage")

@Throws(RequestBodyMustValid::class)
suspend inline fun <reified T> ApplicationCall.receiveAs(errorMessage: String = "Please enter a valid body"): T {
    val received = this.receiveNullableAs<T>()
    if (received == null) {
        this.respond(HttpStatusCode.BadRequest, errorMessage)
        throw RequestBodyMustValid(errorMessage)
    }
    return received
}

class RequireIdException: Exception("Must have the id")

object RequestHelpers {
    const val ID = "/{id}"
}

@Throws(RequireIdException::class)
suspend fun ApplicationCall.requireId(
    errorMessage: String = "Please enter valid id to the url"
): String {
    val parameters = this.parameters
    if (!parameters.contains("id") || parameters["id"].toString().isBlank()) {
        this.respond(HttpStatusCode.BadRequest, errorMessage)
        throw RequireIdException()
    }
    return parameters["id"].toString().trim()
}


fun ApplicationCall.getServerUrl(): String {
    val connectionPoint = this.request.local
    return "${connectionPoint.scheme}://${connectionPoint.serverHost}:${connectionPoint.serverPort}"
}