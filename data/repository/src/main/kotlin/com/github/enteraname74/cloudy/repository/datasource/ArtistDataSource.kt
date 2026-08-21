package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface ArtistDataSource {
    suspend fun getFromInformation(
        name: String,
        userId: Uuid,
    ): Artist?
    suspend fun getFromId(artistId: Uuid): Artist?
    suspend fun getFromUser(
        artistId: Uuid,
        userId: Uuid,
    ): Artist?
    suspend fun getFromCoverPath(coverPath: String): Artist?
    suspend fun isArtistPossessedByUser(userId: Uuid, artistId: Uuid): Boolean
    suspend fun upsert(artist: Artist): Artist
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Artist>
    suspend fun deleteById(artistId: Uuid): Boolean
    suspend fun deleteAll(artistIds: List<Uuid>)

    suspend fun deleteOfUser(userId: Uuid)

    suspend fun deleteAllEmpty()
}