package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.Playlist
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

internal object PlaylistTable: UUIDTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 128)
    val isFavorite = bool("isFavorite")
    val nbPlayed = integer("nbPlayed")
    val coverPath = text("coverPath").nullable()
    val addedDate = datetime("addedDate")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdateAt = datetime("lastUpdateAt").default(LocalDateTime.now())
}

internal fun ResultRow.toPlaylist(): Playlist? =
    try {
        Playlist(
            id = this[PlaylistTable.id].value,
            name = this[PlaylistTable.name],
            coverPath = this[PlaylistTable.coverPath],
            isInQuickAccess = this[PlaylistTable.isInQuickAccess],
            isFavorite = this[PlaylistTable.isFavorite],
            lastUpdateAt = this[PlaylistTable.lastUpdateAt],
            addedDate = this[PlaylistTable.addedDate],
            nbPlayed = this[PlaylistTable.nbPlayed],
            userId = this[PlaylistTable.userId].value,
        )
    } catch (_: Exception) {
        null
    }