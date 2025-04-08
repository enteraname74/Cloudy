package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.ext.toUUID
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
    private val coverFileManager: CoverFileManager,
): PlaylistRepository {
    override suspend fun getFromId(playlistId: UUID): Playlist? =
        playlistDataSource.getFromId(
            playlistId = playlistId,
        )

    override suspend fun getFromCoverPath(coverPath: String): Playlist? =
        playlistDataSource.getFromCoverPath(
            coverPath = coverPath,
        )

    override suspend fun getFromInformation(name: String, userId: UUID): Playlist? =
        playlistDataSource.getFromInformation(
            name = name,
            userId = userId,
        )

    override suspend fun upsert(
        playlist: Playlist,
        coverData: FileData?,
        username: String
    ): Playlist {
        val savedId: UUID? = coverData?.let {
            // We will delete the previous cover if any
            val previousId: UUID? =
                playlist
                    .coverPath
                    ?.takeIf { it.startsWith(Playlist.COVER_PATH) }
                    ?.split('/')?.last()?.toUUID()

            previousId?.let { id ->
                coverFileManager.delete(
                    id = id,
                    username = username,
                )
            }

            coverFileManager.save(
                username = username,
                fileData = it,
            )
        }

        val newCoverPath = savedId?.let {
            "${Playlist.COVER_PATH}$it"
        }

        return playlistDataSource.upsert(
            playlist = playlist.copy(
                lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                coverPath = newCoverPath ?: playlist.coverPath,
            ),
        )
    }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        playlistDataSource.upsertAll(
            playlists = playlists.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                )
            },
        )

    override suspend fun deleteById(playlistId: UUID) =
        playlistDataSource.deleteById(
            playlistId = playlistId,
        )

    override suspend fun deleteAll(playlistIds: List<UUID>) {
        playlistDataSource.deleteAll(playlistIds)
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