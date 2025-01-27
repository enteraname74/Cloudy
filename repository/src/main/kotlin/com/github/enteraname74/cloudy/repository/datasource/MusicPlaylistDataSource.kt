package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

interface MusicPlaylistDataSource {
    suspend fun upsert(musicPlaylist: MusicPlaylist)
    suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>)
    suspend fun delete(musicPlaylist: MusicPlaylist)
    suspend fun deleteAll(ids: List<String>)
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<MusicPlaylist>
}