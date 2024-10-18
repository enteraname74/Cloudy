package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import java.util.*

class DeleteArtistIfEmptyUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artistId: UUID) {
        val isArtistEmpty = musicRepository
            .allFromArtist(artistId = artistId).isEmpty()

        println("Artist $artistId is empty? $isArtistEmpty")

        if (isArtistEmpty) artistRepository.deleteById(artistId = artistId)
    }
}