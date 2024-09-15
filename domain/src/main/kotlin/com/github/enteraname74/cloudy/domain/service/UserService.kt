package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.util.ServiceResult

class UserService(
    private val userRepository: UserRepository,
    private val hashedPasswordManager: HashedPasswordManager
) {
    suspend fun isUsernameUsed(username: String): Boolean =
        userRepository.getFromUsername(username = username) != null

    suspend fun getUserFromUsername(username: String): User? =
        userRepository.getFromUsername(username = username)

    suspend fun createUser(username: String, password: String): ServiceResult {
        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = password,
        ) ?: return ServiceResult.Error()

        val user = User(
            username = username,
            hashedPassword = hashedPassword,
        )

        val savedUser: User = userRepository.upsert(user = user)

        return ServiceResult.Ok(data = savedUser)
    }

    suspend fun logUser(username: String, password: String): ServiceResult {
        val user: User = userRepository.getFromUsername(username = username) ?: return ServiceResult.Error()
        val isPasswordMatching = hashedPasswordManager.isMatching(
            password = password,
            hashedPassword = user.hashedPassword,
        )

        return if (isPasswordMatching) {
            ServiceResult.Ok(data = user)
        } else {
            ServiceResult.Error()
        }
    }
}