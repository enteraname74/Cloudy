package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.checkMusicIdsValidity() {
    val musicService by inject<MusicService>()

    get("/check") {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()
        val idsToCheck: List<String> = call.receive()

        val list = musicService.getDeletedMusicsIds(
            idsToCheck = idsToCheck,
            userId = userId,
        )

        call.respond(list)
    }
}