package com.github.enteraname74.cloudy.domain.service

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.auth.HashedPasswordManager
import com.github.enteraname74.cloudy.domain.ext.roundToTwoDecimals
import com.github.enteraname74.cloudy.domain.ext.toGb
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserInscriptionCode
import com.github.enteraname74.cloudy.domain.model.user.UserStorage
import com.github.enteraname74.cloudy.domain.model.user.UserType
import com.github.enteraname74.cloudy.domain.repository.PlayerRepository
import com.github.enteraname74.cloudy.domain.repository.UserInscriptionCodeRepository
import com.github.enteraname74.cloudy.domain.repository.UserRepository
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.usecase.DeleteUserDataUseCase
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import kotlin.uuid.Uuid

class UserService(
    private val userRepository: UserRepository,
    private val userInscriptionCodeRepository: UserInscriptionCodeRepository,
    private val hashedPasswordManager: HashedPasswordManager,
    private val playerRepository: PlayerRepository,
    private val deleteUserDataUseCase: DeleteUserDataUseCase,
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
        type: UserType,
    ): CloudyResult<User> {
        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = password,
        ) ?: return CloudyResult.Error()

        val user = User(
            username = username,
            hashedPassword = hashedPassword,
            id = Uuid.random(),
            type = type,
        )

        val savedUser: User = userRepository.upsert(user = user)

        return CloudyResult.Success(data = savedUser)
    }

    suspend fun createUserWithInscriptionCode(
        username: String,
        password: String,
        type: UserType,
        inscriptionCode: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<User> {
        val hashedPassword: HashedPassword = hashedPasswordManager.buildHashedPassword(
            password = password,
        ) ?: return CloudyResult.Error(routingMessages.CANNOT_CREATE_USER)

        val user = User(
            username = username,
            hashedPassword = hashedPassword,
            id = Uuid.random(),
            type = type,
        )

        return userRepository.createWithInscriptionCode(
            user = user,
            inscriptionCode = inscriptionCode,
            routingMessages = routingMessages,
        )
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
        // TODO: broadcast deleted played lists or updated played lists because of user deletion
        playerRepository.deleteAllIfEmpty()
    }

    /**
     * Deletes user data, without deleting its account.
     * It will delete its data in the database and clear (without deleting) its folder.
     */
    suspend fun clearUserData(
        userId: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<UserStorage> {
        val user = userRepository.getFromId(userId = userId) ?: return CloudyResult.Error(
            message = routingMessages.CANNOT_FIND_USER,
        )
        deleteUserDataUseCase(userId)
        return CloudyResult.Success(getUserStorage(user.username))
    }

    suspend fun isUserDirectoryFull(
        username: String,
        addedSize: Long = 0L,
    ): Boolean {
        val userDirectorySize = userRepository.getUserDirectorySize(username)
        val gbSize = (userDirectorySize + addedSize).toGb()

        return gbSize >= userRepository.getUserDirectoryMaxSizeInGb(username).total
    }

    suspend fun getUserStorage(
        username: String,
    ): UserStorage {
        val max = userRepository.getUserDirectoryMaxSizeInGb(username)

        return UserStorage(
            max = max.copyData(total = max.total.roundToTwoDecimals()),
            current = userRepository.getUserDirectorySize(username).toGb().roundToTwoDecimals(),
        )
    }

    suspend fun canDeleteUser(
        requester: Uuid,
        userIdToDelete: Uuid,
    ): Boolean {
        val userRequester: User = userRepository.getFromId(userId = requester) ?: return false
        val userToDelete: User = userRepository.getFromId(userId = userIdToDelete) ?: return false

        return (requester == userIdToDelete) || (userRequester.isAdmin && !userToDelete.isAdmin)
    }

    suspend fun generateCode(
        userId: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<UserInscriptionCode> {
        val user: User = userRepository.getFromId(userId) ?: return CloudyResult.Error(routingMessages.CANNOT_FIND_USER)

        if (!user.isAdmin) {
            return CloudyResult.Error(routingMessages.NOT_AN_ADMIN)
        }

        return CloudyResult.Success(
            data = userInscriptionCodeRepository.generate(userId = userId)
        )
    }

    suspend fun getAllCodesOfUser(
        userId: Uuid,
    ): List<UserInscriptionCode> =
        userInscriptionCodeRepository.allOfUser(userId = userId)

    suspend fun deleteCode(
        userId: Uuid,
        code: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<Unit> {
        val code: UserInscriptionCode = userInscriptionCodeRepository.getFromCode(code = code)
            ?.takeIf { it.ownerId == userId } ?: return CloudyResult.Error(routingMessages.INSCRIPTION_CODE_NOT_FOUND)

        userInscriptionCodeRepository.delete(code = code.code)

        return CloudyResult.Success(Unit)
    }
}
