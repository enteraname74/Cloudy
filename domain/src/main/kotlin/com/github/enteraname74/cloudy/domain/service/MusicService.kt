package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.*
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

class MusicService(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateAlbumUseCase: GetOrCreateAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
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

    private suspend fun saveMusicAndCreateMissingAlbumAndArtist(
        user: User,
        metadata: MusicInformationRetriever.Metadata,
        artistCover: FileData?,
        albumCover: FileData?,
    ): UploadedMusicData {
        val artists: List<Artist> = metadata.artists.map { artistName ->
            getOrCreateArtistUseCase(
                artistName = artistName.trim(),
                user = user,
                coverData = artistCover,
            )
        }

        val firstArtist = artists.first()

        val album: Album = getOrCreateAlbumUseCase(
            albumName = metadata.album,
            artistId = firstArtist.id,
            artistName = firstArtist.name,
            coverData = albumCover,
            user = user,
        )

        // TODO: Improve music path definition
        val music: Music = musicMetadataToMusic(
            userId = user.id,
            musicPath = "music/${metadata.fingerprint}",
            metadata = metadata,
        )

        musicRepository.saveMusicFileToDbAfterUploadProcess(music)

        artists.forEach { artist ->
            musicArtistRepository.upsert(
                musicArtist = MusicArtist(
                    musicId = music.fingerprint,
                    artistId = artist.id,
                    userId = user.id,
                )
            )
        }


        return UploadedMusicData(
            music = music,
            artists = artists,
            album = album,
        )
    }

    suspend fun save(
        user: User,
        fileData: FileData,
        customMusicMetadata: CustomMusicMetadata?,
        shouldSearchForMetadata: Boolean,
    ): CloudyResult<UploadedMusicData> {

        val uploadProcess: UploadProcessState = musicRepository.startUploadProcess(
            user = user,
            fileData = fileData,
            customMusicMetadata = customMusicMetadata,
            shouldSearchForMetadata = shouldSearchForMetadata,
        )

        when (uploadProcess) {
            UploadProcessState.Error -> {
                return CloudyResult.Error()
            }

            is UploadProcessState.AlreadyExisting -> {
                val album: Album = albumRepository.getFromId(
                    albumId = uploadProcess.updatedMusic.album.id
                ) ?: return CloudyResult.Error()

                return CloudyResult.Success(
                    UploadedMusicData(
                        music = uploadProcess.updatedMusic,
                        album = album,
                        artists = uploadProcess.updatedMusic.artists,
                    )
                )
            }

            is UploadProcessState.ContinueProcess -> {
                return CloudyResult.Success(
                    saveMusicAndCreateMissingAlbumAndArtist(
                        user = user,
                        metadata = uploadProcess.metadata,
                        // TODO: Add possibility to set an artist/album cover from the sent music.
                        artistCover = null,
                        albumCover = null,
                    )
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


    // TODO: Better impl
    private fun musicMetadataToMusic(
        userId: Uuid,
        musicPath: String,
        metadata: MusicInformationRetriever.Metadata,
    ): Music {
        val artists = metadata.artists.map {
            Artist(
                id = Uuid.random(),
                userId = userId,
                name = it,
                coverPath = null,
                addedDateMillis = DateUtils.now()
            )
        }

        return Music(
            userId = userId,
            name = metadata.name,
            album = Album(
                id = Uuid.random(),
                userId = userId,
                name = metadata.album,
                coverPath = null,
                addedDateMillis = DateUtils.now(),
                artist = artists.first()
            ),
            artists = artists,
            duration = metadata.duration,
            coverPath = metadata.coverPath,
            fingerprint = metadata.fingerprint,
            path = musicPath,
            // TODO: Add album position from metadata
            albumPosition = null,
            addedDateMillis = DateUtils.now(),
        )
    }
}