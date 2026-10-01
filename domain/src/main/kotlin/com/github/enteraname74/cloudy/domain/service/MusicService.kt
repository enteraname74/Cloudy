package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicId
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdatePayload
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadPayload
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
    private val updateMusicUseCase: UpdateMusicUseCase,
    private val uploadMusicUseCase: UploadMusicUseCase,
    private val deleteEmptyAlbumsAndArtistsUseCase: DeleteEmptyAlbumsAndArtistsUseCase,
) {
    suspend fun getFromUser(userId: Uuid, musicId: MusicId): Music? =
        musicRepository.getFromUser(
            musicId = musicId,
            userId = userId,
        )

    suspend fun getMusicFile(musicId: MusicId, userId: Uuid): File? {
        val music: Music = musicRepository.getIfReadPermissionGranted(
            musicId = musicId,
            userId = userId,
        ) ?: return null

        return musicRepository.getMusicFile(
            fingerprint = music.fingerprint,
            userId = music.userId,
        )
    }

    suspend fun getFromCoverPath(coverPath: String): Music? =
        musicRepository.getFromCoverPath(coverPath = coverPath)

    suspend fun saveUserFile(
        userId: Uuid,
        payload: MusicUploadPayload,
        shouldSearchForMetadata: Boolean,
    ): CloudyResult<Music> {
        val uploadProcess: UploadProcessState = musicRepository.startUploadProcess(
            userId = userId,
            data = payload.musicFile,
            shouldSearchForMetadata = shouldSearchForMetadata,
            musicUploadSpec = payload.spec,
            cover = payload.musicCover,
        )

        return when (uploadProcess) {
            UploadProcessState.Error -> {
                CloudyResult.Error()
            }

            is UploadProcessState.ContinueProcess -> {
                uploadMusicUseCase(
                    musicUploadSpec = uploadProcess.musicUploadSpec,
                    cover = payload.musicCover,
                    fingerprint = uploadProcess.fingerprint,
                    userId = userId,
                )
            }
        }
    }

    suspend fun update(
        payload: MusicUpdatePayload,
        userId: Uuid,
    ): CloudyResult<Music> =
        updateMusicUseCase(
            payload = payload,
            userId = userId,
        )

    suspend fun deleteAll(
        musicIds: List<MusicId>,
        userId: Uuid,
    ) {
        musicRepository.deleteAll(
            ids = musicIds,
            userId = userId,
        )
        deleteEmptyAlbumsAndArtistsUseCase(userId)
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
        musicId: MusicId,
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
        idsToCheck: List<MusicId>,
        userId: Uuid
    ): List<MusicId> {
        val existingIds: List<MusicId> = musicRepository.getExistingIdsOfUser(
            ids = idsToCheck,
            userId = userId,
        )

        return idsToCheck.filterNot { it in existingIds }
    }
}