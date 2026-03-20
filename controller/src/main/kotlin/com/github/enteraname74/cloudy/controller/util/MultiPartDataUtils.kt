package com.github.enteraname74.cloudy.controller.util

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.MusicUpload
import com.github.enteraname74.cloudy.domain.util.CloudyJson
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.FileUtils
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.utils.io.toByteArray

object MultiPartDataUtils {
    suspend fun retrieveImageData(fileItem: PartData.FileItem): FileData? {
        val fileExtension = FileUtils.getFileExtension(
            fileName = fileItem.originalFileName.orEmpty()
        )
        if (
            !FileUtils.isImageFile(fileItem) ||
            fileExtension == null
        ) {
            return null
        }

        val fileBytes = fileItem.provider().toByteArray()
        if (fileBytes.isEmpty()) return null

        return FileData(
            extension = fileExtension,
            data = fileBytes,
        )
    }

    suspend inline fun <reified T> processUpdateRequest(request: MultiPartData): CloudyResult<Pair<FileData?, T>> {
        var fileData: FileData? = null
        var modifiedMusic: T? = null
        request.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    modifiedMusic = CloudyJson.decodeFromString(part.value)
                }
                is PartData.FileItem -> {
                    fileData = retrieveImageData(fileItem = part)
                }

                else -> {
                    /*no-op*/
                }
            }
            part.dispose()
        }

        return if (modifiedMusic != null) {
            CloudyResult.Success(
                Pair(fileData, modifiedMusic!!)
            )
        } else {
            CloudyResult.Error()
        }
    }

    suspend fun processMusicUploadRequest(musicFile: MultiPartData): CloudyResult<Pair<FileData, MusicUpload>> {
        var fileData: FileData? = null
        var music: MusicUpload? = null
        musicFile.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    music = CloudyJson.decodeFromString(part.value)
                }

                is PartData.FileItem -> {
                    val fileExtension = FileUtils.getFileExtension(
                        fileName = part.originalFileName.orEmpty()
                    )

                    if (
                        !FileUtils.isMusicFile(part = part) ||
                        fileExtension == null
                    ) {
                        return@forEachPart
                    }

                    val fileBytes = part.provider().toByteArray()
                    if (fileBytes.isEmpty()) return@forEachPart

                    fileData = FileData(
                        extension = fileExtension,
                        data = fileBytes,
                    )
                }

                else -> {}
            }
            part.dispose()
        }

        return if (fileData != null && music != null) {
            CloudyResult.Success(
                Pair(fileData, music!!)
            )
        } else {
            CloudyResult.Error()
        }
    }
}