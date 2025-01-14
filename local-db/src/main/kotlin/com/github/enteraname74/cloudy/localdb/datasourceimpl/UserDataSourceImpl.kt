package com.github.enteraname74.cloudy.localdb.datasourceimpl

import com.github.enteraname74.cloudy.domain.model.User
import com.github.enteraname74.cloudy.localdb.table.UserTable
import com.github.enteraname74.cloudy.localdb.table.toUser
import com.github.enteraname74.cloudy.localdb.util.suspendedTransaction
import com.github.enteraname74.cloudy.repository.datasource.UserDataSource
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.upsert

class UserDataSourceImpl: UserDataSource {
    override suspend fun getFromUsername(username: String): User? =
        suspendedTransaction {
            UserTable
                .selectAll()
                .where { UserTable.username eq username }
                .firstOrNull()?.toUser()
        }

    override suspend fun upsert(user: User): User =
        suspendedTransaction {
            UserTable.upsert {
                it[id] = user.id
                it[username] = user.username
                it[hashedPassword] = user.hashedPassword.hash
                it[salt] = user.hashedPassword.salt
                it[isAdmin] = user.isAdmin
            }

            UserTable
                .selectAll()
                .where { UserTable.id eq user.id }
                .first()
                .toUser()!!
        }

    override suspend fun getAll(): List<User> =
        suspendedTransaction {
            UserTable
                .selectAll()
                .mapNotNull { it.toUser() }
        }
}