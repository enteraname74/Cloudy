package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.MusicArtist
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

/**
 * Table for storing MusicArtists.
 */
internal object MusicArtistTable: Table() {
    val id = varchar("id", 256)
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val artistId = reference("artistId", ArtistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = datetime("lastUpdateAt").default(LocalDateTime.now())

    override val primaryKey: PrimaryKey? = PrimaryKey(id, name = "PK_MusicArtist_id")
}

internal fun ResultRow.toMusicArtist(): MusicArtist? =
    try {
        MusicArtist(
            musicId = this[MusicArtistTable.musicId].value,
            artistId = this[MusicArtistTable.artistId].value,
            userId = this[MusicArtistTable.userId].value,
            lastUpdateAt = this[MusicArtistTable.lastUpdateAt],
        )
    } catch (_: Exception) {
        null
    }