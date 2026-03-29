package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlin.uuid.Uuid

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
                id = Uuid.random(),
                addedDateMillis = DateUtils.now(),
            ),
            coverData = coverData,
            username = user.username,
        )
}