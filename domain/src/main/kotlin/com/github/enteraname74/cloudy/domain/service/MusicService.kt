package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.MusicArtist
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.artist.Artist
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
import com.github.enteraname74.cloudy.domain.model.music.MusicUpload
import com.github.enteraname74.cloudy.domain.repository.MusicArtistRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UploadMusicUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

class MusicService(
    private val musicRepository: MusicRepository,
    private val musicArtistRepository: MusicArtistRepository,
    private val updateMusicUseCase: UpdateMusicUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
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
    ): CloudyResult<Music> {

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
        musicUpdate: MusicUpdate,
        user: User,
    ): CloudyResult<Music> =
        updateMusicUseCase(
            musicUpdate = musicUpdate,
            user = user,
        )

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
        musicRepository.deleteAll(
            ids = musicIds,
            username = username,
        )
        deleteEmptyAlbumsAndArtistsUseCase()
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
    // TODO OPTIMIZATION: Logic should be at DB layer, avoid fetching all musics for checks.
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