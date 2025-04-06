package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import java.io.File
import java.util.*

interface MusicRepository {

    /**
     * Starts the upload process of a music to the server.
     * It will handle the file part of it (saving the file, retrieving its metadata...)
     *
     * @param user the user that possess the music
     * @param fileData the file data of the music
     * @param customMusicMetadata custom metadata that the user has sent with the file
     * @param shouldSearchForMetadata if the system should search music metadata from remote sources
     */
    suspend fun startUploadProcess(
        user: User,
        fileData: FileData,
        customMusicMetadata: CustomMusicMetadata?,
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
    suspend fun getFromId(musicId: UUID): Music?
    suspend fun getFromCoverPath(coverPath: String): Music?
    suspend fun getMusicFile(musicId: UUID, username: String): File?
    suspend fun getAll(ids: List<UUID>): List<Music>

    /**
     * Deletes the given musics from the db and the file system.
     */
    suspend fun deleteAll(ids: List<UUID>, username: String)
    suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest = PaginatedRequest(),
    ): List<Music>

    suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean
    suspend fun getFromFingerprint(fingerprint: String, userId: UUID): Music?
    suspend fun allFromAlbum(albumId: UUID): List<Music>
    suspend fun allFromArtist(artistId: UUID): List<Music>

    sealed interface UploadProcessState {
        /**
         * If an error has occurred during the upload process.
         */
        data object Error : UploadProcessState

        /**
         * If the file was already on the server.
         * In this case, we just need to save the updated file.
         */
        data class AlreadyExisting(val updatedMusic: Music) : UploadProcessState

        /**
         * If we should continue the process and create the links of the music to save (artists, album...).
         */
        data class ContinueProcess(
            val metadata: MusicInformationRetriever.Metadata,
        ): UploadProcessState
    }
}