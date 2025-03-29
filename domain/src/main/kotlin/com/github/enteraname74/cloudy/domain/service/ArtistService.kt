package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetArtistNameForMusicUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class ArtistService(
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val albumRepository: AlbumRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getArtistNameForMusicUseCase: GetArtistNameForMusicUseCase,
) {
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        artistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun update(
        modifiedArtist: Artist,
        user: User,
    ): Artist {
        // We fetch the songs and albums of the artist
        val songsOfArtist: List<Music> = musicRepository.allFromArtist(artistId = modifiedArtist.id)
        val albumsOfArtist: List<Album> = albumRepository.allOfArtist(artistId = modifiedArtist.id)

        /*
        We check if an artist with the same name exist.
        If that's the case, we will redirect songs and albums of the modified artist to this one.
        The modified artist will then be deleted.
         */
        val alreadyExistingArtist: Artist? = artistRepository.getFromInformation(
            name = modifiedArtist.name,
            userId = user.id,
        )

        val savedArtist: Artist = if (alreadyExistingArtist != null && alreadyExistingArtist.id != modifiedArtist.id) {
            artistRepository.deleteById(artistId = modifiedArtist.id)

            // We redirect the songs of the modified artist to the already existing one :
            musicArtistRepository.upsertAll(
                musicArtists = songsOfArtist.map {
                    MusicArtist(
                        musicId = it.id,
                        artistId = alreadyExistingArtist.id,
                        userId = user.id,
                    )
                }
            )

            artistRepository.upsert(
                alreadyExistingArtist.copy(
                    isInQuickAccess = modifiedArtist.isInQuickAccess,
                )
            )
        } else {
            artistRepository.upsert(modifiedArtist)
        }

        val updatedSongs = songsOfArtist.map {
            it.copy(artist = getArtistNameForMusicUseCase(it.id))
        }
        musicRepository.upsertAll(
            musicIds = updatedSongs,
            username = user.username,
        )

        // We update/redirect albums of the artist with the new information
        val updatedAlbums = albumsOfArtist.map {
            it.copy(
                artistId = savedArtist.id,
                artistName = savedArtist.name,
            )
        }
        albumRepository.upsertAll(updatedAlbums)

        return savedArtist
    }

    suspend fun getFromId(artistId: UUID): Artist? =
        artistRepository.getFromId(artistId = artistId)

    suspend fun isArtistPossessedByUser(
        artistId: UUID,
        userId: UUID,
    ): Boolean =
        artistRepository.isArtistPossessedByUser(
            userId = userId,
            artistId = artistId,
        )

    suspend fun deleteAll(
        artistIds: List<UUID>,
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

        val relatedArtists: List<Artist> = buildList {
            musicsToDelete.forEach {
                addAll(artistRepository.getArtistsOfMusic(it.id))
            }
        }.distinct()

        musicRepository.deleteAll(
            ids = musicsToDelete.map { it.id },
            username = username,
        )
        artistRepository.deleteAll(artistIds = artistIds)

        // We check if we can delete the related artists (other artists of songs).
        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(it.id)
        }
    }

    /**
     * Given a list of artist ids to check,
     * returns a list of all the ids of the initial list that are not present
     * in the db.
     */
    suspend fun getDeletedArtistIds(
        idsToCheck: List<UUID>,
        userId: UUID
    ): List<UUID> {
        val allArtistsOfUser: List<UUID> = artistRepository
            .getAllOfUser(
                userId = userId,
            ).map { it.id }

        return idsToCheck.filterNot { it in allArtistsOfUser }
    }
}