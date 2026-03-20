package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.User
import kotlin.uuid.Uuid

interface UserDataSource {
    suspend fun getFromUsername(username: String): User?
    suspend fun getFromId(userId: Uuid): User?
    suspend fun upsert(user: User): User
    suspend fun delete(id: Uuid)
    suspend fun getAll(): List<User>
}