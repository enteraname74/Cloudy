package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

interface MusicArtistRepository {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
    suspend fun upsertAll(musicArtists: List<MusicArtist>)
    suspend fun deleteAll(ids: List<String>)

    suspend fun deleteOfMusic(musicId: MusicId)
    suspend fun isInMultipleArtist(musicId: MusicId): Boolean
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<MusicArtist>
}