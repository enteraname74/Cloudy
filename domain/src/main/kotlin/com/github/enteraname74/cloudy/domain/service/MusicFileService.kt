package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.ext.toGb
import com.github.enteraname74.cloudy.domain.filepersistence.MusicFilePersistenceManager
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationResult
import com.github.enteraname74.cloudy.domain.filepersistence.MusicInformationRetriever
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.MusicRepository
import com.github.enteraname74.cloudy.domain.util.ServiceResult
import io.ktor.http.content.*
import java.io.File
import java.util.*

class MusicFileService(
    private val musicInformationRetriever: MusicInformationRetriever,
    private val musicRepository: MusicRepository,
) {

    private val musicFilePersistenceManager = MusicFilePersistenceManager()

    suspend fun save(
        user: User,
        file: MultiPartData,
        shouldSearchForMetadata: Boolean,
    ): ServiceResult {
        val fileId: UUID = musicFilePersistenceManager.saveFile(
            username = user.username,
            file = file,
        ) ?: return ServiceResult.Error(
            message = FILE_CANNOT_BE_SAVED,
        )

        println("UPLOAD -- Service -- got file id: $fileId")

        val temporalSavedFile: File = musicFilePersistenceManager.getById(
            musicId = fileId,
            username = user.username,
        ) ?: return ServiceResult.Error(
            message = FILE_CANNOT_BE_SAVED,
        )

        println("UPLOAD -- Service -- got tmp file: $temporalSavedFile")

        val musicInformationResult: MusicInformationResult = musicInformationRetriever.getInformationAboutMusicFile(
            musicFile = temporalSavedFile,
            musicId = fileId,
            shouldSearchForMetadata = shouldSearchForMetadata,
        )

        println("UPLOAD -- Service -- got info: $musicInformationResult")

        return when (musicInformationResult) {
            MusicInformationResult.Error -> {
                ServiceResult.Error()
            }

            is MusicInformationResult.FileMetadata -> {
                if (
                    musicRepository.doesMusicExists(
                        fingerprint = musicInformationResult.fingerprint,
                        userId = user.id,
                    )
                ) {
                    musicFilePersistenceManager.deleteFile(
                        musicId = fileId,
                        username = user.username,
                    )
                    ServiceResult.Error(message = MUSIC_ALREADY_SAVED)
                } else {
                    ServiceResult.Ok(
                        data = musicInformationResult,
                    )
                }
            }
        }
    }

    fun deleteMusicFile(musicId: UUID, username: String) {
        musicFilePersistenceManager.deleteFile(
            musicId = musicId,
            username = username,
        )
    }

    fun getMusicFile(musicId: UUID, username: String) : File? =
        musicFilePersistenceManager.getById(
            musicId = musicId,
            username = username,
        )

    fun isUserDirectoryFull(
        username: String,
        addedSize: Long = 0L,
    ): Boolean {
        val userDirectorySize = musicFilePersistenceManager.getUserDirectorySize(username)
        val gbSize = (userDirectorySize + addedSize).toGb()

        return gbSize >= MAX_USER_DIRECTORY_SIZE_IN_GB
    }

    companion object {
        private const val MAX_USER_DIRECTORY_SIZE_IN_GB = 10
        private const val FILE_CANNOT_BE_SAVED = "File cannot be saved."
        private const val MUSIC_ALREADY_SAVED = "This song was already saved before."
    }
}