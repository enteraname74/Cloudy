package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.routingmessages.RoutingMessages
import com.github.enteraname74.cloudy.domain.util.CloudyResult
import com.github.enteraname74.cloudy.localdb.table.UserEntity
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeEntity
import com.github.enteraname74.cloudy.localdb.table.UserInscriptionCodeTable
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.upsert
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

    // TODO DB: Maybe not returning the user in the upsert function
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
        }
    }

    override suspend fun getAll(): List<User> =
        workTransaction {
            UserEntity.all().map { it.toUser() }
        }

    private fun isUsernameAlreadyUsed(username: String): Boolean =
        !UserEntity
            .find { UserTable.username eq username }
            .empty()

    private suspend fun isInscriptionCodeExisting(inscriptionCode: Uuid): Boolean =
        workTransaction {
            UserInscriptionCodeEntity.findById(inscriptionCode) != null
        }
}
