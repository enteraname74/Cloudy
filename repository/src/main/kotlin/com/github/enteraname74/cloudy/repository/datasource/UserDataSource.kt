package com.github.enteraname74.cloudy.repository.datasource

import com.github.enteraname74.cloudy.domain.model.User

interface UserDataSource {
    suspend fun getFromUsername(username: String): User?
    suspend fun upsert(user: User): User
}