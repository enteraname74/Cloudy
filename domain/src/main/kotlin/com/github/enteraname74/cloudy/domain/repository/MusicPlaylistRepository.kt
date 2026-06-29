package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface MusicPlaylistRepository {
    suspend fun upsert(musicPlaylist: MusicPlaylist)
    suspend fun getAllOfPlaylist(playlistId: Uuid): List<MusicPlaylist>
    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>)
    suspend fun delete(musicPlaylist: MusicPlaylist)
    suspend fun deleteAll(ids: List<String>)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<MusicPlaylist>
}