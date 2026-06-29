package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface MusicPlaylistDataSource {
    suspend fun upsert(musicPlaylist: MusicPlaylist)
    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>)
    suspend fun getAllOfPlaylist(playlistId: Uuid): List<MusicPlaylist>
    suspend fun delete(musicPlaylist: MusicPlaylist)
    suspend fun deleteAll(ids: List<String>)
    suspend fun deleteAllOfPlaylist(playlistId: Uuid)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<MusicPlaylist>
}