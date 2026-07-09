package com.github.enteraname74.cloudy.domain.usecase.playlist

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.toCloudyResult

class UploadPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
    private val addMusicsToPlaylistUseCase: AddMusicsToPlaylistUseCase,
) {
    suspend operator fun invoke(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        user: User,
    ): CloudyResult<PlaylistWithMusics> {
        val savedPlaylist: Playlist = if (playlistUpload.isFavorite) {
            handleFavorite(
                playlistUpload = playlistUpload,
                user = user,
                coverData = coverData,
            )
        } else {
            handlePlaylist(
                playlistUpload = playlistUpload,
                user = user,
                coverData = coverData,
            )
        }

        addMusicsToPlaylistUseCase(
            musicIds = playlistUpload.musicIds,
            playlistId = savedPlaylist.id,
            userId = user.id,
        )

        return playlistRepository.getWithMusics(
            playlistId = savedPlaylist.id,
        ).toCloudyResult()
    }

    private suspend fun handlePlaylist(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        user: User,
    ): Playlist {
        val existingPlaylist: Playlist? = playlistRepository.getFromInformation(
            name = playlistUpload.name,
            userId = user.id,
        )
        return if (existingPlaylist != null) {
            playlistRepository.upsert(
                playlist = existingPlaylist.merge(playlistUpload),
                coverData = coverData,
                username = user.username,
            )
        } else {
            playlistRepository.upsert(
                playlist = playlistUpload.toNewPlaylist(user.id),
                coverData = coverData,
                username = user.username,
            )
        }
    }

    private suspend fun handleFavorite(
        playlistUpload: PlaylistUpload,
        coverData: FileData?,
        user: User,
    ): Playlist {
        val favorite: Playlist? = playlistRepository.getFavorite(userId = user.id)
        return if (favorite != null) {
            playlistRepository.upsert(
                playlist = favorite.merge(playlistUpload),
                coverData = coverData,
                username = user.username,
            )
        } else {
            playlistRepository.upsert(
                playlist = playlistUpload.toNewPlaylist(user.id),
                coverData = coverData,
                username = user.username,
            )
        }
    }
}