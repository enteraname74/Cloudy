package com.github.enteraname74.cloudy.controller.routing.music.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.music.model.ModifiedMusic
import com.github.enteraname74.cloudy.controller.routing.music.model.fromModifiedMusic
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.service.MusicService
import com.github.enteraname74.cloudy.domain.service.UserService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.content.MultiPartData
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.updateSong() {
    val musicService by inject<MusicService>()
    val userService by inject<UserService>()

    put {

        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()
        val routingMessages: RoutingMessages = getRoutingMessages()
        val user: User = userService.getUserFromId(userId) ?: return@put badRequest(
            message = routingMessages.CANNOT_FIND_USER,
        )

        val multipartData: MultiPartData = call.receiveMultipart()
        val updateInformation = MultiPartDataUtils.processMusicUpdateRequest(multipartData)

        when(updateInformation) {
            is CloudyResult.Error -> {
                return@put badRequest(routingMessages.WRONG_INFORMATION)
            }
            is CloudyResult.Success -> {
                val modifiedMusicInfo: ModifiedMusic = updateInformation.data.second
                val coverData: FileData? = updateInformation.data.first
                val matchingMusic: Music = musicService.getFromId(musicId = modifiedMusicInfo.id)
                    ?: return@put badRequest(routingMessages.WRONG_ID)

                val isMusicPossessedByUser = musicService.isMusicPossessedByUser(
                    musicId = modifiedMusicInfo.id,
                    userId = userId,
                )

                if (!isMusicPossessedByUser) {
                    return@put forbidden(routingMessages.SONG_NOT_POSSESSED_BY_USER)
                }

                val updatedMusic: Music = matchingMusic.fromModifiedMusic(modifiedMusicInfo)

                val updateResult: CloudyResult<Music> = musicService.update(
                    modifiedMusic = updatedMusic,
                    user = user,
                    newArtistsNames = modifiedMusicInfo.artists,
                    newCover = coverData,
                )

                when(updateResult) {
                    is CloudyResult.Error -> {
                        return@put badRequest(routingMessages.CANNOT_UPDATE_SONG)
                    }
                    is CloudyResult.Success -> {
                        call.respond(updateResult.data)
                    }
                }
            }
        }
    }
}