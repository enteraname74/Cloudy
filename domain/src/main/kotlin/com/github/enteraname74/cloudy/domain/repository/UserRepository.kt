package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun getFromUsername(username: String): User?
    suspend fun getFromId(userId: UUID): User?
    suspend fun upsert(user: User): User
    suspend fun delete(id: UUID)
    suspend fun getAll(): List<User>
}