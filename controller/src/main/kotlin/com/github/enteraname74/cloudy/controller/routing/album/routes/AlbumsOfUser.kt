package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.getPaginatedRequestFromQueryParam
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.albumsOfUser() {
    val albumService by inject<AlbumService>()

    get("/ofUser") {
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        call.respond(
            message = albumService.getAllOfUser(
                userId = userId,
                paginatedRequest = getPaginatedRequestFromQueryParam(),
            )
        )
    }
}