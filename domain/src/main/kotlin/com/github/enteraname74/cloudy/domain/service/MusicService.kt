package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicFilePersistenceManager
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.UploadedMusicData
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetArtistNameForMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class MusicService(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val musicFilePersistenceManager: MusicFilePersistenceManager,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateAlbumUseCase: GetOrCreateAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
    private val getArtistNameForMusicUseCase: GetArtistNameForMusicUseCase,
) {

    suspend fun getFromId(musicId: UUID): Music? =
        musicRepository.getFromId(musicId = musicId)

    suspend fun saveAndCreateMissingAlbumAndArtist(
        user: User,
        musicPath: String,
        musicInformationResult: MusicInformationResult.FileMetadata,
    ): UploadedMusicData {

        val artists: List<Artist> = musicInformationResult.artists.map { artistName ->
            getOrCreateArtistUseCase(
                artistName = artistName.trim(),
                userId = user.id,
                coverPath = musicInformationResult.coverPath,
            )
        }

        val firstArtist = artists.first()

        val album: Album = getOrCreateAlbumUseCase(
            albumName = musicInformationResult.album,
            userId = user.id,
            artistId = firstArtist.id,
            artistName = firstArtist.name,
            coverPath = musicInformationResult.coverPath,
        )

        val music: Music = musicInformationToMusic(
            userId = user.id,
            albumId = album.id,
            musicPath = musicPath,
            musicInformationResult = musicInformationResult,
        )

        musicRepository.upsert(music)

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

    suspend fun update(
        modifiedMusic: Music,
        newArtistsNames: List<String>,
        userId: UUID,
    ): Music {
        // We get or create the artist of the modified music
        val newArtists: List<Artist> = newArtistsNames.map { name ->
            getOrCreateArtistUseCase(
                artistName = name,
                userId = userId,
                coverPath = modifiedMusic.coverPath,
            )
        }

        val previousArtists: List<Artist> = artistRepository
            .getArtistsOfMusic(musicId = modifiedMusic.id)

        val firstArtist: Artist =
            newArtists.firstOrNull() ?: previousArtists.first()

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbumUseCase(
            albumName = modifiedMusic.album,
            userId = userId,
            artistId = firstArtist.id,
            artistName = firstArtist.name,
            coverPath = modifiedMusic.coverPath,
        )

        if (newArtistsNames.isNotEmpty()) {
            updateArtistLinkOfMusic(
                previousArtists = previousArtists,
                newArtists = newArtists,
                userId = userId,
                newArtistsNames = newArtistsNames,
                modifiedMusic = modifiedMusic,
            )
        }

        // We update the album of the music and its artist name
        val musicWithCorrectIds = modifiedMusic.copy(
            albumId = album.id,
            artist = getArtistNameForMusicUseCase(modifiedMusic.id),
        )
        val savedMusic = musicRepository.upsert(musicWithCorrectIds)

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

        musicRepository.deleteAll(musicIds)

        relatedArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }

        musicsToDelete
            .mapNotNull { it.albumId }
            .distinct()
            .forEach { albumId ->
                deleteAlbumIfEmptyUseCase(albumId = albumId)
            }

        musicIds.forEach { musicId ->
            musicFilePersistenceManager.deleteFile(
                musicId = musicId,
                username = username,
            )
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

    private fun musicInformationToMusic(
        userId: UUID,
        albumId: UUID,
        musicPath: String,
        musicInformationResult: MusicInformationResult.FileMetadata,
    ): Music = Music(
        id = musicInformationResult.musicId,
        userId = userId,
        name = musicInformationResult.name,
        album = musicInformationResult.album,
        artist = musicInformationResult.artists.joinToString(", "),
        duration = musicInformationResult.duration,
        coverPath = musicInformationResult.coverPath,
        fingerprint = musicInformationResult.fingerprint,
        albumId = albumId,
        path = musicPath,
    )
}