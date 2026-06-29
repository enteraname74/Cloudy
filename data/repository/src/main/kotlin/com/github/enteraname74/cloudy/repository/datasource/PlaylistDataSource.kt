package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface PlaylistDataSource {
    suspend fun getFromId(
        playlistId: Uuid
    ): Playlist?
    suspend fun getWithMusics(
        playlistId: Uuid,
    ): PlaylistWithMusics?
    suspend fun getFromCoverPath(
        coverPath: String
    ): Playlist?
    suspend fun getFromInformation(
        name: String,
        userId: Uuid,
    ): Playlist?
    suspend fun getFavorite(
        userId: Uuid,
    ): Playlist?
    suspend fun upsert(playlist: Playlist): Playlist
    suspend fun upsertAll(playlists: List<Playlist>): List<Playlist>
    suspend fun deleteById(playlistId: Uuid): Boolean
    suspend fun deleteAll(playlistIds: List<Uuid>)
    suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<PlaylistWithMusics>
    suspend fun isPlaylistPossessedByUser(userId: Uuid, playlistId: Uuid): Boolean
}