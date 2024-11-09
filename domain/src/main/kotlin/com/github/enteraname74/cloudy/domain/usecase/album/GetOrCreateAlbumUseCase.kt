package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import java.util.*

class GetOrCreateAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
        userId: UUID,
        artistId: UUID,
        coverPath: String?
    ): Album =
        albumRepository.getFromInformation(
            albumName = albumName,
            albumArtist = artistName,
            userId = userId,
        ) ?: albumRepository.upsert(
            album = Album(
                userId = userId,
                name = albumName,
                coverPath = coverPath,
                artistId = artistId,
                artistName = artistName,
            )
        )
}