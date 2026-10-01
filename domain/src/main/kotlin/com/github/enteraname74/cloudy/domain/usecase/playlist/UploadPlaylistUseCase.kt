package com.github.enteraname74.cloudy.domain.usecase.playlist

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudyResult
import kotlin.uuid.Uuid

class UploadPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
    private val setPlaylistMusicsUseCase: SetPlaylistMusicsUseCase,
) {
    suspend operator fun invoke(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        userId: Uuid,
    ): CloudyResult<PlaylistWithMusics> {
        val savedPlaylist: Playlist = if (playlistUpload.isFavorite) {
            handleFavorite(
                playlistUpload = playlistUpload,
                userId = userId,
                coverData = coverData,
            )
        } else {
            handlePlaylist(
                playlistUpload = playlistUpload,
                userId = userId,
                coverData = coverData,
            )
        }

        setPlaylistMusicsUseCase(
            musicIds = playlistUpload.musicIds,
            playlistId = savedPlaylist.id,
            userId = userId,
        )

        return playlistRepository.getWithMusics(
            playlistId = savedPlaylist.id,
        ).toCloudyResult()
    }

    private suspend fun handlePlaylist(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        userId: Uuid,
    ): Playlist {
        val existingPlaylist: Playlist? = playlistUpload.id?.let {
            playlistRepository.getFromUser(
                playlistId = it,
                userId = userId,
            )
        } ?: playlistRepository.getFromInformation(
            name = playlistUpload.name,
            userId = userId,
        )
        return if (existingPlaylist != null) {
            playlistRepository.upsert(
                playlist = existingPlaylist.merge(playlistUpload),
                coverData = coverData,
            )
        } else {
            playlistRepository.upsert(
                playlist = playlistUpload.toNewPlaylist(userId),
                coverData = coverData,
            )
        }
    }

    private suspend fun handleFavorite(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        userId: Uuid,
    ): Playlist {
        val favorite: Playlist? = playlistRepository.getFavorite(userId = userId)
        return if (favorite != null) {
            playlistRepository.upsert(
                playlist = favorite.merge(playlistUpload),
                coverData = coverData,
            )
        } else {
            playlistRepository.upsert(
                playlist = playlistUpload.toNewPlaylist(userId).copy(
                    isFavorite = true,
                ),
                coverData = coverData,
            )
        }
    }
}