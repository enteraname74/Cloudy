package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.usecase.playlist.UploadPlaylistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val uploadPlaylistUseCase: UploadPlaylistUseCase,
) {
    suspend fun getFromId(playlistId: Uuid): Playlist? =
        playlistRepository.getFromId(playlistId)

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

    suspend fun upsert(
        playlist: Playlist,
        coverData: FileData?,
        username: String,
    ): Playlist =
        playlistRepository.upsert(
            playlist = playlist,
            coverData = coverData,
            username = username,
        )

    suspend fun upload(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        user: User,
    ): CloudyResult<PlaylistWithMusics> =
        uploadPlaylistUseCase(
            playlistUpload = playlistUpload,
            user = user,
            coverData = coverData,
        )

    suspend fun deleteAll(playlistIds: List<Uuid>) {
        playlistRepository.deleteAll(playlistIds)
    }
}

