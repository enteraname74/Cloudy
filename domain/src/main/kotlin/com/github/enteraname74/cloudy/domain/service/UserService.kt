package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.ext.toGb
import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import kotlin.uuid.Uuid

class UserService(
    private val userRepository: UserRepository,
    private val hashedPasswordManager: HashedPasswordManager
) {
    suspend fun isUsernameUsed(username: String): Boolean =
        userRepository.getFromUsername(username = username) != null

    suspend fun getUserFromUsername(username: String): User? =
        userRepository.getFromUsername(username = username)

    suspend fun getUserFromId(userId: Uuid): User? =
        userRepository.getFromId(userId = userId)

    suspend fun createUser(
        username: String,
        password: String,
        isAdmin: Boolean = false,
    ): CloudyResult<User> {
        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = password,
        ) ?: return CloudyResult.Error()

        val user = User(
            username = username,
            hashedPassword = hashedPassword,
            isAdmin = isAdmin,
            id = Uuid.random(),
        )

        val savedUser: User = userRepository.upsert(user = user)

        return CloudyResult.Success(data = savedUser)
    }

    suspend fun logUser(username: String, password: String): CloudyResult<User> {
        val user: User = userRepository.getFromUsername(username = username) ?: return CloudyResult.Error()

        val isPasswordMatching = hashedPasswordManager.isMatching(
            password = password,
            hashedPassword = user.hashedPassword,
        )

        return if (isPasswordMatching) {
            CloudyResult.Success(data = user)
        } else {
            CloudyResult.Error()
        }
    }

    suspend fun getAll(): List<User> {
        return userRepository.getAll()
    }

    suspend fun deleteUser(userId: Uuid) {
        userRepository.delete(id = userId)
    }

    suspend fun isUserDirectoryFull(
        username: String,
        addedSize: Long = 0L,
    ): Boolean {
        val userDirectorySize = userRepository.getUserDirectorySize(username)
        val gbSize = (userDirectorySize + addedSize).toGb()

        return gbSize >= MAX_USER_DIRECTORY_SIZE_IN_GB
    }

    suspend fun canDeleteUser(
        requester: Uuid,
        userIdToDelete: Uuid,
    ): Boolean {
        val userRequester: User = userRepository.getFromId(userId = requester) ?: return false
        val userToDelete: User = userRepository.getFromId(userId = userIdToDelete) ?: return false

        return (requester == userIdToDelete) || (userRequester.isAdmin && !userToDelete.isAdmin)
    }

    companion object {
        private val MAX_USER_DIRECTORY_SIZE_IN_GB = System.getenv("TOTAL_SPACE_PER_FOLDER")?.toIntOrNull() ?: 10
    }
}