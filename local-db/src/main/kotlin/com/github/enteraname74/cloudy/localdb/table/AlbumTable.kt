package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.Album
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.javatime.datetime

internal object AlbumTable: UUIDTable() {
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 128)
    val coverPath = varchar("coverPath", 128).nullable()
    val addedDate = datetime("addedDate")
    val nbPlayed = integer("nbPlayed")
    val isInQuickAccess = bool("isInQuickAccess")
    val artistId = reference("artistId", ArtistTable.id, ReferenceOption.CASCADE)
    val artistName = varchar("artistName", 128).default("")
}

internal fun ResultRow.toAlbum(): Album? =
    try {
        Album(
            id = this[AlbumTable.id].value,
            name = this[AlbumTable.name],
            coverPath = this[AlbumTable.coverPath],
            addedDate = this[AlbumTable.addedDate],
            nbPlayed = this[AlbumTable.nbPlayed],
            isInQuickAccess = this[AlbumTable.isInQuickAccess],
            userId = this[AlbumTable.userId].value,
            artistId = this[AlbumTable.artistId].value,
            artistName = this[AlbumTable.artistName],
        )
    } catch (_: Exception) {
        null
    }