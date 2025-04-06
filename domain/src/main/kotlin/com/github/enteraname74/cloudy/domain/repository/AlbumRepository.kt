package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

interface AlbumRepository {
    suspend fun getFromId(
        albumId: UUID,
    ): Album?
    suspend fun getFromCoverPath(coverPath: String): Album?
    suspend fun getFromInformation(
        albumName: String,
        albumArtist: String,
        userId: UUID
    ): Album?
    suspend fun getAll(albumIds: List<UUID>): List<Album>
    suspend fun upsert(
        album: Album,
        coverData: FileData?,
        username: String,
    ): Album
    suspend fun upsertAll(albums: List<Album>)
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Album>
    suspend fun deleteById(albumId: UUID)
    suspend fun deleteAll(albumIds: List<UUID>)
    suspend fun allOfArtist(artistId: UUID): List<Album>
    suspend fun isAlbumPossessedByUser(userId: UUID, albumId: UUID): Boolean
}