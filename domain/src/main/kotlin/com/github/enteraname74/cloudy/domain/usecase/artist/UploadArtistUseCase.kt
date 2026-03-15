package com.github.enteraname74.cloudy.domain.usecase.artist

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.ArtistUpload
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository

class UploadArtistUseCase(
    private val artistRepository: ArtistRepository,
) {
    suspend operator fun invoke(
        artistUpload: ArtistUpload,
        user: User,
    ): Artist {
        val existingArtist: Artist? = artistRepository.getFromInformation(
            name = artistUpload.name,
            userId = user.id,
        )

        return if (existingArtist == null) {
            artistRepository.upsert(
                artist = artistUpload.toNewArtist(user.id),
                coverData = null,
                username = user.username,
            )
        } else {
            artistRepository.upsert(
                artist = existingArtist.merge(artistUpload),
                coverData = null,
                username = user.username,
            )
        }
    }
}