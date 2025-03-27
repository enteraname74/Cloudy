package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.util.FileUtils
import com.github.enteraname74.cloudy.logging.CloudyLogger
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import java.io.File
import java.util.*

class CoverFilePersistenceManager {
    private val logger = CloudyLogger(this::class)

    private fun getCoversDirectory(username: String): String =
        "${FilePersistenceUtils.APP_FOLDER}/$username/${FilePersistenceUtils.MUSIC_FOLDER}/$COVERS_FOLDER"

    fun getById(id: UUID, username: String): File? {
        val coversDirectory = File(getCoversDirectory(username)).also { it.mkdirs() }

        return coversDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.firstOrNull { FilePersistenceUtils.getUUIDFromFileName(it.name) == id }
    }

    fun deleteCover(id: UUID, username: String) {
        val fileToDelete: File = getById(
            id = id,
            username = username,
        ) ?: return

        val hasBeenDeleted = fileToDelete.delete()
        if (!hasBeenDeleted) {
            logger.warn("Failed to delete cover file with id $id.")
        }
    }

    suspend fun saveCover(username: String, file: MultiPartData): Boolean {
        var fileId: UUID? = null
        file.forEachPart { part ->
            when(part) {
                is PartData.FileItem -> {
                    val fileExtension = FileUtils.getFileExtension(
                        fileName = part.originalFileName.orEmpty()
                    )
                    if (
                        !FileUtils.isImageFile(part = part) ||
                        fileExtension == null
                    ) {
                        return@forEachPart
                    }

                    val fileBytes = part.streamProvider().readBytes()
                    if (fileBytes.isEmpty()) return@forEachPart

                    if (fileId == null) {
                        fileId = UUID.randomUUID()
                    }

                    val filename = "$fileId.$fileExtension"
                    val filepath = "${getCoversDirectory(username)}/$filename"

                    val fileToSave = File(filepath)

                    fileToSave.parentFile?.mkdirs()
                    fileToSave.writeBytes(fileBytes)
                }
                else -> {
                    /*no-op*/
                }
            }
            part.dispose()
        }

        return fileId != null
    }

    companion object {
        private const val COVERS_FOLDER = "covers"
    }
}