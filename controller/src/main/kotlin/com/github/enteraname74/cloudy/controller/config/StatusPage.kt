package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

internal fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<BadRequestException> { call, _ ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = call.getRoutingMessages().WRONG_BODY_DATA,
            )
        }
        exception<RequestValidationException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = call
                    .getRoutingMessages()
                    .fromInvalidRequestType(
                        type = InvalidRequestType.fromRequest(cause),
                    )
            )
        }
        exception<Throwable> { call, cause ->
            println("HOLY MOLLY")
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = call.getRoutingMessages().internalServerError(
                    error = cause.message.orEmpty()
                ),
            )
        }
    }
}

private fun RoutingMessages.fromInvalidRequestType(type: InvalidRequestType): String =
    when (type) {
        InvalidRequestType.UserInformation -> MISSING_USER_INFORMATION
        InvalidRequestType.MusicUpdate,
        InvalidRequestType.MusicCheck -> INVALID_INFORMATION

        InvalidRequestType.Unknown -> WRONG_INFORMATION
    }