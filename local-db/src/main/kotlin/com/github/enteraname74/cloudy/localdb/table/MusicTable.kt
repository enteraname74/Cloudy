package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.Music
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

internal object MusicTable: UUIDTable() {
    val name = varchar("name", 128)
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val coverPath = varchar("coverPath", 128).nullable()
    val album = varchar("album", 128)
    val artist = varchar("artist", 128)
    val path = varchar("path", 255)
    val duration = long("duration")
    val addedDate = datetime("addedDate")
    val lastUpdateAt = datetime("lastUpdateAt").default(LocalDateTime.now())
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val albumId = reference("albumId", AlbumTable.id, ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, ReferenceOption.CASCADE)
    val fingerprint = varchar("fingerprint", 128).default("")
}

internal fun ResultRow.toMusic(): Music? =
    try {
        Music(
            id = this[MusicTable.id].value,
            name = this[MusicTable.name],
            coverPath = this[MusicTable.coverPath],
            album = this[MusicTable.album],
            artist = this[MusicTable.artist],
            duration = this[MusicTable.duration],
            addedDate = this[MusicTable.addedDate],
            nbPlayed = this[MusicTable.nbPlayed],
            isInQuickAccess = this[MusicTable.isInQuickAccess],
            albumId = this[MusicTable.albumId].value,
            artistId = this[MusicTable.artistId].value,
            userId = this[MusicTable.userId].value,
            fingerprint = this[MusicTable.fingerprint],
            path = this[MusicTable.path],
            lastUpdateAt = this[MusicTable.lastUpdateAt]
        )
    } catch (_: Exception) {
        null
    }