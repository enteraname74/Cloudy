package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.ext.joinArtists
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import java.util.*

class GetArtistNameForMusicUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(musicId: UUID): String =
        artistRepository.getArtistsOfMusic(musicId).map { it.name }.joinArtists()
}