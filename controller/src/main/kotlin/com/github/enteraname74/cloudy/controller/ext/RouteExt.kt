package com.github.enteraname74.cloudy.controller.ext

import com.github.enteraname74.cloudy.config.ApplicationContext
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.server.application.*
import io.ktor.util.pipeline.*
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
