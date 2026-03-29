package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistUpload
import com.github.enteraname74.cloudy.domain.model.playlist.PlaylistWithMusics
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.usecase.playlist.UploadPlaylistUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
    private val uploadPlaylistUseCase: UploadPlaylistUseCase,
) {
    suspend fun getFromId(playlistId: Uuid): Playlist? =
        playlistRepository.getFromId(playlistId)

    suspend fun getFromCoverPath(coverPath: String): Playlist? =
        playlistRepository.getFromCoverPath(coverPath)

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<PlaylistWithMusics> =
        playlistRepository.allOfUser(
            userId = userId,
            paginatedRequest = paginatedRequest,
        )

    suspend fun isPlaylistPossessedByUser(
        userId: Uuid,
        playlistId: Uuid,
    ): Boolean =
        playlistRepository.isPlaylistPossessedByUser(
            userId = userId,
            playlistId = playlistId,
        )

    suspend fun isPlaylistPossessedByUser(
        playlistName: String,
        userId: Uuid,
    ): Boolean =
        playlistRepository.getFromInformation(
            name = playlistName,
            userId = userId,
        ) != null

    suspend fun upsert(
        playlist: Playlist,
        coverData: FileData?,
        username: String,
    ): Playlist =
        playlistRepository.upsert(
            playlist = playlist,
            coverData = coverData,
            username = username,
        )

    suspend fun upload(
        playlistUpload: PlaylistUpload,
        user: User,
    ): CloudyResult<PlaylistWithMusics> =
        uploadPlaylistUseCase(
            playlistUpload = playlistUpload,
            user = user,
        )

    suspend fun deleteFromPlaylist(
        playlistId: Uuid,
        musicIds: List<String>,
        userId: Uuid,
    ): List<MusicPlaylist> {
        val userSongsIds: List<String> = musicIds.filter { musicId ->
            musicRepository.isMusicPossessedByUser(
                userId = userId,
                musicId = musicId,
            )
        }

        val linksToDelete = userSongsIds.map { musicId ->
            MusicPlaylist(
                musicId = musicId,
                playlistId = playlistId,
                userId = userId,
            )
        }

        musicPlaylistRepository.deleteAll(
            ids = linksToDelete.map { it.id }
        )

        return linksToDelete
    }

    suspend fun addToPlaylist(
        playlistId: Uuid,
        musicIds: List<String>,
        userId: Uuid,
    ): List<MusicPlaylist> {
        val userSongsIds: List<String> = musicIds.filter { musicId ->
            musicRepository.isMusicPossessedByUser(
                userId = userId,
                musicId = musicId,
            )
        }

        musicPlaylistRepository.upsertAll(
            musicPlaylists = userSongsIds.map { musicId ->
                MusicPlaylist(
                    musicId = musicId,
                    playlistId = playlistId,
                    userId = userId,
                )
            }
        )

        return musicPlaylistRepository.getAllOfPlaylist(playlistId)
    }

    suspend fun deleteAll(playlistIds: List<Uuid>) =
        playlistRepository.deleteAll(playlistIds)

    /**
     * Given a list of playlist ids to check,
     * returns a list of all the ids of the initial list that are not present
     * in the db.
     */
    // TODO OPTIMIZATION: Logic should be at DB layer, avoid fetching all playlists for checks.
    suspend fun getDeletedPlaylistsIds(
        idsToCheck: List<Uuid>,
        userId: Uuid
    ): List<Uuid> {
        val allPlaylistOfUser: List<Uuid> = playlistRepository.allOfUser(
            userId = userId,
        ).map { it.playlist.id }

        return idsToCheck.filterNot { it in allPlaylistOfUser }
    }
}

