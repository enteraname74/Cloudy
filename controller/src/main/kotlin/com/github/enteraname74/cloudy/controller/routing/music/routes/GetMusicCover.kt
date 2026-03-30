package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.resource.MusicResource
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.service.CoverService
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import org.koin.ktor.ext.inject
import java.io.File
import kotlin.uuid.Uuid

fun Route.getMusicCover() {
    val musicService by inject<MusicService>()
    val coverService by inject<CoverService>()

    get<MusicResource.Cover> { musicResource ->
        val routingMessages: RoutingMessages = getRoutingMessages()

        val username: String = getUsernameFromToken() ?: return@get missingTokenInformation()
        val userId: Uuid = getUserIdFromToken() ?: return@get missingTokenInformation()

        val correspondingMusic: Music = musicService.getFromCoverPath(
            coverPath = "${Music.COVER_PATH}${musicResource.coverId}",
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.WRONG_ID,
        )

        val musicFile: File = musicService.getMusicFile(
            musicId = correspondingMusic.fingerprint,
            userId = userId,
        ) ?: return@get response(
            status = HttpStatusCode.NotFound,
            message = routingMessages.FILE_NOT_FOUND,
        )

        /*
        We first try to retrieve a custom cover for the file, else, we fetch it from its file.
         */
        val foundCover: ByteArray? = coverService.getByName(
            name = correspondingMusic.fingerprint,
            username = username,
        )

        val finalCover: ByteArray = foundCover ?: coverService.getMusicFileCover(
            file = musicFile
        ) ?: return@get badRequest(routingMessages.IMAGE_NOT_FOUND)

        call.respondBytes(finalCover)
    }
}