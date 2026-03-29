package com.github.enteraname74.cloudy.localdb.table.player

import com.github.enteraname74.cloudy.domain.model.player.PlayedList
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

internal object PlayedListTable : UuidTable() {
    val inviteCode = text("inviteCode")
    val state = text("state")
    val lastUpdateAt = long("lastUpdateAt").default(DateUtils.now())
}

internal class PlayedListEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<PlayedListEntity>(PlayedListTable)

    var inviteCode by PlayedListTable.inviteCode
    var lastUpdateAt by PlayedListTable.lastUpdateAt
    var state by PlayedListTable.state
    val users by PlayedListUserEntity referrersOn PlayedListUserTable.listId

    fun toPlayedList(): PlayedList {
        val simpleUsers = users.map { it.toSimpleUser() }.sortedBy { it.joinedAt }

        return PlayedList(
            id = id.value,
            inviteCode = inviteCode,
            state = PlayedList.State.fromValueOrPaused(state),
            owner = simpleUsers.firstOrNull(),
            users = simpleUsers,
        )
    }
}