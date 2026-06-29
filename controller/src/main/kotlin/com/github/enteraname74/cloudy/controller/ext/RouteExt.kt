package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.controller.routingmessages.AppLocale
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.logging.CloudyLogger
import io.ktor.server.application.*
import io.ktor.server.request.*
import kotlin.time.Instant


private fun ApplicationContext.getTimestampFromQuery(key: String): Long? {
    return try {
        val stringDate: String = call.request.queryParameters[key] ?: return null
        Instant.parse(stringDate).toEpochMilliseconds()
    } catch (_: Exception) {
        null
    }
}

@Deprecated("User resources plugin instead")
fun ApplicationContext.getIntegerFromQueryParam(key: String): Int? {
    return try {
        call.request.queryParameters[key]?.toInt() ?: return null
    } catch (_: Exception) {
        null
    }
}

@Deprecated("User resources plugin instead")
fun ApplicationContext.getPaginatedRequestFromQueryParam(): PaginatedRequest =
    PaginatedRequest(
        lastUpdateAtMillis = getTimestampFromQuery(key = ServerUtil.Keys.LAST_UPDATE_AT_KEY),
        page = getIntegerFromQueryParam(key = ServerUtil.Keys.PAGE_KEY),
        limitPerPage = getIntegerFromQueryParam(key = ServerUtil.Keys.MAX_PER_PAGE),
    )

@Deprecated("User resources plugin instead")
suspend inline fun <reified T: Any> ApplicationCall.safeReceive(): T? =
    runCatching { receive<T>() }.getOrNull()

fun ApplicationContext.getRoutingMessages(): RoutingMessages {
    val language: String? = call.request.header(ACCEPT_LANGUAGE_HEADER)
    val locale: AppLocale = AppLocale.fromValue(language)
    
    return RoutingMessages.fromLocale(locale)
}

private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"
