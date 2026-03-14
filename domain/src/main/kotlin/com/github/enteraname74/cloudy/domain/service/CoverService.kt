package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import java.io.File

class CoverService(
    private val coverRepository: CoverRepository,
) {
    suspend fun getMusicFileCover(file: File): ByteArray? =
        coverRepository.getMusicFileCover(file)

    suspend fun getByName(name: String, username: String): ByteArray? =
        coverRepository.getCover(
            name = name,
            username = username,
        )
}