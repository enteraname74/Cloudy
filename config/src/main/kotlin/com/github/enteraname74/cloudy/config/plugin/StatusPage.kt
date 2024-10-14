package com.github.enteraname74.cloudy.config.plugin

import com.github.enteraname74.cloudy.config.util.Messages
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

internal fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = Messages.StatusPage.internalServerErrorWithCause(
                    cause = cause.message.orEmpty(),
                ),
            )
        }
    }
}