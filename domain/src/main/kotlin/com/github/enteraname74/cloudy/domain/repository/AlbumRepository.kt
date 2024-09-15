package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Album
import java.util.UUID

interface AlbumRepository {
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: UUID
    ): Album?
    suspend fun upsert(album: Album): Album
    suspend fun getAllOfUser(userId: UUID): List<Album>
    suspend fun deleteById(albumId: UUID)
}