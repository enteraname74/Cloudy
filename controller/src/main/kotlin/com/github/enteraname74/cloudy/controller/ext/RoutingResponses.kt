package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.missingTokenInformation() =
    response(
        status = HttpStatusCode.BadRequest,
        message = RoutingMessages.Generic.MISSING_TOKEN_INFORMATION,
    )

suspend fun PipelineContext<Unit, ApplicationCall>.cannotFindUser() =
    response(
        status = HttpStatusCode.BadRequest,
        message = RoutingMessages.User.CANNOT_FIND_USER
    )

suspend fun PipelineContext<Unit, ApplicationCall>.badRequest(message: String) =
    response(
        status = HttpStatusCode.BadRequest,
        message = message,
    )

suspend fun PipelineContext<Unit, ApplicationCall>.response(status: HttpStatusCode, message: String) =
    call.respond(status, message)