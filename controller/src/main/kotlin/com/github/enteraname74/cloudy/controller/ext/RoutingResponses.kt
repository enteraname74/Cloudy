package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun ApplicationContext.missingTokenInformation() =
    response(
        status = HttpStatusCode.BadRequest,
        message = RoutingMessages.Auth.MISSING_TOKEN_INFORMATION,
    )

suspend fun ApplicationContext.cannotFindUser() =
    response(
        status = HttpStatusCode.BadRequest,
        message = RoutingMessages.User.CANNOT_FIND_USER
    )

suspend fun ApplicationContext.badRequest(message: String) =
    response(
        status = HttpStatusCode.BadRequest,
        message = message,
    )

suspend fun ApplicationContext.response(status: HttpStatusCode, message: String) =
    call.respond(status, message)