package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.filepersistence.CoverFilePersistenceManager
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import io.ktor.http.content.MultiPartData
import java.io.File
import java.util.UUID

class CoverService(
    private val coverRepository: CoverRepository,
    private val coverFilePersistenceManager: CoverFilePersistenceManager,
) {
    suspend fun getMusicFileCover(file: File): ByteArray? =
        coverRepository.getMusicFileCover(file)

    fun getById(id: UUID, username: String): ByteArray? =
        coverFilePersistenceManager.getById(
            id = id,
            username = username,
        )?.readBytes()

    suspend fun save(username: String, file: MultiPartData): Boolean =
        coverFilePersistenceManager.saveCover(
            username = username,
            file = file,
        )
}