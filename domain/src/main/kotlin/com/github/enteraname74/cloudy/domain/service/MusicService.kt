package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.ext.joinArtists
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.*
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetArtistNameForMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import java.util.*

class MusicService(
    private val musicRepository: MusicRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateAlbumUseCase: GetOrCreateAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val getArtistNameForMusicUseCase: GetArtistNameForMusicUseCase,
) {

    suspend fun getFromId(musicId: UUID): Music? =
        musicRepository.getFromId(musicId = musicId)

    suspend fun getMusicFile(musicId: UUID, username: String): File? =
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
            albumId = album.id,
            musicPath = "music/${metadata.musicId}",
            metadata = metadata,
        )

        musicRepository.saveMusicFileToDbAfterUploadProcess(music)

        artists.forEach { artist ->
            musicArtistRepository.upsert(
                musicArtist = MusicArtist(
                    musicId = music.id,
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
                    albumId = uploadProcess.updatedMusic.albumId ?: return CloudyResult.Error()
                ) ?: return CloudyResult.Error()

                return CloudyResult.Success(
                    UploadedMusicData(
                        music = uploadProcess.updatedMusic,
                        album = album,
                        artists = artistRepository.getArtistsOfMusic(uploadProcess.updatedMusic.id),
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

        val previousArtists: List<Artist> = artistRepository
            .getArtistsOfMusic(musicId = modifiedMusic.id)

        val firstArtist: Artist =
            newArtists.firstOrNull() ?: previousArtists.first()

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbumUseCase(
            albumName = modifiedMusic.album,
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
            albumId = album.id,
            artist = getArtistNameForMusicUseCase(modifiedMusic.id),
        )
        val savedMusic = musicRepository.upsert(
            music = musicWithCorrectIds,
            username = user.username,
            cover = newCover,
        )

        // We check if the legacy album and artist can be deleted
        modifiedMusic.albumId?.let {
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
        userId: UUID,
    ) {
        // We remove the links between the music and the previous artists that are not in the updated list of artists:
        val artistsToUnlink: List<Artist> = previousArtists.filter { it.name !in newArtistsNames }
        musicArtistRepository.deleteAll(
            ids = artistsToUnlink.map {
                MusicArtist(
                    musicId = modifiedMusic.id,
                    artistId = it.id,
                    userId = userId,
                ).id
            }
        )

        // We set the links between the artists and the music:
        musicArtistRepository.upsertAll(
            musicArtists = newArtists.map {
                MusicArtist(
                    musicId = modifiedMusic.id,
                    artistId = it.id,
                    userId = userId,
                )
            }
        )
    }

    suspend fun deleteAll(
        musicIds: List<UUID>,
        username: String,
    ) {

        val musicsToDelete = musicRepository.getAll(musicIds)
        val relatedArtists: List<Artist> = buildList {
            musicsToDelete.forEach { music ->
                addAll(artistRepository.getArtistsOfMusic(musicId = music.id))
            }
        }.distinct()

        musicRepository.deleteAll(
            ids = musicIds,
            username = username,
        )

        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }

        musicsToDelete
            .mapNotNull { it.albumId }
            .distinct()
            .forEach { albumId ->
                deleteAlbumIfEmptyUseCase(albumId = albumId)
            }
    }

    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicRepository.getAllOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isMusicPossessedByUser(
        musicId: UUID,
        userId: UUID
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
        idsToCheck: List<UUID>,
        userId: UUID
    ): List<UUID> {
        val allMusicOfUser: List<UUID> = musicRepository.getAllOfUser(
            userId = userId,
        ).map { it.id }

        return idsToCheck.filterNot { it in allMusicOfUser }
    }


    private fun musicMetadataToMusic(
        userId: UUID,
        albumId: UUID,
        musicPath: String,
        metadata: MusicInformationRetriever.Metadata,
    ): Music = Music(
        id = metadata.musicId,
        userId = userId,
        name = metadata.name,
        album = metadata.album,
        artist = metadata.artists.joinArtists(),
        duration = metadata.duration,
        coverPath = metadata.coverPath,
        fingerprint = metadata.fingerprint,
        albumId = albumId,
        path = musicPath,
    )
}