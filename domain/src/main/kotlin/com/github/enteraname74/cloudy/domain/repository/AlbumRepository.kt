package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

interface AlbumRepository {
    suspend fun getFromId(
        albumId: UUID,
    ): Album?
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: UUID
    ): Album?
    suspend fun upsert(album: Album): Album
    suspend fun upsertAll(albums: List<Album>)
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Album>
    suspend fun deleteById(albumId: UUID)
    suspend fun allOfArtist(artistId: UUID): List<Album>
    suspend fun isAlbumPossessedByUser(userId: UUID, albumId: UUID): Boolean
}