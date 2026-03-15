package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.*
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UploadMusicUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

class MusicService(
    private val musicRepository: MusicRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateAlbumUseCase: GetOrCreateAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase,
) {

    suspend fun getFromId(musicId: String): Music? =
        musicRepository.getFromId(musicId = musicId)

    suspend fun getMusicFile(musicId: String, username: String): File? =
        musicRepository.getMusicFile(
            musicId = musicId,
            username = username,
        )

    suspend fun getFromCoverPath(coverPath: String): Music? =
        musicRepository.getFromCoverPath(coverPath = coverPath)

    suspend fun save(
        user: User,
        fileData: FileData,
        musicUpload: MusicUpload,
        shouldSearchForMetadata: Boolean,
    ): CloudyResult<Unit> {

        val uploadProcess: UploadProcessState = musicRepository.startUploadProcess(
            user = user,
            fileData = fileData,
            shouldSearchForMetadata = shouldSearchForMetadata,
        )

        return when (uploadProcess) {
            UploadProcessState.Error -> {
                CloudyResult.Error()
            }

            is UploadProcessState.ContinueProcess -> {
                uploadMusicUseCase(
                    musicUpload = musicUpload,
                    fingerprint = uploadProcess.fingerprint,
                    user = user,
                    musicPath = "music/${uploadProcess.fingerprint}",
                )
            }
        }
    }

    suspend fun update(
        modifiedMusic: Music,
        newCover: FileData?,
        newArtistsNames: List<String>,
        user: User,
    ): CloudyResult<Music> {
        // We get or create the artist of the modified music
        val newArtists: List<Artist> = newArtistsNames.map { name ->
            getOrCreateArtistUseCase(
                artistName = name,
                user = user,
                // If a new artist should be made from scratch on the music update, it should not have a predefined cover.
                coverData = null,
            )
        }

        val previousArtists = modifiedMusic.artists

        val firstArtist: Artist =
            newArtists.firstOrNull() ?: modifiedMusic.artists.first()

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbumUseCase(
            albumName = modifiedMusic.album.name,
            artistId = firstArtist.id,
            artistName = firstArtist.name,
            user = user,
            // If a new album should be made from scratch on the music update, it should not have a predefined cover.
            coverData = null,
        )

        if (newArtistsNames.isNotEmpty()) {
            updateArtistLinkOfMusic(
                previousArtists = previousArtists,
                newArtists = newArtists,
                userId = user.id,
                newArtistsNames = newArtistsNames,
                modifiedMusic = modifiedMusic,
            )
        }

        // We update the album of the music and its artist name
        val musicWithCorrectIds = modifiedMusic.copy(
            album = album,
        )
        val savedMusic = musicRepository.upsert(
            music = musicWithCorrectIds,
            username = user.username,
            cover = newCover,
        )

        // We check if the legacy album and artist can be deleted
        modifiedMusic.album.id.let {
            deleteAlbumIfEmptyUseCase(albumId = it)
        }

        previousArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }

        return savedMusic
    }

    private suspend fun updateArtistLinkOfMusic(
        previousArtists: List<Artist>,
        newArtistsNames: List<String>,
        newArtists: List<Artist>,
        modifiedMusic: Music,
        userId: Uuid,
    ) {
        // We remove the links between the music and the previous artists that are not in the updated list of artists:
        val artistsToUnlink: List<Artist> = previousArtists.filter { it.name !in newArtistsNames }
        musicArtistRepository.deleteAll(
            ids = artistsToUnlink.map {
                MusicArtist(
                    musicId = modifiedMusic.fingerprint,
                    artistId = it.id,
                    userId = userId,
                ).id
            }
        )

        // We set the links between the artists and the music:
        musicArtistRepository.upsertAll(
            musicArtists = newArtists.map {
                MusicArtist(
                    musicId = modifiedMusic.fingerprint,
                    artistId = it.id,
                    userId = userId,
                )
            }
        )
    }

    suspend fun deleteAll(
        musicIds: List<String>,
        username: String,
    ) {

        val musicsToDelete = musicRepository.getAll(musicIds)
        val relatedArtists: List<Artist> = musicsToDelete
            .flatMap { it.artists }
            .distinct()

        musicRepository.deleteAll(
            ids = musicIds,
            username = username,
        )

        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }

        musicsToDelete
            .map { it.album.id }
            .distinct()
            .forEach { albumId ->
                deleteAlbumIfEmptyUseCase(albumId = albumId)
            }
    }

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isMusicPossessedByUser(
        musicId: String,
        userId: Uuid
    ): Boolean =
        musicRepository.isMusicPossessedByUser(
            userId = userId,
            musicId = musicId,
        )


    /**
     * Given a list of music ids to check,
     * returns a list of all the ids of the initial list that are not present
     * in the db.
     */
    suspend fun getDeletedMusicsIds(
        idsToCheck: List<String>,
        userId: Uuid
    ): List<String> {
        val allMusicOfUser: List<String> = musicRepository.getAllOfUser(
            userId = userId,
        ).map { it.fingerprint }

        return idsToCheck.filterNot { it in allMusicOfUser }
    }
}