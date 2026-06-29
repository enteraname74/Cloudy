package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.repository.CoverRepository
import com.github.enteraname74.cloudy.fileaccess.CoverFileManager
import com.github.enteraname74.cloudy.metadata.filemetadata.MusicFileMetadataManager
import java.io.File
import kotlin.uuid.Uuid

class CoverRepositoryImpl(
    private val musicFileMetadataManager: MusicFileMetadataManager,
    private val coverFileManager: CoverFileManager,
): CoverRepository {
    override suspend fun getMusicFileCover(musicFile: File): ByteArray? =
        musicFileMetadataManager.getMusicFileCover(musicFile)

    override suspend fun getCover(name: String, username: String): ByteArray? =
        coverFileManager.getByName(
            name = name,
            username = username,
        )?.readBytes()

    override suspend fun save(
        username: String,
        coverData: FileData,
    ): Uuid =
        coverFileManager.save(
            username = username,
            fileData = coverData,
        )
}