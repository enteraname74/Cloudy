package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.model.user.User
import com.github.enteraname74.cloudy.domain.model.user.UserType
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

internal object UserTable : UuidTable() {
    val username = varchar("pseudo", 128)
    val hashedPassword = text("hashedPassword")
    val type = enumeration<UserType>("type")
}

internal class UserEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<UserEntity>(UserTable)

    val username by UserTable.username
    val hashedPassword by UserTable.hashedPassword
    val type by UserTable.type

    fun toUser(): User =
        User(
            id = id.value,
            username = username,
            hashedPassword = HashedPassword(
                hash = hashedPassword,
            ),
            type = type,
        )
}
