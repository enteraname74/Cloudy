package com.github.enteraname74.cloudy.controller.routing.artist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.domain.service.ArtistService
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlin.uuid.Uuid

fun Route.checkArtistIdsValidity() {
    val artistService by inject<ArtistService>()

    get("/check") {
        val userId = getUserIdFromToken() ?: return@get missingTokenInformation()

        val idsToCheck: List<String> = call.receive()
        val uuidList = idsToCheck
            .mapNotNull { Uuid.parseOrNull(it) }
            .distinct()

        val list = artistService.getDeletedArtistIds(
            idsToCheck = uuidList,
            userId = userId,
        )

        call.respond(
            list.map { it.toString() }
        )
    }
}