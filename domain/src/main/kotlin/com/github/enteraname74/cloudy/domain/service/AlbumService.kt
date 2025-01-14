package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetArtistNameForMusicUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.UUID

class AlbumService(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getArtistNameForMusicUseCase: GetArtistNameForMusicUseCase,
) {
    suspend fun getFromId(albumId: UUID): Album? =
        albumRepository.getFromId(
            albumId = albumId,
        )

    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        albumRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isAlbumPossessedByUser(
        userId: UUID,
        albumId: UUID,
    ): Boolean =
        albumRepository.isAlbumPossessedByUser(
            userId = userId,
            albumId = albumId,
        )

    suspend fun deleteAlbum(
        albumId: UUID,
    ): Boolean {
        val album: Album = albumRepository.getFromId(albumId) ?: return false

        albumRepository.deleteById(albumId)

        deleteArtistIfEmptyUseCase(
            artistId = album.artistId,
        )

        return true
    }

    suspend fun upsert(
        modifiedAlbum: Album,
        userId: UUID,
    ): Album {
        // We fetch the songs of the album to update
        val songsOfAlbum: List<Music> = musicRepository.allFromAlbum(albumId = modifiedAlbum.id)

        val previousArtist: Artist? = artistRepository.getFromId(modifiedAlbum.artistId)

        /*
        If there is no existing artist from the album's artist name, we create one.
         */
        val existingArtist: Artist = artistRepository.getFromInformation(
            name = modifiedAlbum.artistName,
            userId = userId,
        ) ?: artistRepository.upsert(
            artist = Artist(
                name = modifiedAlbum.artistName,
                userId = userId,
                coverPath = modifiedAlbum.coverPath,
            )
        )

        /*
        We link the existing artist to each song of the albums.
        We also remove the link from the previous one.
         */

        previousArtist?.let { artist ->
            musicArtistRepository.deleteAll(
                ids = songsOfAlbum.map {
                    MusicArtist(
                        musicId = it.id,
                        artistId = artist.id,
                        userId = userId,
                    ).id
                }
            )
        }
        musicArtistRepository.upsertAll(
            musicArtists = songsOfAlbum.map {
                MusicArtist(
                    musicId = it.id,
                    artistId = existingArtist.id,
                    userId = userId,
                )
            }
        )

        /*
        We check if an album with the same name and artist exist.
        If that's the case, we will redirect the songs of the modified album to this one.
        The modified album will then be deleted.
         */
        val albumInfoToUse: Album? = albumRepository.getFromInformation(
            albumName = modifiedAlbum.name,
            albumArtist = modifiedAlbum.artistName,
            userId = userId,
        )

        if (
            songsOfAlbum.firstOrNull()?.album != modifiedAlbum.name
            || songsOfAlbum.firstOrNull()?.artist != modifiedAlbum.artistName
        ) {
            val updatedSongs = songsOfAlbum.map {
                it.copy(
                    albumId = albumInfoToUse?.id ?: modifiedAlbum.id,
                    album = modifiedAlbum.name,
                    artist = getArtistNameForMusicUseCase(it.id)
                )
            }
            musicRepository.upsertAll(updatedSongs)
        }

        val savedAlbum: Album = if (albumInfoToUse != null) {
            albumRepository.deleteById(albumId = modifiedAlbum.id)
            albumRepository.upsert(
                album = albumInfoToUse.copy(
                    isInQuickAccess = modifiedAlbum.isInQuickAccess,
                )
            )
        } else {
            albumRepository.upsert(
                album = modifiedAlbum.copy(
                    artistId = existingArtist.id,
                )
            )
        }

        // If the artist of the album has changed, we check if we can delete the old one.
        deleteArtistIfEmptyUseCase(artistId = modifiedAlbum.artistId)

        return savedAlbum
    }

    /**
     * Given a list of album ids to check,
     * returns a list of all the ids of the initial list that are not present
     * in the db.
     */
    suspend fun getDeletedMusicsIds(
        idsToCheck: List<UUID>,
        userId: UUID
    ): List<UUID> {
        val allAlbumOfUser: List<UUID> = albumRepository.getAllOfUser(
            userId = userId,
        ).map { it.id }

        return idsToCheck.filterNot { it in allAlbumOfUser }
    }
}