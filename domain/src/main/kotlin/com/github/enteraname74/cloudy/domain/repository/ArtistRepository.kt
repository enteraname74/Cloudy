package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.Artist
import java.util.*

interface ArtistRepository {
    suspend fun getFromInformation(
        name: String,
        userId: UUID,
    ): Artist?
    suspend fun upsert(artist: Artist): Artist
    suspend fun getAllOfUser(userId: UUID): List<Artist>
    suspend fun deleteById(artistId: UUID)
}