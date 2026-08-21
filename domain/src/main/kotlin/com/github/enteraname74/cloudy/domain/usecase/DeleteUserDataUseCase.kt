package com.github.enteraname74.cloudy.domain.usecase

import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import kotlin.uuid.Uuid

class DeleteUserDataUseCase(
    private val artistRepository: ArtistRepository,
    private val playlistRepository: PlaylistRepository,
    private val userRepository: UserRepository,
    private val playerRepository: PlayerRepository,
) {
    suspend operator fun invoke(userId: Uuid) {
        // Deleting artists should cascade on musics and albums.
        artistRepository.deleteOfUser(userId)
        playlistRepository.deleteOfUser(userId)
        userRepository.clearUserDirectory(userId)
        // TODO: broadcast deleted played lists or updated played lists because of user deletion
        playerRepository.deleteAllIfEmpty()
    }
}