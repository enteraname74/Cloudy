package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.repository.MusicRepository.UploadProcessState
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.domain.util.PaginatedRequest
import com.github.enteraname74.cloudy.domain.model.FileSavingData
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.fileaccess.MusicFileManager
import com.github.enteraname74.cloudy.logging.CloudyLogger
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import com.github.enteraname74.cloudy.repository.datasource.MusicDataSource
import java.io.File
import kotlin.uuid.Uuid

class MusicRepositoryImpl(
    private val musicDataSource: MusicDataSource,
    private val musicFileManager: MusicFileManager,
    private val musicInformationRetriever: MusicInformationRetriever,
    private val musicFileMetadataManager: MusicFileMetadataManager,
    private val playerRepository: PlayerRepository,
) : MusicRepository {
    private val logger = CloudyLogger(this::class)

    override suspend fun startUploadProcess(
        user: User,
        data: FileSavingData,
        shouldSearchForMetadata: Boolean,
        musicUploadSpec: MusicUploadSpec?,
        cover: FileData?,
    ): UploadProcessState = runCatching {
        // We save the file
        val temporarySavedFileId: Uuid? = musicFileManager.save(
            data = data,
        )

        if (temporarySavedFileId == null) return UploadProcessState.Error

        // We retrieve the saved file to analyze its fingerprint for metadata
        val temporarySavedFile: File? = musicFileManager.getByName(
            username = user.username,
            name = temporarySavedFileId.toString(),
        )

        if (temporarySavedFile == null) {
            logger.error("Temporary saved file couldn't be found")
            return@runCatching UploadProcessState.Error
        }

        //        val musicMetadata: MusicInformationRetriever.Metadata = musicInformationRetriever.getInformationAboutMusicFile(
        //            musicFile = temporarySavedFile,
        //            customMetadata = customMusicMetadata,
        //            shouldSearchForMetadata = shouldSearchForMetadata,
        //        )

        val fingerprint: String? = musicInformationRetriever.getFingerprint(musicFile = temporarySavedFile)
        if (fingerprint == null) {
            logger.error("Fingerprint not found for music file")
            return@runCatching UploadProcessState.Error
        }

        val finalMusicUploadSpec: MusicUploadSpec = musicUploadSpec ?: musicFileMetadataManager
            .getMetadataOfFile(musicFile = temporarySavedFile)
            .toMusicUpload()

        /*
        We check if a music with the same fingerprint has already been saved.
        If so, we will delete the temporary file and update the information of the found file.
         */
        val existingMusic: Music? = getFromFingerprint(
            fingerprint = fingerprint,
            userId = user.id,
        )

        if (existingMusic != null) {
            musicFileManager.delete(
                name = temporarySavedFileId.toString(),
                username = user.username,
            )
        } else {
            // We will rename the temporary file to suit the music fingerprint
            musicFileManager.rename(
                from = temporarySavedFileId.toString(),
                username = user.username,
                to = "$fingerprint.${data.extension}"
            )
        }

        return UploadProcessState.ContinueProcess(
            fingerprint = fingerprint,
            musicUploadSpec = finalMusicUploadSpec,
            cover = cover,
        )
    }.getOrElse {
        logger.error(
            "Error while downloading uploaded song: $it"
        )
        UploadProcessState.Error
    }

    override suspend fun saveMusicFileToDbAfterUploadProcess(music: Music): Music =
        musicDataSource.upsert(
            music.copy(
                lastUpdateAtMillis = DateUtils.now()
            )
        )

    override suspend fun upsert(
        music: Music,
        username: String,
        cover: FileData?,
    ): CloudyResult<Music> {
        val musicFile: File = musicFileManager.getByName(
            username = username,
            name = music.fingerprint,
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
                    lastUpdateAtMillis = DateUtils.now(),
                    coverPath = cover?.let { Music.buildLocalCoverPath() } ?: music.coverPath,
                )
            )
        )
    }

    override suspend fun upsertAll(musicIds: List<Music>, username: String): CloudyResult<Unit> {
        musicIds.forEach { music ->
            val musicFile: File = musicFileManager.getByName(
                username = username,
                name = music.fingerprint,
            ) ?: return CloudyResult.Error()

            musicFileMetadataManager.setMetadataOfFile(
                musicFile = musicFile,
                music = music,
                cover = null,
            )
        }

        musicDataSource.upsertAll(
            musicIds.map {
                it.copy(
                    lastUpdateAtMillis = DateUtils.now(),
                )
            }
        )

        return CloudyResult.Success(Unit)
    }

    override suspend fun getFromId(musicId: String): Music? =
        musicDataSource.getFromId(musicId = musicId)

    override suspend fun getFromUser(
        musicId: String,
        userId: Uuid
    ): Music? =
        musicDataSource.getFromUser(
            userId = userId,
            musicId = musicId,
        )

    override suspend fun getFromCoverPath(coverPath: String): Music? =
        musicDataSource.getFromCoverPath(coverPath = coverPath)

    override suspend fun getMusicFile(musicId: String, username: String): File? =
        musicFileManager.getByName(
            name = musicId,
            username = username,
        )

    override suspend fun getAll(ids: List<String>): List<Music> =
        musicDataSource.getAll(ids)

    override suspend fun deleteAll(ids: List<String>, username: String) {
        // We must ensure that played lists are reorderd correctly if a music was in it.
        playerRepository.removeMusics(
            listIds = playerRepository.getPlayedListIdsOfMusics(ids),
            musicIds = ids,
            socketDeviceIdToIgnore = null,
        )

        ids.forEach { id ->
            musicFileManager.delete(
                name = id,
                username = username,
            )
        }
        musicDataSource.deleteAll(ids)
    }

    override suspend fun getAllOfUser(
        userId: Uuid,
        paginatedRequest: PaginatedRequest,
    ): List<Music> =
        musicDataSource
            .getAllOfUser(
                userId = userId,
                paginatedRequest = paginatedRequest,
            )

    override suspend fun getExistingIdsOfUser(
        userId: Uuid,
        ids: List<String>
    ): List<String> =
        musicDataSource.getExistingIdsOfUser(
            userId = userId,
            ids = ids,
        )

    override suspend fun getExistingIds(ids: List<String>): List<String> =
        musicDataSource.getExistingIds(ids)

    override suspend fun isMusicPossessedByUser(userId: Uuid, musicId: String): Boolean =
        musicDataSource.isMusicPossessedByUser(userId, musicId)

    override suspend fun getFromFingerprint(fingerprint: String, userId: Uuid): Music? =
        musicDataSource.getFromFingerprint(
            fingerprint = fingerprint,
            userId = userId,
        )

    override suspend fun allFromAlbum(albumId: Uuid): List<Music> =
        musicDataSource.allFromAlbum(albumId)

    override suspend fun allFromArtist(artistId: Uuid): List<Music> =
        musicDataSource.allFromArtist(artistId)
}