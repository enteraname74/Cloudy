package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.fileaccess.MusicFileManager
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import kotlin.uuid.Uuid

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val musicFileManager: MusicFileManager,
): UserRepository {
    override suspend fun getFromUsername(username: String): User? =
        userDataSource.getFromUsername(username = username)

    override suspend fun getFromId(userId: Uuid): User? =
        userDataSource.getFromId(userId = userId)

    override suspend fun upsert(user: User): User =
        userDataSource.upsert(user = user)

    override suspend fun delete(id: Uuid) {
        userDataSource.delete(id = id)
    }

    override suspend fun getAll(): List<User> =
        userDataSource.getAll()

    override suspend fun getUserDirectorySize(username: String): Long =
        musicFileManager.getUserDirectorySize(username)
}