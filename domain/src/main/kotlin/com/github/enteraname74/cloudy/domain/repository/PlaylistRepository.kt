package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

interface PlaylistRepository {
    suspend fun getFromId(
        playlistId: UUID
    ): Playlist?
    suspend fun getFromInformation(
        name: String,
        userId: UUID,
    ): Playlist?
    suspend fun upsert(playlist: Playlist): Playlist
    suspend fun upsertAll(playlists: List<Playlist>): List<Playlist>
    suspend fun deleteById(playlistId: UUID): Boolean
    suspend fun deleteAll(playlistIds: List<UUID>)
    suspend fun allOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Playlist>
    suspend fun isPlaylistPossessedByUser(userId: UUID, playlistId: UUID): Boolean
}