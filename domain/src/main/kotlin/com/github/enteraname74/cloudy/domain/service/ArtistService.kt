package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class ArtistService(
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
) {
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        artistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun getFromId(artistId: Uuid): Artist? =
        artistRepository.getFromId(artistId = artistId)

    suspend fun getFromCoverPath(coverPath: String): Artist? =
        artistRepository.getFromCoverPath(coverPath)

    suspend fun isArtistPossessedByUser(
        artistId: Uuid,
        userId: Uuid,
    ): Boolean =
        artistRepository.isArtistPossessedByUser(
            userId = userId,
            artistId = artistId,
        )

    suspend fun deleteAll(
        artistIds: List<Uuid>,
        username: String,
    ) {
        val musicsToDelete: List<Music> = buildList {
            artistIds.forEach { artistId ->
                addAll(
                    musicRepository.allFromArtist(
                        artistId = artistId,
                    )
                )
            }
        }.distinct()

        val relatedArtists: List<Artist> = musicsToDelete
            .flatMap { it.artists }
            .distinct()

        musicRepository.deleteAll(
            ids = musicsToDelete.map { it.fingerprint },
            username = username,
        )
        artistRepository.deleteAll(artistIds = artistIds)

        // We check if we can delete the related artists (other artists of songs).
        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(it.id)
        }
    }

}