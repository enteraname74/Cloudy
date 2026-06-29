package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import io.ktor.http.*
import io.ktor.server.response.*

suspend fun ApplicationContext.missingTokenInformation() =
    response(
        status = HttpStatusCode.BadRequest,
        message = getRoutingMessages().MISSING_TOKEN_INFORMATION,
    )

suspend fun ApplicationContext.wrongBody() =
    response(
        status = HttpStatusCode.BadRequest,
        message = getRoutingMessages().WRONG_BODY_DATA,
    )

suspend fun ApplicationContext.cannotFindUser() =
    response(
        status = HttpStatusCode.BadRequest,
        message = getRoutingMessages().CANNOT_FIND_USER
    )

suspend fun ApplicationContext.badRequest(message: String) =
    response(
        status = HttpStatusCode.BadRequest,
        message = message,
    )

suspend fun ApplicationContext.forbidden(message: String) =
    response(
        status = HttpStatusCode.Forbidden,
        message = message,
    )

suspend fun ApplicationContext.response(status: HttpStatusCode, message: String) =
    call.respond(status, message)