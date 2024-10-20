package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import java.util.*

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
): PlaylistRepository {
    override suspend fun getFromId(playlistId: UUID): Playlist? =
        playlistDataSource.getFromId(
            playlistId = playlistId,
        )

    override suspend fun getFromInformation(name: String, userId: UUID): Playlist? =
        playlistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun upsert(playlist: Playlist): Playlist =
        playlistDataSource.upsert(
            playlist = playlist,
        )

    override suspend fun deleteById(playlistId: UUID) {
        playlistDataSource.deleteById(
            playlistId = playlistId,
        )
    }

    override suspend fun allOfUser(userId: UUID, paginatedRequest: PaginatedRequest): List<Playlist> =
        playlistDataSource.allOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    override suspend fun isPlaylistPossessedByUser(userId: UUID, playlistId: UUID): Boolean =
        playlistDataSource.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )
}