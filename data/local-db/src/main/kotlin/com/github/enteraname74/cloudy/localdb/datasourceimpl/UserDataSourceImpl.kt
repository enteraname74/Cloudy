package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.localdb.table.UserEntity
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.util.workTransaction
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.upsert
import kotlin.uuid.Uuid

class UserDataSourceImpl: UserDataSource {
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
                it[salt] = user.hashedPassword.salt
                it[isAdmin] = user.isAdmin
            }

            UserEntity.findById(user.id)!!.toUser()
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
}