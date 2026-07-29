package com.github.enteraname74.cloudy.localdb.table

import com.github.enteraname74.cloudy.domain.model.MusicPlaylist
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert

internal object MusicPlaylistTable : IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val userId = reference("userId", UserTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val playlistId = reference("playlistId", PlaylistTable.id, onDelete = ReferenceOption.CASCADE)
    val lastUpdateAt = long("lastUpdateAt")

    fun upsertAll(musicPlaylists: List<MusicPlaylist>) {
        batchUpsert(musicPlaylists) {
            this[id] = it.id
            this[userId] = it.userId
            this[musicId] = it.musicId
            this[playlistId] = it.playlistId
            this[lastUpdateAt] = it.lastUpdateAtMillis
        }
    }
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
