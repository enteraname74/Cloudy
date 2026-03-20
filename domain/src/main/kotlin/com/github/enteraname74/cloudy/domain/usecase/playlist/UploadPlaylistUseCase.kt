package com.github.enteraname74.cloudy.domain.usecase.playlist

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository

class UploadPlaylistUseCase(
    private val playlistRepository: PlaylistRepository,
    private val addMusicsToPlaylistUseCase: AddMusicsToPlaylistUseCase,
) {
    suspend operator fun invoke(
        playlistUpload: PlaylistUpload,
        user: User,
    ) {
        val savedPlaylist: Playlist = if (playlistUpload.isFavorite) {
            val favorite: Playlist? = playlistRepository.getFavorite(userId = user.id)
            if (favorite != null) {
                playlistRepository.upsert(
                    playlist = favorite.merge(playlistUpload),
                    coverData = null,
                    username = user.username,
                )
            } else {
                playlistRepository.upsert(
                    playlist = playlistUpload.toNewPlaylist(user.id),
                    coverData = null,
                    username = user.username,
                )
            }
        } else {
            playlistRepository.upsert(
                playlist = playlistUpload.toNewPlaylist(user.id),
                coverData = null,
                username = user.username,
            )
        }

        addMusicsToPlaylistUseCase(
            musicIds = playlistUpload.musicIds,
            playlistId = savedPlaylist.id,
            userId = user.id,
        )
    }
}