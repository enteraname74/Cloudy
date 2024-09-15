package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.Album
import java.util.*

interface AlbumDataSource {
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: UUID
    ): Album?
    suspend fun upsert(album: Album): Album
    suspend fun getAllOfUser(userId: UUID): List<Album>
    suspend fun deleteById(albumId: UUID)
}