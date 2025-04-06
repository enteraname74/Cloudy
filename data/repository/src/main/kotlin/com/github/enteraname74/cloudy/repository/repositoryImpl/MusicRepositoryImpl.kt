package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.filepersistence.updateFromMetadata
import com.github.enteraname74.cloudy.domain.model.CustomMusicMetadata
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.Music
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.fileaccess.MusicFileManager
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
    private val musicFileManager: MusicFileManager,
    private val musicInformationRetriever: MusicInformationRetriever,
    private val musicFileMetadataManager: MusicFileMetadataManager,
) : MusicRepository {
    override suspend fun startUploadProcess(
        user: User,
        fileData: FileData,
        customMusicMetadata: CustomMusicMetadata?,
        shouldSearchForMetadata: Boolean
    ): UploadProcessState {
        // We save the file
        val temporarySavedFileId: UUID = musicFileManager.save(
            username = user.username,
            fileData = fileData,
        )
        // We retrieve the saved file to analyze its fingerprint for metadata
        val temporarySavedFile: File = musicFileManager.getById(
            username = user.username,
            id = temporarySavedFileId,
        ) ?: return UploadProcessState.Error

        val musicMetadata: MusicInformationRetriever.Metadata = musicInformationRetriever.getInformationAboutMusicFile(
            musicFile = temporarySavedFile,
            musicId = temporarySavedFileId,
            customMetadata = customMusicMetadata,
            shouldSearchForMetadata = shouldSearchForMetadata,
        )

        /*
        We check if a music with the same fingerprint has already been saved.
        If so, we will delete the temporary file and update the information of the found file.
         */
        val existingMusic: Music? = getFromFingerprint(
            fingerprint = musicMetadata.fingerprint,
            userId = user.id,
        )

        if (existingMusic != null) {
            musicFileManager.delete(
                id = temporarySavedFileId,
                username = user.username,
            )
            val updatedMusic =
                saveMusicFileToDbAfterUploadProcess(music = existingMusic.updateFromMetadata(metadata = musicMetadata))
            return UploadProcessState.AlreadyExisting(updatedMusic)
        }

        return UploadProcessState.ContinueProcess(
            metadata = musicMetadata
        )
    }

    override suspend fun saveMusicFileToDbAfterUploadProcess(music: Music): Music =
        musicDataSource.upsert(
            music.copy(
                lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC)
            )
        )


    override suspend fun upsert(
        music: Music,
        username: String,
        cover: FileData?,
    ): CloudyResult<Music> {
        val musicFile: File = musicFileManager.getById(
            username = username,
            id = music.id,
        ) ?: return CloudyResult.Error()

        // TODO: What to do for OPUS files?
        musicFileMetadataManager.setMetadataOfFile(
            musicFile = musicFile,
            music = music,
            cover = cover?.data,
        )

        return CloudyResult.Success(
            musicDataSource.upsert(
                music.copy(
                    lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                    coverPath = cover?.let { Music.buildLocalCoverPath() } ?: music.coverPath,
                )
            )
        )
    }

    override suspend fun upsertAll(musicIds: List<Music>, username: String): CloudyResult<Unit> {
        musicIds.forEach { music ->
            val musicFile: File = musicFileManager.getById(
                username = username,
                id = music.id,
            ) ?: return CloudyResult.Error()

            // TODO: Handle cover update
            musicFileMetadataManager.setMetadataOfFile(
                musicFile = musicFile,
                music = music,
                cover = null,
            )
        }

        musicDataSource.upsertAll(
            musicIds.map {
                it.copy(
                    lastUpdateAt = LocalDateTime.now(ZoneOffset.UTC),
                )
            }
        )

        return CloudyResult.Success(Unit)
    }

    override suspend fun getFromId(musicId: UUID): Music? =
        musicDataSource.getFromId(musicId = musicId)

    override suspend fun getFromCoverPath(coverPath: String): Music? =
        musicDataSource.getFromCoverPath(coverPath = coverPath)

    override suspend fun getMusicFile(musicId: UUID, username: String): File? =
        musicFileManager.getById(
            id = musicId,
            username = username,
        )

    override suspend fun getAll(ids: List<UUID>): List<Music> =
        musicDataSource.getAll(ids)

    override suspend fun deleteAll(ids: List<UUID>, username: String) {
        ids.forEach { id ->
            musicFileManager.delete(
                id = id,
                username = username,
            )
        }
        musicDataSource.deleteAll(ids)
    }

    override suspend fun getAllOfUser(
        userId: UUID,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun isMusicPossessedByUser(userId: UUID, musicId: UUID): Boolean =
        musicDataSource.isMusicPossessedByUser(userId, musicId)

    override suspend fun getFromFingerprint(fingerprint: String, userId: UUID): Music? =
        musicDataSource.getFromFingerprint(
            fingerprint = fingerprint,
            userId = userId,
        )

    override suspend fun allFromAlbum(albumId: UUID): List<Music> =
        musicDataSource.allFromAlbum(albumId)

    override suspend fun allFromArtist(artistId: UUID): List<Music> =
        musicDataSource.allFromArtist(artistId)
}