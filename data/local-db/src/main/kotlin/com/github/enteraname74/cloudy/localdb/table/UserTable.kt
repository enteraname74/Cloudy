package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.auth.HashedPassword
import com.github.enteraname74.cloudy.domain.model.User
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ResultRow

internal object UserTable : UUIDTable() {
    val username = varchar("pseudo", 128)
    val hashedPassword = binary("hashedPassword")
    val salt = binary("salt")
    val isAdmin = bool("isAdmin")
}

internal fun ResultRow.toUser(): User? =
    try {
        User(
            id = this[UserTable.id].value,
            username = this[UserTable.username],
            isAdmin = this[UserTable.isAdmin],
            hashedPassword = HashedPassword(
                salt = this[UserTable.salt],
                hash = this[UserTable.hashedPassword]
            )
        )
    } catch (_: Exception) {
        null
    }