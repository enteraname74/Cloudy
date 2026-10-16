package com.github.enteraname74.cloudy.controller.util

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdatePayload
import com.github.enteraname74.cloudy.domain.model.music.MusicUpdateSpec
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadPayload
import com.github.enteraname74.cloudy.domain.model.music.MusicUploadSpec
import com.github.enteraname74.cloudy.domain.util.CloudyJson
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.domain.util.CommonFileUtils
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.utils.io.toByteArray

// TODO V2: Avoid loading big files into memory, use streams instead.
object MultiPartDataUtils {
    suspend fun retrieveImageData(fileItem: PartData.FileItem): FileData? {
        val fileExtension = CommonFileUtils.getFileExtension(
            fileName = fileItem.originalFileName.orEmpty()
        )
        if (
            !CommonFileUtils.isImageFile(fileItem) ||
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
        val fileExtension = CommonFileUtils.getFileExtension(
            fileName = fileItem.originalFileName.orEmpty()
        )

        if (
            !CommonFileUtils.isMusicFile(part = fileItem) ||
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

    suspend inline fun <reified T> processCoverUpdateRequest(
        request: MultiPartData,
    ): CloudyResult<Pair<List<FileData?>, T>> {
        val covers = MutableList<FileData?>(4) { null }
        val receivedIndices = mutableSetOf<Int>()
        var metadata: T? = null
        var isInvalid = false

        request.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if (part.name != "metadata" || metadata != null) {
                        isInvalid = true
                    } else {
                        metadata = runCatching { CloudyJson.decodeFromString<T>(part.value) }.getOrNull()
                        if (metadata == null) isInvalid = true
                    }
                }
                is PartData.FileItem -> {
                    val partName = part.name
                    val index = partName
                        ?.takeIf { it.startsWith("cover") }
                        ?.removePrefix("cover")
                        ?.toIntOrNull()
                    if (
                        index == null ||
                        partName != "cover$index" ||
                        index !in covers.indices ||
                        !receivedIndices.add(index)
                    ) {
                        isInvalid = true
                    } else {
                        covers[index] = retrieveImageData(part)
                        if (covers[index] == null) isInvalid = true
                    }
                }
                else -> isInvalid = true
            }
            part.dispose()
        }

        return if (!isInvalid && metadata != null) {
            CloudyResult.Success(covers to metadata!!)
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
