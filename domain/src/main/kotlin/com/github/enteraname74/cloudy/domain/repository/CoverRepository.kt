package com.github.enteraname74.cloudy.domain.repository

import java.io.File

interface CoverRepository {
    suspend fun getMusicFileCover(musicFile: File): ByteArray?
}