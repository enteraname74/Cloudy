package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getLocalDateTimeFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.*

fun Route.songsOfUser() {
    val musicService by inject<MusicService>()

    get("/ofUser") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()
        val lastUpdateAt: LocalDateTime? = getLocalDateTimeFromQueryParam(
            key = ServerUtil.Keys.LAST_UPDATE_AT_KEY,
        )

        val paginatedRequest = PaginatedRequest(
            lastUpdateAt = lastUpdateAt
        )

        call.respond(
            musicService.getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )
        )
    }
}