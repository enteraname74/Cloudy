package com.github.enteraname74.cloudy.domain.usecase.playlist

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import kotlin.uuid.Uuid

class AddMusicsToPlaylistUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        musicIds: List<String>,
        playlistId: Uuid,
        userId: Uuid,
    ) {
        val safeMusics: List<String> = musicRepository.getExistingIds(
            userId = userId,
            ids = musicIds,
        )
        musicPlaylistRepository.upsertAll(
            musicPlaylists = safeMusics.map {
                MusicPlaylist(
                    musicId = it,
                    playlistId = playlistId,
                    userId = userId,
                )
            }
        )
    }
}