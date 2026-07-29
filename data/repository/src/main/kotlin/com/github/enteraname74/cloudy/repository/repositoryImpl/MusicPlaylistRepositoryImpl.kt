package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.MusicPlaylistDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import kotlin.uuid.Uuid

class MusicPlaylistRepositoryImpl(
    private val musicPlaylistDataSource: MusicPlaylistDataSource,
    private val playlistDataSource: PlaylistDataSource,
) : MusicPlaylistRepository {
    override suspend fun upsert(musicPlaylist: MusicPlaylist) {
        val updatedAt = DateUtils.now()
        musicPlaylistDataSource.upsert(
            musicPlaylist.copy(
                lastUpdateAtMillis = updatedAt,
            )
        )
        playlistDataSource.updateLastUpdatedAtField(
            playlistIds = listOf(musicPlaylist.playlistId),
            updatedAt = updatedAt,
        )
    }

    override suspend fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        val updatedAt = DateUtils.now()
        musicPlaylistDataSource.upsertAll(
            musicPlaylists = musicPlaylists.map {
                it.copy(
                    lastUpdateAtMillis = updatedAt,
                )
            }
        )
        playlistDataSource.updateLastUpdatedAtField(
            playlistIds = musicPlaylists.map { it.playlistId },
            updatedAt = updatedAt,
        )
    }

    override suspend fun getAllOfPlaylist(playlistId: Uuid): List<MusicPlaylist> =
        musicPlaylistDataSource.getAllOfPlaylist(playlistId)

    override suspend fun delete(musicPlaylist: MusicPlaylist) {
        musicPlaylistDataSource.delete(musicPlaylist)
        playlistDataSource.updateLastUpdatedAtField(
            playlistIds = listOf(musicPlaylist.playlistId),
            updatedAt = DateUtils.now(),
        )
    }

    override suspend fun deleteAll(musicPlaylists: List<MusicPlaylist>) {
        musicPlaylistDataSource.deleteAll(musicPlaylists.map { it.id })
        playlistDataSource.updateLastUpdatedAtField(
            playlistIds = musicPlaylists.map { it.playlistId },
            updatedAt = DateUtils.now(),
        )
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