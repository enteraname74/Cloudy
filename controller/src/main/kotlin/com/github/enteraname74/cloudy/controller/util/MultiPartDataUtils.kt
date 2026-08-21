package com.github.enteraname74.cloudy.controller.util

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdatePayload
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdateSpec
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadPayload
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
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

    private suspend fun retrieveMusicData(fileItem: PartData.FileItem): FileData? {
        val fileExtension = FileUtils.getFileExtension(
            fileName = fileItem.originalFileName.orEmpty()
        )

        if (
            !FileUtils.isMusicFile(part = fileItem) ||
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

    suspend fun processMusicUploadRequest(request: MultiPartData): CloudyResult<MusicUploadPayload> {
        var musicFile: FileData? = null
        var musicUploadSpec: MusicUploadSpec? = null
        var cover: FileData? = null
        request.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    musicUploadSpec = CloudyJson.decodeFromString(part.value)
                }

                is PartData.FileItem -> {
                    if (musicFile == null) {
                        musicFile = retrieveMusicData(part)
                    }
                    if (cover == null) {
                        cover = retrieveImageData(part)
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        return if (musicFile != null && musicUploadSpec != null) {
            CloudyResult.Success(
                MusicUploadPayload(
                    musicFile = musicFile!!,
                    musicCover = cover,
                    spec = musicUploadSpec!!,
                )
            )
        } else {
            CloudyResult.Error()
        }
    }

    suspend fun processMusicUpdateRequest(request: MultiPartData): CloudyResult<MusicUpdatePayload> {
        var musicUpdateSpec: MusicUpdateSpec? = null
        var cover: FileData? = null
        request.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    musicUpdateSpec = CloudyJson.decodeFromString(part.value)
                }

                is PartData.FileItem -> {
                    if (cover == null) {
                        cover = retrieveImageData(part)
                    }
                }

                else -> {}
            }
            part.dispose()
        }

        return if (musicUpdateSpec != null) {
            CloudyResult.Success(
                MusicUpdatePayload(
                    musicCover = cover,
                    spec = musicUpdateSpec!!,
                )
            )
        } else {
            CloudyResult.Error()
        }
    }
}