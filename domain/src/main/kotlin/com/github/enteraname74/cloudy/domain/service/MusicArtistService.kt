package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

class MusicArtistService(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend fun allOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<MusicArtist> =
        musicArtistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}