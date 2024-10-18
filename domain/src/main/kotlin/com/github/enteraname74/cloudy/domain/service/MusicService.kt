package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.usecase.album.DeleteAlbumIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.album.GetOrCreateAlbumUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.DeleteArtistIfEmptyUseCase
import com.github.enteraname74.cloudy.domain.usecase.artist.GetOrCreateArtistUseCase
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class MusicService(
    private val musicRepository: MusicRepository,
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
    ) {

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
            artistId = artist.id,
            musicPath = musicPath,
            musicInformationResult = musicInformationResult,
        )

        musicRepository.upsert(music)
    }

    suspend fun upsert(
        modifiedMusic: Music,
        userId: UUID,
    ) {
        // We get or create the artist of the modified music
        val artist: Artist = getOrCreateArtistUseCase(
            artistName = modifiedMusic.artist,
            userId = userId,
            coverPath = modifiedMusic.coverPath,
        )

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbumUseCase(
            albumName = modifiedMusic.album,
            userId = userId,
            artistId = artist.id,
            artistName = artist.name,
            coverPath = modifiedMusic.coverPath,
        )

        // We update the album and artist id of the music
        val musicWithCorrectIds = modifiedMusic.copy(
            albumId = album.id,
            artistId = artist.id,
        )
        musicRepository.upsert(musicWithCorrectIds)

        // We check if the legacy album and artist can be deleted
        deleteAlbumIfEmptyUseCase(albumId = modifiedMusic.albumId)

        deleteArtistIfEmptyUseCase(artistId = modifiedMusic.artistId)
    }

    suspend fun deleteById(musicId: UUID): Boolean {
        val music: Music = musicRepository.getFromId(musicId = musicId) ?: return false
        musicRepository.deleteById(musicId)

        // We check if we can delete the album of the music:
        deleteAlbumIfEmptyUseCase(albumId = music.albumId)

        // We then check if we can delete the artist of the music:
        deleteArtistIfEmptyUseCase(artistId = music.artistId)

        return true
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

    private fun musicInformationToMusic(
        userId: UUID,
        albumId: UUID,
        artistId: UUID,
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
        artistId = artistId,
        path = musicPath,
    )
}