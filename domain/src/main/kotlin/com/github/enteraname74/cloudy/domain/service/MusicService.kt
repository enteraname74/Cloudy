package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.model.Album
import com.github.enteraname74.cloudy.domain.model.Artist
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.AlbumRepository
import com.github.enteraname74.cloudy.domain.repository.ArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.util.*

class MusicService(
    private val musicRepository: MusicRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
) {

    suspend fun getFromInfo(
        musicId: UUID,
        userId: UUID
    ): Music? =
        musicRepository.getFromInfo(
            musicId = musicId,
            userId = userId,
        )

    private suspend fun getOrCreateArtist(
        artistName: String,
        userId: UUID,
        coverPath: String?
    ): Artist =
        artistRepository.getFromInformation(
            name = artistName,
            userId = userId,
        ) ?: artistRepository.upsert(
            artist = Artist(
                userId = userId,
                name = artistName,
                coverPath = coverPath,
            )
        )

    private suspend fun getOrCreateAlbum(
        albumName: String,
        artistName: String,
        userId: UUID,
        artistId: UUID,
        coverPath: String?
    ): Album =
        albumRepository.getFromInformation(
            albumName = albumName,
            albumArtist = artistName,
            userId = userId,
        ) ?: albumRepository.upsert(
            album = Album(
                userId = userId,
                name = albumName,
                coverPath = coverPath,
                artistId = artistId,
                artistName = artistName,
            )
        )

    private suspend fun deleteAlbumIfEmpty(
        albumId: UUID,
        userId: UUID,
    ) {
        val isAlbumEmpty = musicRepository
            .allFromAlbum(
                albumId = albumId,
                userId = userId,
            ).isEmpty()

        if (isAlbumEmpty) albumRepository.deleteById(albumId = albumId)
    }

    private suspend fun deleteArtistIfEmpty(
        artistId: UUID,
        userId: UUID,
    ) {
        val isArtistEmpty = musicRepository
            .allFromArtist(
                artistId = artistId,
                userId = userId,
            ).isEmpty()

        if (isArtistEmpty) artistRepository.deleteById(artistId = artistId)
    }

    suspend fun saveAndCreateMissingAlbumAndArtist(
        user: User,
        musicPath: String,
        musicInformationResult: MusicInformationResult.FileMetadata,
    ) {

        val artist: Artist = getOrCreateArtist(
            artistName = musicInformationResult.artist,
            userId = user.id,
            coverPath = musicInformationResult.coverPath,
        )

        val album: Album = getOrCreateAlbum(
            albumName = musicInformationResult.name,
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

    suspend fun upsertMusic(
        modifiedMusic: Music,
        userId: UUID,
    ) {
        // We get or create the artist of the modified music
        val artist: Artist = getOrCreateArtist(
            artistName = modifiedMusic.artist,
            userId = userId,
            coverPath = modifiedMusic.coverPath,
        )

        // We get or create the album of the modified music
        val album: Album = getOrCreateAlbum(
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
        deleteAlbumIfEmpty(
            albumId = modifiedMusic.albumId,
            userId = userId,
        )

        deleteArtistIfEmpty(
            artistId = modifiedMusic.artistId,
            userId = userId,
        )
    }

    suspend fun deleteById(
        musicId: UUID,
        userId: UUID,
    ): Boolean {
        val music: Music = musicRepository.getFromInfo(
            musicId = musicId,
            userId = userId,
        ) ?: return false
        musicRepository.deleteById(musicId)

        // We check if we can delete the album of the music:
        deleteAlbumIfEmpty(
            albumId = music.albumId,
            userId = music.userId,
        )

        // We then check if we can delete the artist of the music:
        deleteArtistIfEmpty(
            artistId = music.artistId,
            userId = music.userId,
        )

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