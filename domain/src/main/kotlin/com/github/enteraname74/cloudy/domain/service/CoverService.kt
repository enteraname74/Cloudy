package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import java.io.File
import kotlin.uuid.Uuid

class CoverService(
    private val coverRepository: CoverRepository,
) {
    suspend fun getMusicFileCover(file: File): ByteArray? =
        coverRepository.getMusicFileCover(file)

    suspend fun getByName(name: String, userId: Uuid): ByteArray? =
        coverRepository.getCover(
            name = name,
            userId = userId,
        )
}