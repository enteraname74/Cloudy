package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserStorage
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import java.io.File
import kotlin.uuid.Uuid

interface UserDataSource {
    suspend fun getFromUsername(username: String): User?
    suspend fun getFromId(userId: Uuid): User?
    suspend fun upsert(user: User): User
    suspend fun createWithInscriptionCode(
        user: User,
        inscriptionCode: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<User>

    suspend fun delete(id: Uuid)
    suspend fun getAll(): List<User>

    suspend fun getUserDirectorySize(userId: Uuid): Long

    suspend fun getUserDirectoryMaxSizeInGb(userId: Uuid): UserStorage.StorageType

    /**
     * Clear the content of the user's folder, but don't delete it
     */
    suspend fun clearUserFolder(userId: Uuid)

    suspend fun getUserDirectory(userId: Uuid): File
}
