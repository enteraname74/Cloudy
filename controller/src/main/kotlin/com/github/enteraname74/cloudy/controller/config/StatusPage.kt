package com.github.enteraname74.cloudy.controller.config

import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

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
        InvalidRequestType.InvalidData -> INVALID_INFORMATION
        InvalidRequestType.Unknown ->  WRONG_INFORMATION
    }