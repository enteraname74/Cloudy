package com.github.enteraname74.cloudy.localdb.table.player

import com.github.enteraname74.cloudy.domain.model.player.PlayerUser
import com.github.enteraname74.cloudy.domain.util.DateUtils
import com.github.enteraname74.cloudy.localdb.table.UserEntity
import com.github.enteraname74.cloudy.localdb.table.UserTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.insert
import kotlin.uuid.Uuid

internal object PlayedListUserTable: IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val listId = reference("listId", PlayedListTable.id, onDelete = ReferenceOption.CASCADE)
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val deviceId = text("deviceId")
    val joinedAt = long("joinedAt")

    fun insert(
        userId: Uuid,
        deviceId: String,
        listId: Uuid
    ) {
        this.insert {
            it[id] = "$listId-$userId-$deviceId"
            it[this.listId] = listId
            it[this.deviceId] = deviceId
            it[this.userId] = userId
            it[joinedAt] = DateUtils.now()
        }
    }
}

internal class PlayedListUserEntity(id: EntityID<String>): Entity<String>(id) {
    companion object : EntityClass<String, PlayedListUserEntity>(PlayedListUserTable)

    val user by UserEntity referencedOn PlayedListUserTable.userId
    var deviceId by PlayedListUserTable.deviceId
    var listId by PlayedListUserTable.listId
    var joinedAt by PlayedListUserTable.joinedAt

    fun toPlayerUser(): PlayerUser =
        PlayerUser(
            id = user.id.value,
            deviceId = deviceId,
            username = user.username,
            joinedAt = joinedAt,
        )
}