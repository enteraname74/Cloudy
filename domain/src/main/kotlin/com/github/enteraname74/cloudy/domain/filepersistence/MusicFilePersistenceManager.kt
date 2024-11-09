package com.github.enteraname74.cloudy.domain.filepersistence

import com.github.enteraname74.cloudy.domain.util.FileUtils
import io.ktor.http.content.*
import java.io.File
import java.util.*

class MusicFilePersistenceManager {

    private fun getMusicIdFromFileName(fileName: String): UUID =
        UUID.fromString(
            fileName.replaceFirst(
                regex = """[.][^.]+$""".toRegex(),
                replacement = ""
            )
        )

    private fun getUserDirectory(username: String): String =
        "$MUSIC_FOLDER/$username"

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

    /**
     * Saves a file and return its id.
     */
    suspend fun saveFile(username: String, file: MultiPartData): UUID? {
        var fileId: UUID? = null
        file.forEachPart { part ->
            println("UPLOAD -- MFP -- got part to analyze")
            when(part) {
                is PartData.FormItem -> {}
                is PartData.FileItem -> {
                    val fileExtension = FileUtils.getFileExtension(
                        fileName = part.originalFileName.orEmpty()
                    )

                    if (
                        !FileUtils.isMusicFile(part = part) ||
                        fileExtension == null
                        ) {
                        println("UPLOAD -- MFP -- the given file is not a music file")
                        return@forEachPart
                    }

                    val fileBytes = part.streamProvider().readBytes()
                    if (fileBytes.isEmpty()) return@forEachPart

                    println("UPLOAD -- MFP -- file is not empty, will analyze")

                    if (fileId == null) {
                        fileId = UUID.randomUUID()
                    }

                    val filename = "$fileId.$fileExtension"
                    val filepath = "${getUserDirectory(username)}/$filename"

                    val fileToSave = File(filepath)

                    println("UPLOAD -- MFP -- will save file: $fileToSave")
                    fileToSave.parentFile?.mkdirs()
                    fileToSave.writeBytes(fileBytes)
                }
                else -> {}
            }
            part.dispose()
        }

        return fileId
    }

    fun getById(musicId: UUID, username: String): File? {
        val userDirectory = File(getUserDirectory(username = username))

        return userDirectory
            .listFiles()
            ?.filter { it.isFile }
            ?.firstOrNull { getMusicIdFromFileName(it.name) == musicId }
    }

    fun deleteFile(musicId: UUID, username: String) {
        val fileToDelete: File = getById(
            musicId = musicId,
            username = username,
        ) ?: return

        fileToDelete.delete()
    }


    companion object {
        private const val MUSIC_FOLDER = "app/musics"
    }
}