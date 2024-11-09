package com.github.enteraname74.cloudy.domain.usecase.album

import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import java.util.*

class DeleteAlbumIfEmptyUseCase(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
) {
    suspend operator fun invoke(albumId: UUID) {
        val isAlbumEmpty = musicRepository
            .allFromAlbum(albumId = albumId).isEmpty()

        if (isAlbumEmpty) albumRepository.deleteById(albumId = albumId)
    }
}