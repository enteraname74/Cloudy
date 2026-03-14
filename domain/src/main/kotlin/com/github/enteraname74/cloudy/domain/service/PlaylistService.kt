package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.model.Playlist
import com.github.enteraname74.cloudy.domain.model.UploadedPlaylistData
import com.github.enteraname74.cloudy.domain.repository.MusicPlaylistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.PlaylistRepository
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import kotlin.uuid.Uuid

class PlaylistService(
    private val playlistRepository: PlaylistRepository,
    private val musicRepository: MusicRepository,
    private val musicPlaylistRepository: MusicPlaylistRepository,
) {
    suspend fun getFromId(playlistId: Uuid): Playlist? =
        playlistRepository.getFromId(playlistId)

    suspend fun getFromCoverPath(coverPath: String): Playlist? =
        playlistRepository.getFromCoverPath(coverPath)

    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest
    ): List<Playlist> =
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

    /**
     * Upload playlists from the user.
     * Returns a list of a playlist and its legacy id (send by the user).
     */
    suspend fun uploadPlaylists(
        playlists: List<Playlist>,
        userId: Uuid,
    ): List<UploadedPlaylistData> {

        /*
        We will save only the playlists that are not already saved (with the same name)
         */
        val uniquePlaylists = playlists.distinctBy { it.name }
        val playlistsToSave =
            uniquePlaylists.filter {
                playlistRepository.getFromInformation(
                    name = it.name,
                    userId = userId,
                ) == null
            }

        /*
        We will attribute news ids for the playlists
         */
        val mapOfIds: Map<Uuid, Uuid> = buildMap {
            playlistsToSave.forEach {
                put(Uuid.random(), it.id)
            }
        }

        return playlistRepository.upsertAll(
            playlists = playlistsToSave.map { playlist ->
                playlist.copy(
                    id = mapOfIds.entries.first { it.value == playlist.id }.key,
                )
            }
        ).map { playlist ->
            UploadedPlaylistData(
                playlist = playlist,
                userPlaylistId = mapOfIds[playlist.id]!!,
            )
        }
    }

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
    suspend fun getDeletedPlaylistsIds(
        idsToCheck: List<Uuid>,
        userId: Uuid
    ): List<Uuid> {
        val allPlaylistOfUser: List<Uuid> = playlistRepository.allOfUser(
            userId = userId,
        ).map { it.id }

        return idsToCheck.filterNot { it in allPlaylistOfUser }
    }
}

