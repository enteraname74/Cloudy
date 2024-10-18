package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicFilePersistenceManager
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

class ArtistService(
    private val artistRepository: ArtistRepository,
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
) {
    private val musicFilePersistenceManager = MusicFilePersistenceManager()

    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Artist> =
        artistRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun upsert(
        modifiedArtist: Artist,
        userId: UUID,
    ) {
        // We fetch the songs and albums of the artist
        val songsOfArtist: List<Music> = musicRepository.allFromArtist(artistId = modifiedArtist.id)
        val albumsOfArtist: List<Album> = albumRepository.allOfArtist(artistId = modifiedArtist.id)

        /*
        We check if an artist with the same name exist.
        If that's the case, we will redirect songs and albums of the modified artist to this one.
        The modified artist will then be deleted.
         */
        val artistInfoToUse: Artist? = artistRepository.getFromInformation(
            name = modifiedArtist.name,
            userId = userId,
        )

        // The only important part of the artist in a song is its name and its id.
        if (songsOfArtist.firstOrNull()?.artist != modifiedArtist.name) {
            val updatedSongs = songsOfArtist.map {
                it.copy(
                    artistId = artistInfoToUse?.id ?: modifiedArtist.id,
                    artist = modifiedArtist.name,
                )
            }
            musicRepository.upsertAll(updatedSongs)
        }

        // Same for the albums
        if (albumsOfArtist.firstOrNull()?.artistName != modifiedArtist.name) {
            val updatedAlbums = albumsOfArtist.map {
                it.copy(
                    artistId = artistInfoToUse?.id ?: modifiedArtist.id,
                    artistName = modifiedArtist.name,
                )
            }
            albumRepository.upsertAll(updatedAlbums)
        }

        if (artistInfoToUse != null) {
            artistRepository.deleteById(artistId = modifiedArtist.id)
            artistRepository.upsert(
                artistInfoToUse.copy(
                    isInQuickAccess = modifiedArtist.isInQuickAccess,
                )
            )
        } else {
            artistRepository.upsert(modifiedArtist)
        }
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

    suspend fun deleteArtist(
        artistId: UUID,
        username: String,
    ): Boolean {
        artistRepository.getFromId(artistId = artistId) ?: return false

        // We first delete all the music files of the artist
        val songsOfArtist: List<Music> = musicRepository.allFromArtist(
            artistId = artistId,
        )
        songsOfArtist.forEach { song ->
            musicFilePersistenceManager.deleteFile(
                musicId = song.id,
                username = username,
            )
        }

        // We then delete the artist
        artistRepository.deleteById(artistId = artistId)

        return true
    }
}