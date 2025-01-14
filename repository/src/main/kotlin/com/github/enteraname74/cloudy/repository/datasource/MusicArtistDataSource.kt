package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import java.util.UUID

interface MusicArtistDataSource {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
    suspend fun upsertAll(musicArtists: List<MusicArtist>)
    suspend fun deleteAll(ids: List<String>)
    suspend fun isInMultipleArtist(musicId: UUID): Boolean
}