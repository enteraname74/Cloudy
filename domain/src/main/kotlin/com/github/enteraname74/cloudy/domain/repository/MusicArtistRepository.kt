package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

interface MusicArtistRepository {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
    suspend fun upsertAll(musicArtists: List<MusicArtist>)
    suspend fun deleteAll(ids: List<String>)
    suspend fun isInMultipleArtist(musicId: UUID): Boolean
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<MusicArtist>
}