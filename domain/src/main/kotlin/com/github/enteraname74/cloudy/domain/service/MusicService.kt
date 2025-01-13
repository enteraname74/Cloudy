package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class MusicService(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val getOrCreateArtistUseCase: GetOrCreateArtistUseCase,
    private val deleteArtistIfEmptyUseCase: DeleteArtistIfEmptyUseCase,
    private val getOrCreateAlbumUseCase: GetOrCreateAlbumUseCase,
    private val deleteAlbumIfEmptyUseCase: DeleteAlbumIfEmptyUseCase,
) {

    suspend fun getFromId(musicId: UUID): Music? =
        musicRepository.getFromId(musicId = musicId)

    suspend fun saveAndCreateMissingAlbumAndArtist(
        user: User,
        musicPath: String,
        musicInformationResult: MusicInformationResult.FileMetadata,
    ): Music {

        val artist: Artist = getOrCreateArtistUseCase(
            artistName = musicInformationResult.artist,
            userId = user.id,
            coverPath = musicInformationResult.coverPath,
        )

        val album: Album = getOrCreateAlbumUseCase(
            albumName = musicInformationResult.album,
            userId = user.id,
            artistId = artist.id,
            artistName = artist.name,
            coverPath = musicInformationResult.coverPath,
        )

        val music: Music = musicInformationToMusic(
            userId = user.id,
            albumId = album.id,
            musicPath = musicPath,
            musicInformationResult = musicInformationResult,
        )

        musicRepository.upsert(music)
        return music
    }

    suspend fun update(
        modifiedMusic: Music,
        newArtists: List<String>,
        userId: UUID,
    ): Music {
        // We get or create the artist of the modified music
        val artists: List<Artist> = newArtists.map { name ->
            getOrCreateArtistUseCase(
                artistName = name,
                userId = userId,
                coverPath = modifiedMusic.coverPath,
            )
        }

        val previousArtists: List<Artist> = artistRepository
            .getArtistsOfMusic(musicId = modifiedMusic.id)

        val firstArtist: Artist =
            artists.firstOrNull() ?: previousArtists.first()

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbumUseCase(
            albumName = modifiedMusic.album,
            userId = userId,
            artistId = firstArtist.id,
            artistName = firstArtist.name,
            coverPath = modifiedMusic.coverPath,
        )

        // We update the album of the music
        val musicWithCorrectIds = modifiedMusic.copy(
            albumId = album.id,
            artist = newArtists.takeIf { it.isNotEmpty() }?.joinToString(", ")
                ?: previousArtists.joinToString(", "),
        )
        val savedMusic = musicRepository.upsert(musicWithCorrectIds)

        // We set the links between the artists and the music:
        artists.forEach {
            musicArtistRepository.upsert(
                musicArtist = MusicArtist(
                    musicId = modifiedMusic.id,
                    artistId = it.id,
                )
            )
        }

        // We check if the legacy album and artist can be deleted
        deleteAlbumIfEmptyUseCase(albumId = modifiedMusic.albumId)

        previousArtists.forEach {
            deleteArtistIfEmptyUseCase(artistId = it.id)
        }

        return savedMusic
    }

    suspend fun deleteAll(musicIds: List<UUID>) {
        musicIds.forEach { musicId ->
            val music: Music = musicRepository.getFromId(musicId = musicId) ?: return
            val artistsOfMusic: List<Artist> = artistRepository.getArtistsOfMusic(musicId = musicId)

            // We check if we can delete the album of the music:
            deleteAlbumIfEmptyUseCase(albumId = music.albumId)

            // We then check if we can delete the artist of the music:
            artistsOfMusic.forEach {
                deleteArtistIfEmptyUseCase(artistId = it.id)
            }
        }

        musicRepository.deleteAll(musicIds)
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
        artist = musicInformationResult.artist,
        duration = musicInformationResult.duration,
        coverPath = musicInformationResult.coverPath,
        fingerprint = musicInformationResult.fingerprint,
        albumId = albumId,
        path = musicPath,
    )
}