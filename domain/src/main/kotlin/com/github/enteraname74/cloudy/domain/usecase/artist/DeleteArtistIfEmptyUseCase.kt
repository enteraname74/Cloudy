package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import kotlin.uuid.Uuid

class DeleteArtistIfEmptyUseCase(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(artistId: Uuid) {
        val isArtistEmpty = musicRepository
            .allFromArtist(artistId = artistId)
            .isEmpty()

        if (isArtistEmpty) artistRepository.deleteById(artistId = artistId)
    }
}