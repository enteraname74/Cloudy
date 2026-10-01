package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.usecase.playlist.UploadPlaylistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val uploadPlaylistUseCase: UploadPlaylistUseCase,
    private val coverRepository: CoverRepository,
) {
    suspend fun getFromCoverPath(coverPath: String): Playlist? =
        playlistRepository.getFromCoverPath(coverPath)

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlaylistWithMusics> =
        playlistRepository.allOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isPlaylistPossessedByUser(
        userId: Uuid,
        playlistId: Uuid,
    ): Boolean =
        playlistRepository.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )

    suspend fun upload(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        userId: Uuid,
    ): CloudyResult<PlaylistWithMusics> =
        uploadPlaylistUseCase(
            playlistUpload = playlistUpload,
            userId = userId,
            coverData = coverData,
        )

    suspend fun deleteAll(playlistIds: List<Uuid>, userId: Uuid) {
        playlistRepository.deleteAll(playlistIds, userId)
        coverRepository.deletedUnusedCovers(userId)
    }

    suspend fun getDeletedPlaylistIds(
        idsToCheck: List<Uuid>,
        userId: Uuid,
    ): List<Uuid> =
        playlistRepository.getDeletedPlaylistIds(
            idsToCheck = idsToCheck,
            userId = userId,
        )
}

