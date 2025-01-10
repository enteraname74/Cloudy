package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

class PlaylistService(
    private val playlistRepository: PlaylistRepository
) {
    suspend fun getFromId(playlistId: UUID): Playlist? =
        playlistRepository.getFromId(playlistId)

    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest
    ): List<Playlist> =
        playlistRepository.allOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isPlaylistPossessedByUser(
        userId: UUID,
        playlistId: UUID,
    ): Boolean =
        playlistRepository.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )

    suspend fun deletePlaylist(
        playlistId: UUID,
    ): Boolean =
        playlistRepository.deleteById(playlistId)
}