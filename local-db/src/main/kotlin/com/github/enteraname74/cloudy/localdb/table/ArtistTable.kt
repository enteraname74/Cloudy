package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.Artist
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

internal object ArtistTable: UUIDTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 128)
    val coverPath = varchar("coverPath", 128).nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val lastUpdatedAt = datetime("lastUpdatedAt").default(LocalDateTime.now())
}

internal fun ResultRow.toArtist(): Artist? =
    try {
        Artist(
            id = this[ArtistTable.id].value,
            name = this[ArtistTable.name],
            coverPath = this[ArtistTable.coverPath],
            addedDate = this[ArtistTable.addedDate],
            nbPlayed = this[ArtistTable.nbPlayed],
            isInQuickAccess = this[ArtistTable.isInQuickAccess],
            userId = this[ArtistTable.userId].value,
            lastUpdateAt = this[ArtistTable.lastUpdatedAt],
        )
    } catch (_: Exception) {
        null
    }