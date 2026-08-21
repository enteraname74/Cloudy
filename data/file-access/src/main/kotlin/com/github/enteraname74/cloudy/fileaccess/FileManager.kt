package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.domain.model.FileSavingData
import com.github.enteraname74.cloudy.logging.CloudyLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.uuid.Uuid

abstract class FileManager {
    protected val logger = CloudyLogger(this::class)

    private val initialCookiesFile = Path.of("cookies.txt")
    private val writableCookiesFile = Path.of("$APP_FOLDER/cookies.txt")

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

    // TODO user directory should be set from the user id instead?
    protected fun getUserDirectory(username: String): String {
        val path = "$APP_FOLDER/$username"
        File(path).mkdirs()
        return path
    }

    fun deleteUserDirectory(username: String) {
        File(getUserDirectory(username)).deleteRecursively()
    }

    fun clearUserDirectory(username: String) {
        val directory = File(getUserDirectory(username))

        if (!directory.exists() || !directory.isDirectory) {
            return
        }

        directory.listFiles()?.all { child -> child.deleteRecursively() }
    }

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

    /**
     * Returns the max size usable on the user directory
     */
    fun getUserDirectoryMaxSize(username: String): Long {
        val userDirectory = getUserDirectory(username = username)
        val directory = File(userDirectory)

        return directory.usableSpace
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
    ): Uuid? = try {
        val fileId = Uuid.random()
        val filename = "$fileId.${data.fileData.extension}"
        val filepath = "${getFileDirectory(data.username)}/$filename"

        val fileToSave = File(filepath)

        fileToSave.parentFile?.mkdirs()
        fileToSave.writeBytes(data.fileData.data)

        return fileId
    } catch (e: Exception) {
        logger.error("Failed to save temporary file to user storage: $e")
        null
    }

    /**
     * Fetch and save a music from yt.
     *
     * @return the id of the music if saved.
     */
    private suspend fun saveFromUrl(
        data: FileSavingData.MusicUrl,
    ): Uuid? = withContext(Dispatchers.IO) {

        // First, check for cookies file
        if (Files.notExists(initialCookiesFile)) {
            logger.error("No initial cookies file found")
            return@withContext null
        }

        if (Files.notExists(writableCookiesFile)) {
            Files.createDirectories(writableCookiesFile.parent)
            Files.copy(
                initialCookiesFile,
                writableCookiesFile,
                StandardCopyOption.REPLACE_EXISTING,
            )
        }

        val fileId = Uuid.random()
        // TODO YT: In future, let user choose the format
        val filename = "$fileId.m4a"
        val filepath = "${getFileDirectory(data.username)}/$filename"

        try {
            val process = ProcessBuilder(
                "yt-dlp",
                "--cookies", writableCookiesFile.toString(),
                "--js-runtimes", "node",
                "--remote-components", "ejs:github",
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
        private fun isUsingSQLite(): Boolean =
            System.getenv("DB_URL")?.contains("sqlite") ?: false

        val APP_FOLDER = if (isUsingSQLite()) {
            "cloudy_data"
        } else {
            "/cloudy_data"
        }
    }
}