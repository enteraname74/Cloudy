package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.model.CheckMusicsBody
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject

fun Route.checkMusicIdsValidity() {
    val musicService by inject<MusicService>()

    post<MusicResource.Check> {
        val userId = getUserIdFromToken() ?: return@post missingTokenInformation()

        val body: CheckMusicsBody = call.receive()
        val list = musicService.getDeletedMusicsIds(
            idsToCheck = body.ids,
            userId = userId,
        )

        call.respond(list)
    }
}