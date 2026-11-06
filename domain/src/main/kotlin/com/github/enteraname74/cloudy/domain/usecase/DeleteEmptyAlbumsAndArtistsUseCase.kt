package com.github.enteraname74.cloudy.domain.usecase

import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import kotlin.uuid.Uuid

class DeleteEmptyAlbumsAndArtistsUseCase(
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val coverRepository: CoverRepository,
) {
    suspend operator fun invoke(userId: Uuid) {
        // Albums first: an artist is only empty if it has no music and no album.
        albumRepository.deleteAllEmpty()
        artistRepository.deleteAllEmpty()
        coverRepository.deletedUnusedCovers(userId)
    }
}