package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import java.util.UUID

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
): UserRepository {
    override suspend fun getFromUsername(username: String): User? =
        userDataSource.getFromUsername(username = username)

    override suspend fun getFromId(userId: UUID): User? =
        userDataSource.getFromId(userId = userId)

    override suspend fun upsert(user: User): User =
        userDataSource.upsert(user = user)

    override suspend fun delete(id: UUID) {
        userDataSource.delete(id = id)
    }

    override suspend fun getAll(): List<User> =
        userDataSource.getAll()
}