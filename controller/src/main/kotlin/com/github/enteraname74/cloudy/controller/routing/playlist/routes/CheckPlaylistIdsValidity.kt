package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.CheckPlaylistsBody
import com.github.enteraname74.cloudy.controller.routing.playlist.resource.PlaylistResource
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.checkPlaylistIdsValidity() {
    val playlistService by inject<PlaylistService>()

    post<PlaylistResource.Check> {
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val body: CheckPlaylistsBody = call.receive()
        val list = playlistService.getDeletedPlaylistIds(
            idsToCheck = body.ids,
            userId = userId,
        )

        call.respond(list)
    }
}