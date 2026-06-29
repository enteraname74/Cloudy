package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.*
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class AlbumService(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
) {
    suspend fun getFromId(albumId: Uuid): Album? =
        albumRepository.getFromId(
            albumId = albumId,
        )

    suspend fun getFromCoverPath(coverPath: String): Album? =
        albumRepository.getFromCoverPath(
            coverPath = coverPath,
        )

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Album> =
        albumRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isAlbumPossessedByUser(
        userId: Uuid,
        albumId: Uuid,
    ): Boolean =
        albumRepository.isAlbumPossessedByUser(
            userId = userId,
            albumId = albumId,
        )

    suspend fun deleteAll(
        albumIds: List<Uuid>,
        username: String,
    ) {
        val albumsToDelete: List<Album> = albumRepository.getAll(albumIds)
        val musicsToDelete: List<Music> = buildList {
            albumsToDelete.forEach {
                addAll(
                    musicRepository.allFromAlbum(
                        albumId = it.id
                    )
                )
            }
        }

        val relatedArtists: List<Artist> = musicsToDelete
            .flatMap { it.artists }
            .distinct()

        /*
        Even if the deletion of albums delete the musics,
         we need to ensure that the files will be also deleted.
         */
        musicRepository.deleteAll(
            ids = musicsToDelete.map { it.fingerprint },
            username = username,
        )

        albumRepository.deleteAll(albumIds)

        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }
    }

    suspend fun update(
        modifiedAlbum: Album,
        coverData: FileData?,
        user: User,
    ): Album {
        // We fetch the songs of the album to update and its artist
        val songsOfAlbum: List<Music> = musicRepository.allFromAlbum(albumId = modifiedAlbum.id)
        val previousArtist: Artist? = artistRepository.getFromId(modifiedAlbum.artist.id)

        /*
        If there is no existing artist from the album's artist name, we create one.
         */
        val existingArtist: Artist = getOrCreateArtistUseCase(
            artistName = modifiedAlbum.artist.name,
            user = user,
            coverData = null,
        )

        /*
        We link the existing artist to each song of the albums.
        We also remove the link from the previous one.
         */
        previousArtist?.let { artist ->
            musicArtistRepository.deleteAll(
                ids = songsOfAlbum.map {
                    MusicArtist(
                        musicId = it.fingerprint,
                        artistId = artist.id,
                        userId = user.id,
                    ).id
                }
            )
        }
        musicArtistRepository.upsertAll(
            musicArtists = songsOfAlbum.map {
                MusicArtist(
                    musicId = it.fingerprint,
                    artistId = existingArtist.id,
                    userId = user.id,
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
            albumArtist = modifiedAlbum.artist.name,
            userId = user.id,
        )

        val updatedSongs = songsOfAlbum.map {
            it.copy(
                album = albumInfoToUse ?: modifiedAlbum,
                // TODO: update artists?
            )
        }
        musicRepository.upsertAll(
            musicIds = updatedSongs,
            username = user.username,
        )

        val savedAlbum: Album = if (albumInfoToUse != null && albumInfoToUse.id != modifiedAlbum.id) {
            albumRepository.deleteById(albumId = modifiedAlbum.id)
            albumRepository.upsert(
                album = albumInfoToUse.copy(
                    isInQuickAccess = modifiedAlbum.isInQuickAccess,
                ),
                coverData = coverData,
                username = user.username,
            )
        } else {
            albumRepository.upsert(
                album = modifiedAlbum.copy(
                    artist = existingArtist,
                ),
                coverData = coverData,
                username = user.username,
            )
        }

        // If the artist of the album has changed, we check if we can delete the old one.
        deleteArtistIfEmptyUseCase(artistId = modifiedAlbum.artist.id)

        return savedAlbum
    }

    /**
     * Given a list of album ids to check,
     * returns a list of all the ids of the initial list that are not present
     * in the db.
     */
    suspend fun getDeletedMusicsIds(
        idsToCheck: List<Uuid>,
        userId: Uuid
    ): List<Uuid> {
        val allAlbumOfUser: List<Uuid> = albumRepository.getAllOfUser(
            userId = userId,
        ).map { it.id }

        return idsToCheck.filterNot { it in allAlbumOfUser }
    }
}