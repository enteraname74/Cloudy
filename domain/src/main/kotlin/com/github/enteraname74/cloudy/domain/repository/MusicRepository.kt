package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import kotlin.uuid.Uuid

interface MusicRepository {

    /**
     * Starts the upload process of a music to the server.
     * It will handle the file part of it (saving the file, retrieving its metadata...)
     *
     * @param user the user that possess the music
     * @param fileData the file data of the music
     * @param shouldSearchForMetadata if the system should search music metadata from remote sources
     */
    suspend fun startUploadProcess(
        user: User,
        fileData: FileData,
        shouldSearchForMetadata: Boolean,
    ): UploadProcessState

    /**
     * Saved a music file only in the db after the upload process.
     */
    suspend fun saveMusicFileToDbAfterUploadProcess(music: Music): Music

    suspend fun upsert(
        music: Music,
        username: String,
        cover: FileData?,
    ): CloudyResult<Music>
    suspend fun upsertAll(musicIds: List<Music>, username: String): CloudyResult<Unit>
    suspend fun getFromId(musicId: String): Music?
    suspend fun getFromUser(
        musicId: String,
        userId: Uuid,
    ): Music?
    suspend fun getFromCoverPath(coverPath: String): Music?
    suspend fun getMusicFile(musicId: String, username: String): File?
    suspend fun getAll(ids: List<String>): List<Music>

    /**
     * Deletes the given musics from the db and the file system.
     */
    suspend fun deleteAll(ids: List<String>, username: String)
    suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Music>

    suspend fun getExistingIdsOfUser(
        userId: Uuid,
        ids: List<String>,
    ): List<String>

    suspend fun getExistingIds(
        ids: List<String>,
    ): List<String>

    suspend fun isMusicPossessedByUser(userId: Uuid, musicId: String): Boolean
    suspend fun getFromFingerprint(fingerprint: String, userId: Uuid): Music?
    suspend fun allFromAlbum(albumId: Uuid): List<Music>
    suspend fun allFromArtist(artistId: Uuid): List<Music>

    sealed interface UploadProcessState {
        /**
         * If an error has occurred during the upload process.
         */
        data object Error : UploadProcessState

        /**
         * If we should continue the process and create the links of the music to save (artists, album...).
         */
        data class ContinueProcess(
            val fingerprint: String,
        ): UploadProcessState
    }
}