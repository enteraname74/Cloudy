package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.domain.model.FileSavingData
import com.github.enteraname74.cloudy.logging.CloudyLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.uuid.Uuid

abstract class FileManager {
    protected val logger = CloudyLogger(this::class)
    protected abstract fun getFileDirectory(username: String): String

    // Retrieve file by its name (without extension)
    fun getByName(name: String, username: String): File? {
        val coversDirectory = File(getFileDirectory(username)).also { it.mkdirs() }

        return coversDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.firstOrNull {
                it.name.replaceFirst(
                    regex = """[.][^.]+$""".toRegex(),
                    replacement = ""
                ) == name
            }
    }

    protected fun getUserDirectory(username: String): String =
        "$APP_FOLDER/$username"

    /**
     * Retrieves the user directory size.
     */
    fun getUserDirectorySize(username: String): Long {
        val userDirectory = getUserDirectory(username = username)
        val directory = File(userDirectory)
        val directorySize = directory
            .walkTopDown()
            .filter { it.isFile }
            .sumOf { it.length() }

        return directorySize
    }

    open fun delete(name: String, username: String) {
        val fileToDelete: File = getByName(
            name = name,
            username = username,
        ) ?: return

        val hasBeenDeleted = fileToDelete.delete()
        if (!hasBeenDeleted) {
            logger.warn("Failed to delete cover file with id $name.")
        }
    }

    fun rename(
        from: String,
        to: String,
        username: String,
    ) {
        val existingFile: File = getByName(
            name = from,
            username = username,
        ) ?: return

        val updated = File("${getFileDirectory(username)}/$to")

        existingFile.renameTo(updated)
    }

    /**
     * Saves a file and returns its id (its name without an extension).
     */
    suspend fun save(data: FileSavingData): Uuid? =
        when (data) {
            is FileSavingData.MusicUrl -> saveFromUrl(data = data)
            is FileSavingData.UserFile -> saveUserData(data = data)
        }

    private fun saveUserData(
        data: FileSavingData.UserFile,
    ): Uuid {
        val fileId = Uuid.random()
        val filename = "$fileId.${data.fileData.extension}"
        val filepath = "${getFileDirectory(data.username)}/$filename"

        val fileToSave = File(filepath)

        fileToSave.parentFile?.mkdirs()
        fileToSave.writeBytes(data.fileData.data)

        return fileId
    }

    /**
     * Fetch and save a music from yt.
     *
     * @return the id of the music if saved.
     */
    private suspend fun saveFromUrl(
        data: FileSavingData.MusicUrl,
    ): Uuid? = withContext(Dispatchers.IO) {
        val fileId = Uuid.random()
        // TODO YT: In future, let user choose the format
        val filename = "$fileId.m4a"
        val filepath = "${getFileDirectory(data.username)}/$filename"

        try {
            val process = ProcessBuilder(
                "yt-dlp",
                "-f", "bestaudio[ext=m4a]/bestaudio",
                "-x",
                "--audio-format", "m4a",
                "--embed-metadata",
                "--embed-thumbnail",
                "--convert-thumbnails", "jpg",
                "-o", filepath,
                data.url
            )
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            if (exitCode != 0) {
                throw RuntimeException("yt-dlp failed with code $exitCode:\n$output")
            }
            fileId
        } catch (e: Exception) {
            logger.error("Failed to download music from yt: ${data.url}, got exception: ${e.message}")
            null
        }
    }

    companion object {
        const val APP_FOLDER = "app"
    }
}