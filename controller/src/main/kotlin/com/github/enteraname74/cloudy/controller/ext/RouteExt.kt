package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.domain.routingmessages.AppLocale
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun ApplicationContext.getRoutingMessages(): RoutingMessages =
    call.getRoutingMessages()

fun ApplicationCall.getRoutingMessages(): RoutingMessages {
    val language: String? = request.headers[HttpHeaders.AcceptLanguage]
    val locale: AppLocale = AppLocale.fromValue(language)

    return RoutingMessages.fromLocale(locale)
}

suspend inline fun <reified T: Any> RoutingContext.respond(result: CloudyResult<T>) {
    when (result) {
        is CloudyResult.Error<T> -> badRequest(result.message.orEmpty())
        is CloudyResult.Success<T> -> {
            call.respond(result.data)
        }
    }
}
