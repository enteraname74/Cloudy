package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.ext.toGb
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserStorage
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.localdb.table.UserEntity
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeEntity
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.util.AppFileUtils
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.upsert
import java.io.File
import kotlin.uuid.Uuid

class UserDataSourceImpl : UserDataSource {
    override suspend fun getFromUsername(username: String): User? =
        workTransaction {
            UserEntity.find {
                UserTable.username eq username
            }.firstOrNull()?.toUser()
        }

    override suspend fun getFromId(userId: Uuid): User? =
        workTransaction {
            UserEntity.findById(userId)?.toUser()
        }

    override suspend fun upsert(user: User): User =
        workTransaction {
            UserTable.upsert {
                it[id] = user.id
                it[username] = user.username
                it[hashedPassword] = user.hashedPassword.hash
                it[type] = user.type
            }

            UserEntity.findById(user.id)!!.toUser()
        }

    override suspend fun createWithInscriptionCode(
        user: User,
        inscriptionCode: Uuid,
        routingMessages: RoutingMessages,
    ): CloudyResult<User> =
        try {
            workTransaction {
                // TODO V2: Maybe don't block single username as it is no longer used for user folder
                if (isUsernameAlreadyUsed(username = user.username)) {
                    return@workTransaction CloudyResult.Error(
                        message = routingMessages.USERNAME_TAKEN,
                    )
                }

                if (UserInscriptionCodeEntity.findById(inscriptionCode) == null) {
                    return@workTransaction CloudyResult.Error(
                        message = routingMessages.INVALID_INSCRIPTION_CODE,
                    )
                }

                UserTable.insert {
                    it[id] = user.id
                    it[username] = user.username
                    it[hashedPassword] = user.hashedPassword.hash
                    it[type] = user.type
                }

                val deletedCodeCount = UserInscriptionCodeTable.deleteWhere {
                    UserInscriptionCodeTable.id eq inscriptionCode
                }
                check(deletedCodeCount == 1)

                CloudyResult.Success(
                    data = UserEntity.findById(user.id)!!.toUser()
                )
            }
        } catch (_: Exception) {
            when {
                getFromUsername(username = user.username) != null -> CloudyResult.Error(
                    message = routingMessages.USERNAME_TAKEN,
                )
                !isInscriptionCodeExisting(inscriptionCode) -> CloudyResult.Error(
                    message = routingMessages.INVALID_INSCRIPTION_CODE,
                )
                else -> CloudyResult.Error(
                    message = routingMessages.CANNOT_CREATE_USER,
                )
            }
        }

    override suspend fun delete(id: Uuid) {
        workTransaction {
            UserEntity.findById(id)?.delete()
            getUserDirectory(id).deleteRecursively()
        }
    }

    override suspend fun getAll(): List<User> =
        workTransaction {
            UserEntity.all().map { it.toUser() }
        }

    override suspend fun getUserDirectorySize(userId: Uuid): Long {
        val folder = getUserDirectory(userId)

        return runCatching {
            folder
                .walkTopDown()
                .filter { it.isFile }
                .sumOf { it.length() }
        }.getOrNull() ?: 0L
    }

    override suspend fun getUserDirectoryMaxSizeInGb(userId: Uuid): UserStorage.StorageType {
        val defaultMaxSize = UserStorage.StorageType.Reduced(
            (System.getenv("TOTAL_SPACE_PER_FOLDER")?.toLongOrNull() ?: 10L).toDouble()
        )
        val user = getFromId(userId) ?: return defaultMaxSize

        return if (user.isAdmin) {
            UserStorage.StorageType.AllAvailable(
                total = getUserDirectory(userId).usableSpace.toGb(),
            )
        } else {
            defaultMaxSize
        }
    }

    override suspend fun clearUserFolder(userId: Uuid) {
        val directory = AppFileUtils.get(userId.toString())

        if (!directory.exists() || !directory.isDirectory) {
            return
        }

        directory.listFiles()?.all { child -> child.deleteRecursively() }
    }

    override suspend fun getUserDirectory(userId: Uuid): File =
        AppFileUtils.ensureFolderExist(userId.toString())

    private fun isUsernameAlreadyUsed(username: String): Boolean =
        !UserEntity
            .find { UserTable.username eq username }
            .empty()

    private suspend fun isInscriptionCodeExisting(inscriptionCode: Uuid): Boolean =
        workTransaction {
            UserInscriptionCodeEntity.findById(inscriptionCode) != null
        }
}
