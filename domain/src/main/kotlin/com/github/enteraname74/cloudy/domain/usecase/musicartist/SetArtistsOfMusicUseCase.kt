package com.github.enteraname74.cloudy.domain.usecase.musicartist

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import kotlin.uuid.Uuid

class SetArtistsOfMusicUseCase(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend operator fun invoke(
        musicId: String,
        artistIds: List<Uuid>,
        userId: Uuid,
    ) {
        musicArtistRepository.deleteOfMusic(musicId = musicId)
        musicArtistRepository.upsertAll(
            musicArtists = artistIds.map {
                MusicArtist(
                    musicId = musicId,
                    artistId = it,
                    userId = userId,
                )
            }
        )
    }
}