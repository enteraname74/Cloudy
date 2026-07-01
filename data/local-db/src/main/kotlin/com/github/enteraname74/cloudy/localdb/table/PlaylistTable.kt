package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.playlist.Playlist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import kotlin.uuid.Uuid

internal object PlaylistTable: UuidTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = text("name")
    val isFavorite = bool("isFavorite")
    val nbPlayed = integer("nbPlayed")
    val coverPath = text("coverPath").nullable()
    val addedDate = long("addedDate")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = long("lastUpdateAt").default(DateUtils.now())
}

internal class PlaylistEntity(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<PlaylistEntity>(PlaylistTable)

    var userId by PlaylistTable.userId
    var name by PlaylistTable.name
    var isFavorite by PlaylistTable.isFavorite
    var nbPlayed by PlaylistTable.nbPlayed
    var coverPath by PlaylistTable.coverPath
    var addedDate by PlaylistTable.addedDate
    var isInQuickAccess by PlaylistTable.isInQuickAccess
    var lastUpdateAt by PlaylistTable.lastUpdateAt

    fun toPlaylist(): Playlist =
        Playlist(
            id = id.value,
            userId = userId.value,
            name = name,
            coverPath = coverPath,
            isFavorite = isFavorite,
            addedDateMillis = addedDate,
            nbPlayed = nbPlayed,
            isInQuickAccess = isInQuickAccess,
            lastUpdateAtMillis = lastUpdateAt,
        )
}
