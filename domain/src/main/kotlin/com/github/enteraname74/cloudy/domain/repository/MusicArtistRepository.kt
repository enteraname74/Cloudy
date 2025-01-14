package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import java.util.UUID

interface MusicArtistRepository {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
    suspend fun upsertAll(musicArtists: List<MusicArtist>)
    suspend fun deleteAll(ids: List<String>)
    suspend fun isInMultipleArtist(musicId: UUID): Boolean
}