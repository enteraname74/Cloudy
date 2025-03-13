package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.controller.routingmessages.AppLocale
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.time.LocalDateTime


fun ApplicationContext.getLocalDateTimeFromQueryParam(key: String): LocalDateTime? {
    return try {
        val stringDate: String = call.request.queryParameters[key] ?: return null
        LocalDateTime.parse(stringDate)
    } catch (_: Exception) {
        null
    }
}

fun ApplicationContext.getIntegerFromQueryParam(key: String): Int? {
    return try {
        call.request.queryParameters[key]?.toInt() ?: return null
    } catch (_: Exception) {
        null
    }
}

fun ApplicationContext.getPaginatedRequestFromQueryParam(): PaginatedRequest =
    PaginatedRequest(
        lastUpdateAt = getLocalDateTimeFromQueryParam(key = ServerUtil.Keys.LAST_UPDATE_AT_KEY),
        page = getIntegerFromQueryParam(key = ServerUtil.Keys.PAGE_KEY),
        limitPerPage = getIntegerFromQueryParam(key = ServerUtil.Keys.MAX_PER_PAGE),
    )

suspend inline fun <reified T: Any> ApplicationCall.safeReceive(): T? =
    runCatching { receive<T>() }.getOrNull()

fun ApplicationContext.getRoutingMessages(): RoutingMessages {
    val language: String? = call.request.header(ACCEPT_LANGUAGE_HEADER)
    val locale: AppLocale = AppLocale.fromValue(language)
    
    return RoutingMessages.fromLocale(locale)
}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
