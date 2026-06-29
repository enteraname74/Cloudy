package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface PlaylistRepository {
    suspend fun getFromId(
        playlistId: Uuid
    ): Playlist?
    suspend fun getFromCoverPath(
        coverPath: String
    ): Playlist?
    suspend fun getFromInformation(
        name: String,
        userId: Uuid,
    ): Playlist?
    suspend fun upsert(
        playlist: Playlist,
        coverData: FileData?,
        username: String,
    ): Playlist
    suspend fun upsertAll(playlists: List<Playlist>): List<Playlist>
    suspend fun deleteById(playlistId: Uuid): Boolean
    suspend fun deleteAll(playlistIds: List<Uuid>)
    suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Playlist>
    suspend fun isPlaylistPossessedByUser(userId: Uuid, playlistId: Uuid): Boolean
}