package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.album.Album
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class AlbumService(
    private val albumRepository: AlbumRepository,
    private val musicRepository: MusicRepository,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
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

}