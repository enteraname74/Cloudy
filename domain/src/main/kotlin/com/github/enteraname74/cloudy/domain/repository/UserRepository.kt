package com.github.enteraname74.cloudy.domain.repository

import com.github.enteraname74.cloudy.domain.model.User

interface UserRepository {
    suspend fun getFromUsername(username: String): User?
    suspend fun upsert(user: User): User
}