package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.model.User
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

internal object UserTable : UuidTable() {
    val username = varchar("pseudo", 128)
    val hashedPassword = binary("hashedPassword")
    val salt = binary("salt")
    val isAdmin = bool("isAdmin")
}

internal class UserEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<UserEntity>(UserTable)

    val username by UserTable.username
    val hashedPassword by UserTable.hashedPassword
    val salt by UserTable.salt
    val isAdmin by UserTable.isAdmin

    fun toUser(): User =
        User(
            id = id.value,
            username = username,
            hashedPassword = HashedPassword(
                salt = salt,
                hash = hashedPassword,
            ),
            isAdmin = isAdmin
        )
}