package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

class MusicPlaylistRepositoryImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
): MusicPlaylistRepository {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        musicPlaylistDataSource.upsert(
            musicPlaylist.copy(
                lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
            )
        )
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistDataSource.upsertAll(
            musicPlaylists = musicPlaylists.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                )
            }
        )
    }

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        musicPlaylistDataSource.delete(musicPlaylist)
    }

    override suspend fun deleteAll(ids: List<String>) {
        musicPlaylistDataSource.deleteAll(ids)
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest
    ): List<MusicPlaylist> =
        musicPlaylistDataSource.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}