package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object MusicPlaylistTable: Table() {
    val id = varchar("id", 256)
    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = datetime("lastUpdateAt").default(LocalDateTime.now())

    override val primaryKey: PrimaryKey? = PrimaryKey(MusicArtistTable.id, name = "PK_MusicPlaylist_id")
}

internal fun ResultRow.toMusicPlaylist(): MusicPlaylist? =
    try {
        MusicPlaylist(
            musicId = this[MusicPlaylistTable.musicId].value,
            playlistId = this[MusicPlaylistTable.playlistId].value,
            userId = this[MusicPlaylistTable.userId].value,
            lastUpdateAt = this[MusicPlaylistTable.lastUpdateAt],
        )
    } catch (_: Exception) {
        null
    }