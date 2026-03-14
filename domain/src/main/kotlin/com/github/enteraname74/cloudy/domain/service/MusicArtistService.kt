package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class MusicArtistService(
    private val musicArtistRepository: MusicArtistRepository,
) {
    suspend fun allOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<MusicArtist> =
        musicArtistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )
}