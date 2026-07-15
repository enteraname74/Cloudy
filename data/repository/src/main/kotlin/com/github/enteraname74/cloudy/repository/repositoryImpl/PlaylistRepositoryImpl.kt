package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.FileSavingData
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import kotlin.uuid.Uuid

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
    private val coverFileManager: CoverFileManager,
): PlaylistRepository {
    override suspend fun getFromId(playlistId: Uuid): Playlist? =
        playlistDataSource.getFromId(
            playlistId = playlistId,
        )

    override suspend fun getWithMusics(playlistId: Uuid): PlaylistWithMusics? =
        playlistDataSource.getWithMusics(playlistId)

    override suspend fun getFromCoverPath(coverPath: String): Playlist? =
        playlistDataSource.getFromCoverPath(
            coverPath = coverPath,
        )

    override suspend fun getFromInformation(name: String, userId: Uuid): Playlist? =
        playlistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun getFavorite(userId: Uuid): Playlist? =
        playlistDataSource.getFavorite(userId)

    override suspend fun upsert(
        playlist: Playlist,
        coverData: FileData?,
        username: String
    ): Playlist {
        val savedId: Uuid? = coverData?.let { cover ->
            // We will delete the previous cover if any
            val previousName: String? =
                playlist
                    .coverPath
                    ?.takeIf { it.startsWith(Playlist.COVER_PATH) }
                    ?.split('/')?.last()

            previousName?.let { name ->
                coverFileManager.delete(
                    name = name,
                    username = username,
                )
            }

            coverFileManager.save(
                data = FileSavingData.UserFile(
                    username = username,
                    fileData = cover,
                )
            )
        }

        val newCoverPath = savedId?.let {
            "${Playlist.COVER_PATH}$it"
        }

        return playlistDataSource.upsert(
            playlist = playlist.copy(
                lastUpdateAtMillis = DateUtils.now(),
                coverPath = newCoverPath ?: playlist.coverPath,
            ),
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        playlistDataSource.upsertAll(
            playlists = playlists.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            },
        )

    override suspend fun deleteAll(playlistIds: List<Uuid>) {
        playlistDataSource.deleteAll(playlistIds)
    }

    override suspend fun allOfUser(userId: Uuid, paginatedRequest: PaginatedRequest): List<PlaylistWithMusics> =
        playlistDataSource.allOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    override suspend fun isPlaylistPossessedByUser(userId: Uuid, playlistId: Uuid): Boolean =
        playlistDataSource.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )
}