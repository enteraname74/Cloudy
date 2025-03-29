package com.github.enteraname74.cloudy.fileaccess

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.logging.CloudyLogger
import java.io.File
import java.util.*

abstract class FileManager {
    protected val logger = CloudyLogger(this::class)
    protected abstract fun getFileDirectory(username: String): String

    fun getById(id: UUID, username: String): File? {
        val coversDirectory = File(getFileDirectory(username)).also { it.mkdirs() }

        return coversDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.firstOrNull { getUUIDFromFileName(it.name) == id }
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
            .map { it.length() }
            .sum()

        return directorySize
    }

    open fun delete(id: UUID, username: String) {
        val fileToDelete: File = getById(
            id = id,
            username = username,
        ) ?: return

        val hasBeenDeleted = fileToDelete.delete()
        if (!hasBeenDeleted) {
            logger.warn("Failed to delete cover file with id $id.")
        }
    }

    /**
     * Saves a file and returns its id (its name without an extension).
     */
    fun save(username: String, fileData: FileData): UUID {
        val fileId = UUID.randomUUID()
        val filename = "$fileId.${fileData.extension}"
        val filepath = "${getFileDirectory(username)}/$filename"

        val fileToSave = File(filepath)

        fileToSave.parentFile?.mkdirs()
        fileToSave.writeBytes(fileData.data)

        return fileId
    }

    private fun getUUIDFromFileName(fileName: String): UUID? =
        runCatching {
            UUID.fromString(
                fileName.replaceFirst(
                    regex = """[.][^.]+$""".toRegex(),
                    replacement = ""
                )
            )
        }.getOrNull()

    companion object {
        const val APP_FOLDER = "app"
    }
}