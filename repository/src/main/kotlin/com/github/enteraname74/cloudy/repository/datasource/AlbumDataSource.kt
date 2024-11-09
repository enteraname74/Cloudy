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
    suspend fun upsertAll(albums: List<Album>)
    suspend fun getAllOfUser(userId: UUID): List<Album>
    suspend fun deleteById(albumId: UUID)
    suspend fun allOfArtist(artistId: UUID): List<Album>
}