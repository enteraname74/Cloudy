package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.user.UserInscriptionCode
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.jdbc.insert
import kotlin.uuid.Uuid

internal object UserInscriptionCodeTable : UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)

    fun insert(
        userId: Uuid,
        code: Uuid,
    ) {
        this.insert {
            it[id] = code
            it[UserInscriptionCodeTable.userId] = userId
        }
    }
}

internal class UserInscriptionCodeEntity(id: EntityID<Uuid>): UuidEntity(id) {
    companion object : UuidEntityClass<UserInscriptionCodeEntity>(UserInscriptionCodeTable)

    var userId by UserInscriptionCodeTable.userId

    fun toUserInscriptionCode(): UserInscriptionCode =
        UserInscriptionCode(
            ownerId = userId.value,
            code = id.value,
        )
}