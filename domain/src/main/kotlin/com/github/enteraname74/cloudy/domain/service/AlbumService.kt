package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import java.util.UUID

class AlbumService(
    private val albumRepository: AlbumRepository,
) {
    suspend fun getAllOfUser(userId: UUID): List<Album> =
        albumRepository.getAllOfUser(userId)
}