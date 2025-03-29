package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import java.io.File
import java.util.*

class CoverService(
    private val coverRepository: CoverRepository,
) {
    suspend fun getMusicFileCover(file: File): ByteArray? =
        coverRepository.getMusicFileCover(file)

    suspend fun getById(id: UUID, username: String): ByteArray? =
        coverRepository.getCover(
            id = id,
            username = username,
        )
}