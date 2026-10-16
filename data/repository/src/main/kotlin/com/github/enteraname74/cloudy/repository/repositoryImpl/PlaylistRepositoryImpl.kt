package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.cover.Cover
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.datasource.PlaylistDataSource
import java.util.concurrent.CancellationException
import kotlin.uuid.Uuid

class PlaylistRepositoryImpl(
    private val playlistDataSource: PlaylistDataSource,
    private val coverDataSource: CoverDataSource,
) : PlaylistRepository {
    override suspend fun getFromUser(
        playlistId: Uuid,
        userId: Uuid,
    ): Playlist? =
        playlistDataSource.getFromUser(
            playlistId = playlistId,
            userId = userId,
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
        coversData: List<FileData?>,
        coverMode: Cover.Mode,
    ): Playlist {
        val savedIds = List(Cover.MAX_PATHS) { index ->
            coversData.getOrNull(index)?.let { saveCoverOrNull(playlist.userId, it) }
        }
        return try {
            playlistDataSource.upsert(
                playlist.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                    cover = Cover.update(
                        cover = playlist.cover,
                        mode = coverMode,
                        paths = savedIds.map { it?.let { id -> "${Playlist.COVER_PATH}$id" } },
                    ),
                ),
            )
        } catch (throwable: Throwable) {
            coverDataSource.deleteAll(playlist.userId, savedIds.mapNotNull { it?.toString() })
            throw throwable
        }
    }

    private suspend fun saveCoverOrNull(userId: Uuid, data: FileData): Uuid? = try {
        coverDataSource.save(userId, data)
    } catch (exception: CancellationException) {
        throw exception
    } catch (_: Exception) {
        null
    }

    override suspend fun upsertAll(playlists: List<Playlist>): List<Playlist> =
        playlistDataSource.upsertAll(
            playlists = playlists.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            },
        )

    override suspend fun deleteAll(playlistIds: List<Uuid>, userId: Uuid) {
        playlistDataSource.deleteAll(playlistIds, userId)
    }

    override suspend fun deleteOfUser(userId: Uuid) {
        playlistDataSource.deleteOfUser(userId)
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

    override suspend fun getDeletedPlaylistIds(
        idsToCheck: List<Uuid>,
        userId: Uuid,
    ): List<Uuid> =
        playlistDataSource.getDeletedPlaylistIds(
            idsToCheck = idsToCheck,
            userId = userId,
        )
}
