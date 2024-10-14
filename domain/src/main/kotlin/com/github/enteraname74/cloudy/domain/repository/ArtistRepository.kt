package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.time.Instant
import java.util.*

interface ArtistRepository {
    suspend fun getFromInformation(
        name: String,
        userId: UUID,
    ): Artist?
    suspend fun getFromId(artistId: UUID): Artist?
    suspend fun isArtistPossessedByUser(userId: UUID, artistId: UUID): Boolean
    suspend fun upsert(artist: Artist): Artist
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Artist>
    suspend fun deleteById(artistId: UUID)
}