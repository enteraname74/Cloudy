package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.playlistsOfUser() {
    val playlistService by inject<PlaylistService>()

    get<PlaylistResource.OfUser> { playlistResource ->
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val data: List<PlaylistWithMusics> = playlistService.getAllOfUser(
            userId = userId,
            paginatedRequest = playlistResource.toPaginatedRequest(),
        )

        call.respond(data)
    }
}