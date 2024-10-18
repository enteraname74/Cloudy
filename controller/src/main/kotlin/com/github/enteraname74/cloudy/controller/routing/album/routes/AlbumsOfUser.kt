package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getLocalDateTimeFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.util.ServerUtil
import com.github.enteraname74.cloudy.domain.service.AlbumService
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.time.LocalDateTime
import java.util.*

fun Route.albumsOfUser() {
    val albumService by inject<AlbumService>()

    get("/ofUser") {
        val userId: UUID = getUserIdFromToken() ?: return@get missingTokenInformation()
        val lastUpdateAt: LocalDateTime? = getLocalDateTimeFromQueryParam(
            key = ServerUtil.Keys.LAST_UPDATE_AT_KEY,
        )
        call.respond(
            message = albumService.getAllOfUser(
                userId = userId,
                paginatedRequest = PaginatedRequest(
                    lastUpdateAt = lastUpdateAt,
                ),
            )
        )
    }
}