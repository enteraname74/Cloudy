package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import kotlin.uuid.Uuid

class MusicPlaylistRepositoryImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
): MusicPlaylistRepository {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        musicPlaylistDataSource.upsert(
            musicPlaylist.copy(
                lastUpdateAtMillis = DateUtils.now(),
            )
        )
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistDataSource.upsertAll(
            musicPlaylists = musicPlaylists.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            }
        )
    }

    override suspend fun getAllOfPlaylist(playlistId: Uuid): List<MusicPlaylist> =
        musicPlaylistDataSource.getAllOfPlaylist(playlistId)

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        musicPlaylistDataSource.delete(musicPlaylist)
    }

    override suspend fun deleteAll(ids: List<String>) {
        musicPlaylistDataSource.deleteAll(ids)
    }

    override suspend fun deleteAllOfPlaylist(playlistId: Uuid) {
        musicPlaylistDataSource.deleteAllOfPlaylist(playlistId)
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<MusicPlaylist> =
        musicPlaylistDataSource.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}