package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.controller.routingmessages.AppLocale
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.header

fun ApplicationContext.getRoutingMessages(): RoutingMessages =
    call.getRoutingMessages()

fun ApplicationCall.getRoutingMessages(): RoutingMessages {
    val language: String? = request.header(ACCEPT_LANGUAGE_HEADER)
    val locale: AppLocale = AppLocale.fromValue(language)

    return RoutingMessages.fromLocale(locale)
}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
