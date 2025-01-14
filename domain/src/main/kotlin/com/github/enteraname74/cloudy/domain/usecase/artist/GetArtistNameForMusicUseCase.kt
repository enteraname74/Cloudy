package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import java.util.UUID

class GetArtistNameForMusicUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(musicId: UUID): String =
        artistRepository.getArtistsOfMusic(musicId).joinToString(", ") {
            it.name
        }
}