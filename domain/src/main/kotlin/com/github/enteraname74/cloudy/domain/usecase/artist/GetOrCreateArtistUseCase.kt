package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import java.util.*

class GetOrCreateArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistName: String,
        user: User,
        coverData: FileData?,
    ): Artist =
        artistRepository.getFromInformation(
            name = artistName,
            userId = user.id,
        ) ?: artistRepository.upsert(
            artist = Artist(
                userId = user.id,
                name = artistName,
                coverPath = null,
            ),
            coverData = coverData,
            username = user.username,
        )
}