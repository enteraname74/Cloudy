package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.MusicArtist

interface MusicArtistRepository {
    suspend fun upsert(musicArtist: MusicArtist)
    suspend fun delete(musicArtist: MusicArtist)
}