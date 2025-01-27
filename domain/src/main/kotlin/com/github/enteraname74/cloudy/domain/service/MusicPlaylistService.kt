package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class MusicPlaylistService(
    private val musicPlaylistRepository: MusicPlaylistRepository,
) {
    suspend fun allOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<MusicPlaylist> =
        musicPlaylistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun upsert(
        musicPlaylist: MusicPlaylist,
    ) {
        musicPlaylistRepository.upsert(musicPlaylist)
    }
}