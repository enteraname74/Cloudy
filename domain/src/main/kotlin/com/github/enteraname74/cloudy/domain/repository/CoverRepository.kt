package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.FileData
import java.io.File
import java.util.UUID

interface CoverRepository {
    /**
     * Retrieves the cover of a music file
     */
    suspend fun getMusicFileCover(musicFile: File): ByteArray?

    /**
     * Retrieves a cover saved on the file system
     */
    suspend fun getCover(id: UUID, username: String): ByteArray?
    suspend fun save(
        username: String,
        coverData: FileData,
    ): UUID
}