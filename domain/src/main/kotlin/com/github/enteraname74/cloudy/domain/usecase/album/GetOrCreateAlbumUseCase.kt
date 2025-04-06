package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import java.util.*

class GetOrCreateAlbumUseCase(
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
        user: User,
        artistId: UUID,
        coverData: FileData?,
    ): Album =
        albumRepository.getFromInformation(
            albumName = albumName,
            albumArtist = artistName,
            userId = user.id,
        ) ?: albumRepository.upsert(
            album = Album(
                userId = user.id,
                name = albumName,
                coverPath = null,
                artistId = artistId,
                artistName = artistName,
            ),
            coverData = coverData,
            username = user.username,
        )
}