package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.checkMusicIdsValidity() {
    val musicService by inject<MusicService>()

    get("/check") {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        val idsToCheck: List<String> = call.receive()
        val uuidList = idsToCheck
            .mapNotNull { UUIDUtils.fromString(it) }
            .distinct()

        val list = musicService.getDeletedMusicsIds(
            idsToCheck = uuidList,
            userId = userId,
        )

        call.respond(
            list.map { it.toString() }
        )
    }
}