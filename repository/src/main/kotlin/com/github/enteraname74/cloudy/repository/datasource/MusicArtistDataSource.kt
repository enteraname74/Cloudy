package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.MusicArtist

interface MusicArtistDataSource {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
}