package com.github.enteraname74.cloudy.domain.usecase

import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository

class DeleteEmptyAlbumsAndArtistsUseCase(
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke() {
        artistRepository.deleteAllEmpty()
        albumRepository.deleteAllEmpty()
    }
}