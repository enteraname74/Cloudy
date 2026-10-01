package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.FileData
import kotlin.uuid.Uuid

interface CoverDataSource {
    suspend fun get(userId: Uuid, name: String): ByteArray?
    suspend fun save(userId: Uuid, data: FileData): Uuid?
    suspend fun delete(userId: Uuid, name: String)

    suspend fun deleteAll(userId: Uuid, names: List<String>)

    suspend fun getAllNames(userId: Uuid): List<String>
}