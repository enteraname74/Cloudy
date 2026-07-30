package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.FileSavingData
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdate
import com.github.enteraname74.cloudy.domain.model.music.MusicUpload
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.usecase.DeleteEmptyAlbumsAndArtistsUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UpdateMusicUseCase
import com.github.enteraname74.cloudy.domain.usecase.music.UploadMusicUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

class MusicService(
    private val musicRepository: MusicRepository,
    private val userRepository: UserRepository,
    private val updateMusicUseCase: UpdateMusicUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase,
    private val playerRepository: PlayerRepository,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
) {
    suspend fun getFromId(musicId: String): Music? =
        musicRepository.getFromId(musicId = musicId)

    suspend fun getMusicFile(musicId: String, userId: Uuid): File? {
        val hasPermission: Boolean = musicRepository.isMusicPossessedByUser(
            userId = userId,
            musicId = musicId,
        ) || playerRepository.hasReadPermission(
            userId = userId,
            musicId = musicId,
        )

        if (!hasPermission) return null

        val music: Music = musicRepository.getFromId(musicId) ?: return null
        val user: User = userRepository.getFromId(music.userId) ?: return null

        return musicRepository.getMusicFile(
            musicId = musicId,
            username = user.username,
        )
    }

    suspend fun getFromCoverPath(coverPath: String): Music? =
        musicRepository.getFromCoverPath(coverPath = coverPath)

    private suspend fun saveData(
        user: User,
        fileSavingData: FileSavingData,
        shouldSearchForMetadata: Boolean,
        musicUpload: MusicUpload?,
    ): CloudyResult<Music> {
        val uploadProcess: UploadProcessState = musicRepository.startUploadProcess(
            user = user,
            data = fileSavingData,
            shouldSearchForMetadata = shouldSearchForMetadata,
            musicUpload = musicUpload,
        )

        return when (uploadProcess) {
            UploadProcessState.Error -> {
                CloudyResult.Error()
            }

            is UploadProcessState.ContinueProcess -> {
                uploadMusicUseCase(
                    musicUpload = uploadProcess.musicUpload,
                    fingerprint = uploadProcess.fingerprint,
                    user = user,
                    musicPath = "music/${uploadProcess.fingerprint}",
                )
            }
        }
    }

    suspend fun saveUserFile(
        user: User,
        fileData: FileData,
        musicUpload: MusicUpload,
        shouldSearchForMetadata: Boolean,
    ): CloudyResult<Music> =
        saveData(
            user = user,
            fileSavingData = FileSavingData.UserFile(
                username = user.username,
                fileData = fileData,
            ),
            shouldSearchForMetadata = shouldSearchForMetadata,
            musicUpload = musicUpload,
        )

    suspend fun update(
        musicUpdate: MusicUpdate,
        user: User,
    ): CloudyResult<Music> =
        updateMusicUseCase(
            musicUpdate = musicUpdate,
            user = user,
        )

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
        val existingIds: List<String> = musicRepository.getExistingIdsOfUser(
            ids = idsToCheck,
            userId = userId,
        )

        return idsToCheck.filterNot { it in existingIds }
    }
}