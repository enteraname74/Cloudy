package com.github.enteraname74.cloudy.domain.usecase.playlist

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import kotlin.uuid.Uuid

class SetPlaylistMusicsUseCase(
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val musicRepository: MusicRepository,
) {
    suspend operator fun invoke(
        musicIds: List<String>,
        playlistId: Uuid,
        userId: Uuid,
    ) {
        val safeMusics: List<String> = musicRepository.getExistingIdsOfUser(
            userId = userId,
            ids = musicIds,
        )
        musicPlaylistRepository.deleteAllOfPlaylist(playlistId)
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