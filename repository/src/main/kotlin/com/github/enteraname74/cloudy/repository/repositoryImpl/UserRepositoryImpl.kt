package com.github.enteraname74.cloudy.repository.repositoryImpl

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource

class UserRepositoryImpl(
    private val userDataSource: UserDataSource,
): UserRepository {
    override suspend fun getFromUsername(username: String): User? =
        userDataSource.getFromUsername(username = username)

    override suspend fun upsert(user: User): User =
        userDataSource.upsert(user = user)
}