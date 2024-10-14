package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.ext.response
import com.github.enteraname74.cloudy.controller.routing.music.model.ModifiedMusic
import com.github.enteraname74.cloudy.controller.routing.music.model.fromModifiedMusic
import com.github.enteraname74.cloudy.controller.util.RoutingMessages
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.service.MusicService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.updateSong() {
    val musicService by inject<MusicService>()

    put {
        val modifiedMusicInfo: ModifiedMusic = call.receive()
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()

        val matchingMusic: Music = musicService.getFromId(musicId = modifiedMusicInfo.id)
            ?: return@put badRequest(RoutingMessages.Generic.WRONG_ID)

        val isMusicPossessedByUser = musicService.isMusicPossessedByUser(
            musicId = modifiedMusicInfo.id,
            userId = userId,
        )

        if (!isMusicPossessedByUser) {
            return@put response(
                status = HttpStatusCode.Forbidden,
                message = RoutingMessages.Music.SONG_NOT_POSSESSED_BY_USER,
            )
        }

        val updatedMusic: Music = matchingMusic.fromModifiedMusic(modifiedMusicInfo)

        musicService.upsert(
            modifiedMusic = updatedMusic,
            userId = userId,
        )

        response(
            status = HttpStatusCode.OK,
            message = RoutingMessages.Music.SONG_UPDATED,
        )
    }
}