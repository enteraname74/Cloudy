package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.util.DateUtils
import kotlin.uuid.Uuid

class GetOrCreateAlbumUseCase(
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
) {

    // TODO: Better impl
    suspend operator fun invoke(
        albumName: String,
        artistName: String,
        user: User,
        artistId: Uuid,
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
                artist = artistRepository.getFromId(artistId) ?: Artist(
                    id = Uuid.random(),
                    userId = user.id,
                    name = artistName,
                    coverPath = null,
                    addedDateMillis = DateUtils.now()
                ),
                id = Uuid.random(),
                addedDateMillis = DateUtils.now()
            ),
            coverData = coverData,
            username = user.username,
        )
}