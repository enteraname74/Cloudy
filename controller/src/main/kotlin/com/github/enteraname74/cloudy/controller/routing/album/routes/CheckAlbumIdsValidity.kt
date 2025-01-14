package com.github.enteraname74.cloudy.controller.routing.album.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.util.UUIDUtils
import com.github.enteraname74.cloudy.domain.service.AlbumService
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject

fun Route.checkAlbumIdsValidity() {
    val albumService by inject<AlbumService>()

    get("/check") {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        val idsToCheck: List<String> = call.receive()
        val uuidList = idsToCheck
            .mapNotNull { UUIDUtils.fromString(it) }
            .distinct()

        val list = albumService.getDeletedMusicsIds(
            idsToCheck = uuidList,
            userId = userId,
        )

        call.respond(
            list.map { it.toString() }
        )
    }
}