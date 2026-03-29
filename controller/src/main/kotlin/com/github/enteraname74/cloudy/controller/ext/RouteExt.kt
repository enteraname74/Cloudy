package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.domain.routingmessages.AppLocale
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext

fun ApplicationContext.getRoutingMessages(): RoutingMessages =
    call.getRoutingMessages()

fun ApplicationCall.getRoutingMessages(): RoutingMessages {
    val language: String? = request.header(ACCEPT_LANGUAGE_HEADER)
    val locale: AppLocale = AppLocale.fromValue(language)

    return RoutingMessages.fromLocale(locale)
}

suspend inline fun <reified T: Any> RoutingContext.respond(result: CloudyResult<T>) {
    when (result) {
        is CloudyResult.Error<T> -> badRequest(result.message.orEmpty())
        is CloudyResult.Success<T> -> {
            if (result.data != Unit) {
                call.respond(result.data)
            }
        }
    }
}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
