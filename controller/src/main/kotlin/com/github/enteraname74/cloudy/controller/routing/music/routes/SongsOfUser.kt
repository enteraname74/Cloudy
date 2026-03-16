package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.songsOfUser() {
    val musicService by inject<MusicService>()

    get<MusicResource.OfUser> { musicResource ->
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val data: List<Music> = musicService.getAllOfUser(
            userId = userId,
            paginatedRequest = musicResource.toPaginatedRequest(),
        )

        call.respond(data)
    }
}