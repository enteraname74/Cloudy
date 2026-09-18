package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import java.io.File
import kotlin.uuid.Uuid

interface CoverRepository {
    /**
     * Retrieves the cover of a music file
     */
    suspend fun getMusicFileCover(musicFile: File): ByteArray?

    /**
     * Retrieves a cover saved on the file system
     */
    suspend fun getCover(
        userId: Uuid,
        name: String,
    ): ByteArray?

    suspend fun save(
        userId: Uuid,
        coverData: FileData,
    ): Uuid?

    suspend fun deletedUnusedCovers(userId: Uuid)
}