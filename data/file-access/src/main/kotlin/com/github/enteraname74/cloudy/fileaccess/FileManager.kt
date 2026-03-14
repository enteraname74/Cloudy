package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.logging.CloudyLogger
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
    fun save(username: String, fileData: FileData): Uuid {
        val fileId = Uuid.random()
        val filename = "$fileId.${fileData.extension}"
        val filepath = "${getFileDirectory(username)}/$filename"

        val fileToSave = File(filepath)

        fileToSave.parentFile?.mkdirs()
        fileToSave.writeBytes(fileData.data)

        return fileId
    }

    companion object {
        const val APP_FOLDER = "app"
    }
}