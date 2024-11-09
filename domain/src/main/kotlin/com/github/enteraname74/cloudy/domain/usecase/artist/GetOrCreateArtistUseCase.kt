package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import java.util.*

class GetOrCreateArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistName: String,
        userId: UUID,
        coverPath: String?
    ): Artist =
        artistRepository.getFromInformation(
            name = artistName,
            userId = userId,
        ) ?: artistRepository.upsert(
            artist = Artist(
                userId = userId,
                name = artistName,
                coverPath = coverPath,
            )
        )
}