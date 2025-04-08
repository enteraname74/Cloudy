package com.github.enteraname74.cloudy.controller.routing.playlist.routes

import com.github.enteraname74.cloudy.config.auth.getUserIdFromToken
import com.github.enteraname74.cloudy.config.auth.getUsernameFromToken
import com.github.enteraname74.cloudy.controller.ext.badRequest
import com.github.enteraname74.cloudy.controller.ext.forbidden
import com.github.enteraname74.cloudy.controller.ext.getRoutingMessages
import com.github.enteraname74.cloudy.controller.ext.missingTokenInformation
import com.github.enteraname74.cloudy.controller.routing.playlist.model.ModifiedPlaylist
import com.github.enteraname74.cloudy.controller.routing.playlist.model.fromModifiedPlaylist
import com.github.enteraname74.cloudy.controller.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.controller.util.MultiPartDataUtils
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.service.PlaylistService
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.util.*

fun Route.updatePlaylist() {
    val playlistService by inject<PlaylistService>()

    put {
        val userId: UUID = getUserIdFromToken() ?: return@put missingTokenInformation()
        val username: String = getUsernameFromToken() ?: return@put missingTokenInformation()

        val routingMessages: RoutingMessages = getRoutingMessages()

        val multipartData: MultiPartData = call.receiveMultipart()
        val updateInformation = MultiPartDataUtils.processUpdateRequest<ModifiedPlaylist>(multipartData)

        when(updateInformation) {
            is CloudyResult.Error -> {
                return@put badRequest(routingMessages.WRONG_INFORMATION)
            }
            is CloudyResult.Success -> {
                val modifiedPlaylist: ModifiedPlaylist = updateInformation.data.second
                val coverData: FileData? = updateInformation.data.first

                val matchingPlaylist: Playlist = playlistService.getFromId(
                    playlistId = modifiedPlaylist.id
                ) ?: return@put badRequest(routingMessages.WRONG_ID)

                val isPlaylistPossessedByUser: Boolean = playlistService.isPlaylistPossessedByUser(
                    userId = userId,
                    playlistId = modifiedPlaylist.id,
                )

                if (!isPlaylistPossessedByUser) {
                    return@put forbidden(routingMessages.PLAYLIST_NOT_POSSESSED_BY_USER)
                }

                val updatedPlaylist: Playlist = matchingPlaylist.fromModifiedPlaylist(modifiedPlaylist)

                val playlist: Playlist = playlistService.upsert(
                    playlist = updatedPlaylist,
                    coverData = coverData,
                    username = username,
                )

                call.respond(playlist)
            }
        }
    }
}