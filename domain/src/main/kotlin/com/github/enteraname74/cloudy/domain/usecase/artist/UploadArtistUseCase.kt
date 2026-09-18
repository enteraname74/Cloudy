package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.artist.ArtistUpload
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import kotlin.uuid.Uuid

class UploadArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistUpload: ArtistUpload,
        userId: Uuid,
    ): Artist {
        val existingArtist: Artist? = artistRepository.getFromInformation(
            name = artistUpload.name,
            userId = userId,
        )

        return if (existingArtist == null) {
            artistRepository.upsert(
                artist = artistUpload.toNewArtist(userId),
                coverData = null,
            )
        } else {
            artistRepository.upsert(
                artist = existingArtist.merge(artistUpload),
                coverData = null,
            )
        }
    }
}