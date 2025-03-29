package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.metadata.cover.LocalCoverRetriever
import java.io.File

class CoverRepositoryImpl(
    private val localCoverRetriever: LocalCoverRetriever,
): CoverRepository {
    override suspend fun getMusicFileCover(musicFile: File): ByteArray? =
        localCoverRetriever.getMusicFileCover(musicFile)
}