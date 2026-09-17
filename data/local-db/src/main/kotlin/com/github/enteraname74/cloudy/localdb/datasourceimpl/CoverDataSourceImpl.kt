package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.ext.ensureExist
import com.github.enteraname74.cloudy.domain.model.FileData
import com.github.enteraname74.cloudy.domain.util.CommonFileUtils
import com.github.enteraname74.cloudy.repository.datasource.CoverDataSource
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import java.io.File
import kotlin.uuid.Uuid

class CoverDataSourceImpl(
    private val userDataSource: UserDataSource,
) : CoverDataSource {
    private suspend fun getCoverFolder(userId: Uuid): File {
        val userFolder = userDataSource.getUserDirectory(userId)
        return File(userFolder, COVERS_FOLDER).ensureExist()
    }

    override suspend fun get(userId: Uuid, name: String): ByteArray? =
        CommonFileUtils.getByNameWithoutExtension(
            parent = getCoverFolder(userId),
            name = name
        )?.readBytes()

    override suspend fun save(userId: Uuid, data: FileData): Uuid? =
        CommonFileUtils.save(
            parent = getCoverFolder(userId),
            fileData = data,
        )

    override suspend fun delete(userId: Uuid, name: String) {
        CommonFileUtils.delete(
            parent = getCoverFolder(userId),
            name = name
        )
    }

    override suspend fun deleteAll(userId: Uuid, names: List<String>) {
        val coverFolder = getCoverFolder(userId)
        names.forEach {
            CommonFileUtils.delete(
                parent = coverFolder,
                name = it,
            )
        }
    }

    override suspend fun getAllNames(userId: Uuid): List<String> =
        CommonFileUtils.getAllNamesWithoutExtension(
            folder = getCoverFolder(userId),
        )

    companion object {
        private const val COVERS_FOLDER = "covers"
    }
}