package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import java.util.UUID

class ArtistService(
    private val artistRepository: ArtistRepository,
) {
    suspend fun getAllOfUser(userId: UUID): List<Artist> =
        artistRepository.getAllOfUser(userId)
}