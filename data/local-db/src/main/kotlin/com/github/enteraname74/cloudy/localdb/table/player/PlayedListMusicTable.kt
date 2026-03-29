package com.github.enteraname74.cloudy.localdb.table.player

import com.github.enteraname74.cloudy.domain.model.player.PlayerMusic
import com.github.enteraname74.cloudy.domain.model.player.SimplePlayerMusic
import com.github.enteraname74.cloudy.localdb.table.MusicEntity
import com.github.enteraname74.cloudy.localdb.table.MusicTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.jdbc.batchUpsert

internal object PlayedListMusicTable : IdTable<String>() {
    override val id = text("id").entityId()
    override val primaryKey = PrimaryKey(id)

    val listId = reference("listId", PlayedListTable.id, onDelete = ReferenceOption.CASCADE)
    val musicId = reference("musicId", MusicTable.id, onDelete = ReferenceOption.CASCADE)
    val lastPlayedMillis = long("lastPlayedMillis").nullable()
    val order = double("order")

    fun upsertAll(musics: List<PlayerMusic>) {
        batchUpsert(musics) { music ->
            this[id] = music.id
            this[listId] = music.playedListId
            this[musicId] = music.music.fingerprint
            this[lastPlayedMillis] = music.lastPlayedMillis
            this[order] = music.order
        }
    }

    fun upsertAllSimple(musics: List<SimplePlayerMusic>) {
        batchUpsert(musics) { music ->
            this[id] = music.id
            this[listId] = music.playedListId
            this[musicId] = music.musicId
            this[lastPlayedMillis] = music.lastPlayedMillis
            this[order] = music.order
        }
    }
}

internal class PlayedListMusicEntity(id: EntityID<String>): Entity<String>(id) {
    companion object: EntityClass<String, PlayedListMusicEntity>(PlayedListMusicTable)

    var listId by PlayedListMusicTable.listId
    val music by MusicEntity referencedOn PlayedListMusicTable.musicId
    var lastPlayedMillis by PlayedListMusicTable.lastPlayedMillis
    var order by PlayedListMusicTable.order

    fun toPlayerMusic(): PlayerMusic = PlayerMusic(
        playedListId = listId.value,
        music = music.toMusic(),
        order = order,
        lastPlayedMillis = lastPlayedMillis
    )
}