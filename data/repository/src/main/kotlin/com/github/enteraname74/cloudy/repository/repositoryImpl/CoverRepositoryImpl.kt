package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import java.io.File
import java.util.*

class CoverRepositoryImpl(
    private val musicFileMetadataManager: MusicFileMetadataManager,
    private val coverFileManager: CoverFileManager,
): CoverRepository {
    override suspend fun getMusicFileCover(musicFile: File): ByteArray? =
        musicFileMetadataManager.getMusicFileCover(musicFile)

    override suspend fun getCover(id: UUID, username: String): ByteArray? =
        coverFileManager.getById(
            id = id,
            username = username,
        )?.readBytes()

    override suspend fun save(
        username: String,
        coverData: FileData,
    ): UUID =
        coverFileManager.save(
            username = username,
            fileData = coverData,
        )
}