package com.github.enteraname74.cloudy.controller.ext

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import java.time.LocalDateTime


fun PipelineContext<Unit, ApplicationCall>.getLocalDateTimeFromQueryParam(key: String): LocalDateTime? {
    return try {
        val stringDate: String = call.request.queryParameters[key] ?: return null
        LocalDateTime.parse(stringDate)
    } catch (_: Exception) {
        null
    }
}