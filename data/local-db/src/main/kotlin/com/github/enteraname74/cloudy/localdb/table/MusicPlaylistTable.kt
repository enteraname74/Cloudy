package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import com.github.enteraname74.cloudy.domain.util.DateUtils
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass

internal object MusicPlaylistTable : IdTable<String>() {
    // TODO: replace with text() as we cannot guarantee correct size
    override val id = varchar("id", 256).entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt").default(DateUtils.now())
}

internal class MusicPlaylistEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, MusicPlaylistEntity>(MusicPlaylistTable)

    var userId by MusicPlaylistTable.userId
    var musicId by MusicPlaylistTable.musicId
    var playlistId by MusicPlaylistTable.playlistId
    var lastUpdateAt by MusicPlaylistTable.lastUpdateAt

    fun toMusicPlaylist(): MusicPlaylist =
        MusicPlaylist(
            musicId = musicId.value,
            playlistId = playlistId.value,
            userId = userId.value,
            lastUpdateAtMillis = lastUpdateAt,
        )
}