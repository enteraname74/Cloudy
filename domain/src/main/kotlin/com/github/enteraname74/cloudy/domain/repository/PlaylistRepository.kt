package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Playlist
import java.util.*

interface PlaylistRepository {
    suspend fun upsert(playlist: Playlist)
    suspend fun deleteById(playlistId: UUID)
    suspend fun doesPlaylistExists(playlistId: UUID): Boolean
    suspend fun isPlaylistPossessedByUser(userId: UUID, playlistId: UUID): Boolean
}