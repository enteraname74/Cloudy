package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import kotlin.uuid.Uuid

interface UserRepository {
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

    /**
     * Retrieves the size of the user directory, in GB
     */
    suspend fun getUserDirectorySize(username: String): Long
}
