package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.ImportFolder
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.Music
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadPayload
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.CommonFileUtils
import com.github.enteraname74.cloudy.logging.CloudyLogger
import java.io.File

/**
 * Imports music files that were put directly on the server (in the import folder of a user),
 * instead of being uploaded through the API.
 *
 * It uses the same process as an upload, without a client spec (metadata are read from the file).
 * Remote metadata are searched only if enabled for the import folder (see [ImportFolder.shouldSearchMetadata]).
 */
class MusicImportService(
    private val musicService: MusicService,
    private val userService: UserService,
    private val importFolder: ImportFolder,
) {
    private val logger = CloudyLogger(this::class)

    suspend fun importFile(
        username: String,
        file: File,
    ): ImportResult {
        val user: User = userService.getUserFromUsername(username) ?: return ImportResult.UnknownUser

        val extension: String = CommonFileUtils.getFileExtension(file.name)
            ?.lowercase()
            ?.takeIf { it in SUPPORTED_EXTENSIONS }
            ?: return ImportResult.UnsupportedFile

        // Not the file's fault: it must not be deleted, the permissions must be fixed.
        if (!file.canRead()) return ImportResult.Unreadable

        if (userService.isUserDirectoryFull(userId = user.id, addedSize = file.length())) {
            return ImportResult.StorageFull
        }

        val data: ByteArray = runCatching { file.readBytes() }.getOrElse {
            logger.error("Failed to read file to import ${file.path}: $it")
            return ImportResult.Failed
        }
        if (data.isEmpty()) return ImportResult.UnsupportedFile

        val result: CloudyResult<Music> = musicService.saveUserFile(
            userId = user.id,
            payload = MusicUploadPayload(
                musicFile = FileData(
                    extension = extension,
                    data = data,
                ),
                musicCover = null,
                spec = null,
            ),
            shouldSearchForMetadata = importFolder.shouldSearchMetadata,
        )

        return when (result) {
            is CloudyResult.Error -> ImportResult.Failed
            is CloudyResult.Success -> ImportResult.Imported(result.data)
        }
    }

    sealed interface ImportResult {
        data class Imported(val music: Music) : ImportResult
        data object UnknownUser : ImportResult
        data object UnsupportedFile : ImportResult
        data object StorageFull : ImportResult
        data object Unreadable : ImportResult
        data object Failed : ImportResult
    }

    companion object {
        val SUPPORTED_EXTENSIONS: Set<String> = setOf(
            "mp3",
            "m4a",
            "mp4",
            "aac",
            "flac",
            "ogg",
            "oga",
            "opus",
            "wav",
            "aif",
            "aiff",
            "wma",
            "webm",
        )
    }
}
